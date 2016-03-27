package pl.mkapiczynski.dron.servlets;

import java.io.IOException;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.mkapiczynski.dron.business.AdministrationService;
import pl.mkapiczynski.dron.domain.NDBUser;
import pl.mkapiczynski.dron.helpers.HttpHelper;
import pl.mkapiczynski.dron.helpers.ServerResponse;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;
import pl.mkapiczynski.dron.response.PreferencesResponse;

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("GetPreferencesRequest received");
		String url = request.getRequestURL() + ("?") + request.getQueryString();
		Map<String, String> parameters = HttpHelper.readRequestParameters(url);
		String login = parameters.get("login");

		log.info("getPreferences request for login " + login);

		if (login != null) {
			log.info("Request for login " + login);
			NDBUser user = administrationService.getNDBUserForLogin(login);
			if (user != null) {
				PreferencesResponse preferencesResponse = new PreferencesResponse();
				preferencesResponse.setLogin(user.getLogin());
				preferencesResponse.setAssignedDrones(user.getAssignedDrones());
				preferencesResponse.setTrackedDrones(user.getTrackedDrones());
				preferencesResponse.setVisualizedDrones(user.getVisualizedDrones());
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				String json = gson.toJson(preferencesResponse);
				HttpHelper.sendJSON(response, json);
			} else {
				HttpHelper.setStatusOrError(response, ServerResponse.INTERNAL_SERVER_ERROR);
			}
		} else {
			HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("SetPreferencesRequest received");
		String message = request.getParameter("message");
		SetPreferencesMessage setPreferencesMessage = decodeSetPreferencesMessage(message);
		if (setPreferencesMessage != null) {
			String login = setPreferencesMessage.getLogin();
			if (login != null) {
				log.info("Request for login " + login);
				boolean updateSuccess = administrationService.updateUserDronesPreferences(setPreferencesMessage);
				if (updateSuccess) {
					HttpHelper.setStatusOrError(response, ServerResponse.OK);
				} else {
					HttpHelper.setStatusOrError(response, ServerResponse.INTERNAL_SERVER_ERROR);
				}
			} else {
				log.info("Required field login is missing");
				HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
			}
		} else {
			log.info("SetPreferencesMessage can not be null!");
			HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
		}

	}

	private SetPreferencesMessage decodeSetPreferencesMessage(String jsonMessage) {
		Gson gson = new Gson();
		return gson.fromJson(jsonMessage, SetPreferencesMessage.class);
	}

}
