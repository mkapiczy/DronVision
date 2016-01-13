package pl.mkapiczynski.dron.business;

import pl.mkapiczynski.dron.domain.LoginResponse;

public interface AdministrationService {
	public boolean checkLoginData(String login, String password);
	public LoginResponse generateLoginResponse(String login);

}
