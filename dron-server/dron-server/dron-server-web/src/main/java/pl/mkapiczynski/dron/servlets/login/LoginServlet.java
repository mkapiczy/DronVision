package pl.mkapiczynski.dron.servlets.login;

import java.io.IOException;
import java.util.Date;

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
import pl.mkapiczynski.dron.domain.LoginResponse;
import pl.mkapiczynski.dron.helpers.HttpHelper;
import pl.mkapiczynski.dron.helpers.JsonDateSerializer;
import pl.mkapiczynski.dron.helpers.ServerResponse;

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String password = request.getParameter("password");
		
		if(login!=null && password!=null){
			if(administrationService.checkLoginData(login, password)){
				LoginResponse loginResponse = administrationService.generateLoginResponse(login);
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				String json = gson.toJson(loginResponse);
				HttpHelper.sendJSON(response, json);
			} else{
				HttpHelper.setStatusOrError(response, ServerResponse.NOT_AUTHORIZED);
			}
		} else{
			HttpHelper.setStatusOrError(response, ServerResponse.REQUIRED_FIELD_MISSING);
		}
		
	}

}
