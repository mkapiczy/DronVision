package pl.mkapiczynski.dron.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.mkapiczynski.dron.business.AdministrationService;
import pl.mkapiczynski.dron.helpers.HttpHelper;
import pl.mkapiczynski.dron.helpers.ServerResponse;
import pl.mkapiczynski.dron.message.PreferencesResponse;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;

/**
 * Servlet implementation class PreferencesServlet
 */
@WebServlet("/preferences")
public class PreferencesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(PreferencesServlet.class);
	
	@EJB
	AdministrationService administrationService;
       
    
    public PreferencesServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("GetPreferencesRequest received");
		String url = request.getRequestURL()+("?")+request.getQueryString();
		Map<String, String> parameters = readRequestParameters(url);
		String login = parameters.get("login");
		
		log.info("getPreferences request for login " + login);
		
		if(login!=null){
			log.info("Request for login " + login);
			PreferencesResponse preferencesResponse = administrationService.getPreferencesForClient(login);
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();
			String json = gson.toJson(preferencesResponse);
			HttpHelper.sendJSON(response, json);
		} else{
			HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("SetPreferencesRequest received");
		String message = request.getParameter("message");
		SetPreferencesMessage setPreferencesMessage = decodeSetPreferencesMessage(message);
		if(setPreferencesMessage!=null){
			String login = setPreferencesMessage.getLogin();
			if(login!=null){
				log.info("Request for login " + login);
				boolean updateSuccess = administrationService.updateUserDronesPreferences(setPreferencesMessage);
				if(updateSuccess){
					HttpHelper.setStatusOrError(response, ServerResponse.OK);
				} else{
					HttpHelper.setStatusOrError(response, ServerResponse.INTERNAL_SERVER_ERROR);
				}
			} else{
				log.info("Required field login is missing");
				HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
			}
		} else{
			log.info("SetPreferencesMessage can not be null!");
			HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
		}
		
	}
	
	private Map<String, String> readRequestParameters(String url) {
		List<NameValuePair> params = new ArrayList<>();
		try {
			params = URLEncodedUtils.parse(new URI(url), "UTF-8");
		} catch (URISyntaxException e1) {
			log.info("Exception during parsing address url", e1);
		}
		Map<String, String> parameters = new HashMap<String, String>();
		for (NameValuePair param : params) {
			parameters.put(param.getName(), param.getValue());
		}
		return parameters;
	}
	
	private SetPreferencesMessage decodeSetPreferencesMessage(String jsonMessage) {
		Gson gson = new Gson();
		return gson.fromJson(jsonMessage, SetPreferencesMessage.class);

	}

}
