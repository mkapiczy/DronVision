package pl.mkapiczynski.dron.business;

import pl.mkapiczynski.dron.database.ClientUser;
import pl.mkapiczynski.dron.message.PreferencesResponse;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;

public interface AdministrationService {
	public ClientUser getClientForId(Long clientId);
	public boolean checkLoginData(String login, String password);
	public PreferencesResponse getPreferencesForClient(String login);
	public boolean updateUserDronesPreferences(SetPreferencesMessage setPreferencesMessage);

}
