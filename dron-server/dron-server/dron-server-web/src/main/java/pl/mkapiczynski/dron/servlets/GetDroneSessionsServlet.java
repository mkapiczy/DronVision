package pl.mkapiczynski.dron.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.List;
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
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.domain.NDBDroneSession;
import pl.mkapiczynski.dron.helpers.HttpHelper;
import pl.mkapiczynski.dron.helpers.JsonDateDeserializer;
import pl.mkapiczynski.dron.helpers.ServerResponse;
import pl.mkapiczynski.dron.response.GetDroneSessionsResponse;

/**
 * Servlet implementation class PreferencesServlet
 */
@WebServlet("/getDroneSessions")
public class GetDroneSessionsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(GetDroneSessionsServlet.class);

	@EJB
	private AdministrationService administrationService;

	public GetDroneSessionsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("GetDroneSessionsRequest received");
		String url = request.getRequestURL() + ("?") + request.getQueryString();
		Map<String, String> parameters = HttpHelper.readRequestParameters(url);
		String droneId = parameters.get("droneId");

		log.info("GetDroneSessionsRequest request for droneId " + droneId);

		if (droneId != null) {
			log.info("Request for login " + droneId);
			Long droneIdLong = Long.parseLong(droneId);
			List<NDBDroneSession> ndbDroneSessions = administrationService.getNDBDroneSessionsForDroneId(droneIdLong);
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

}
