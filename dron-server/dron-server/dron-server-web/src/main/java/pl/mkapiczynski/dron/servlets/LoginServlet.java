package pl.mkapiczynski.dron.servlets;

import java.io.IOException;

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
import pl.mkapiczynski.dron.response.PreferencesResponse;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(LoginServlet.class);

	@EJB
	AdministrationService administrationService;

	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("Unexpected GET request for LoginServlet");
		HttpHelper.setStatusOrError(response, ServerResponse.METHOD_NOT_ALLOWED);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		log.info("Login request for login " + login);

		if (login != null && password != null) {
			if (administrationService.checkLoginData(login, password)) {
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
				HttpHelper.setStatusOrError(response, ServerResponse.NOT_AUTHORIZED);
			}
		} else {
			HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
		}

	}

}
