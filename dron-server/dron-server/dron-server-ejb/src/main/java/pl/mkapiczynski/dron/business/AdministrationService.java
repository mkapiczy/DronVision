package pl.mkapiczynski.dron.business;

import pl.mkapiczynski.dron.database.ClientUser;
import pl.mkapiczynski.dron.domain.NDBUser;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;

public interface AdministrationService {
	public ClientUser getClientForId(Long clientId);
	public boolean checkLoginData(String login, String password);
	public NDBUser getNDBUserForLogin(String login);
	public boolean updateUserDronesPreferences(SetPreferencesMessage setPreferencesMessage);

}
