package pl.mkapiczynski.dron.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.domain.NDBDroneSession;
import pl.mkapiczynski.dron.domain.NDBSearchedArea;
import pl.mkapiczynski.dron.helpers.HttpHelper;
import pl.mkapiczynski.dron.helpers.JsonDateDeserializer;
import pl.mkapiczynski.dron.helpers.ServerResponse;
import pl.mkapiczynski.dron.response.GetDroneSessionsResponse;
import pl.mkapiczynski.dron.response.GetSearchedAreaResponse;

/**
 * Servlet implementation class PreferencesServlet
 */
@WebServlet("/getSearchedArea")
public class GetSearchedAreaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(GetSearchedAreaServlet.class);

	@EJB
	DroneService droneService;

	public GetSearchedAreaServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("GetSearchedAreaRequest received");
		String url = request.getRequestURL() + ("?") + request.getQueryString();
		Map<String, String> parameters = HttpHelper.readRequestParameters(url);
		String sessionId = parameters.get("sessionId");

		log.info("GetSearchedAreaRequest request for droneId " + sessionId);

		if (sessionId != null) {
			log.info("Request for login " + sessionId);
			Long sessionIdLong = Long.parseLong(sessionId);
			NDBSearchedArea responseSearchedArea =  droneService.getSearchedAreaForSession(sessionIdLong);
			GetSearchedAreaResponse getSearchedAreaResponse = new GetSearchedAreaResponse();
			getSearchedAreaResponse.setSearchedArea(responseSearchedArea);
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();
			String json = gson.toJson(getSearchedAreaResponse);
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
