package pl.mkapiczynski.dron.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import pl.mkapiczynski.dron.business.DroneService;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.domain.NDBDroneSession;
import pl.mkapiczynski.dron.helpers.HttpHelper;
import pl.mkapiczynski.dron.helpers.JsonDateDeserializer;
import pl.mkapiczynski.dron.helpers.ServerResponse;
import pl.mkapiczynski.dron.message.GetDroneSessionsResponse;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;

/**
 * Servlet implementation class PreferencesServlet
 */
@WebServlet("/getDroneSessions")
public class GetDroneSessionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(GetDroneSessionsServlet.class);

	@EJB
	DroneService droneService;

	public GetDroneSessionsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("GetDroneSessionsRequest received");
		String url = request.getRequestURL() + ("?") + request.getQueryString();
		Map<String, String> parameters = readRequestParameters(url);
		String droneId = parameters.get("droneId");

		log.info("GetDroneSessionsRequest request for droneId " + droneId);

		if (droneId != null) {
			log.info("Request for login " + droneId);
			Long droneIdLong = Long.parseLong(droneId);
			List<DroneSession> droneSessions = droneService.getDroneSessions(droneIdLong);
			List<NDBDroneSession> ndbDroneSessions = new ArrayList<>();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < droneSessions.size(); i++) {
				DroneSession currentIteratedDroneSession = droneSessions.get(i);
				NDBDroneSession ndbDroneSession = new NDBDroneSession();
				ndbDroneSession.setSessionId(currentIteratedDroneSession.getSessionId());
				ndbDroneSession.setDroneId(currentIteratedDroneSession.getDrone().getDroneId());
				ndbDroneSession.setDroneName(currentIteratedDroneSession.getDrone().getDroneName());
				String startDate = dateFormat.format(currentIteratedDroneSession.getSessionStarted());
				String endDate = dateFormat.format(currentIteratedDroneSession.getSessionEnded());
				ndbDroneSession.setSessionStarted(startDate);
				ndbDroneSession.setSessionEnded(endDate);
				ndbDroneSessions.add(ndbDroneSession);
			}
			GetDroneSessionsResponse getDroneSessionsResponse = new GetDroneSessionsResponse();
			getDroneSessionsResponse.setDroneSessions(ndbDroneSessions);
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
			Gson gson = gsonBuilder.create();
			String json = gson.toJson(getDroneSessionsResponse);
			HttpHelper.sendJSON(response, json);
		} else {
			HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("Unexpected POST request for GetDroneSessionsServlet");
		HttpHelper.setStatusOrError(response, ServerResponse.METHOD_NOT_ALLOWED);
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

}
