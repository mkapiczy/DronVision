package pl.mkapiczynski.dron.business;

import java.util.List;

import pl.mkapiczynski.dron.database.ClientUser;
import pl.mkapiczynski.dron.domain.NDBDroneSession;
import pl.mkapiczynski.dron.domain.NDBUser;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;

/**
 * 
 * @author Michal Kapiczynski
 * 
 * Interfejs dla klasy biznesowej do pobierania z bazy danych danych administracyjnych, związanych z użytkownikiem
 * takich jak login, preferencje, czy ustawienia konta
 *
 */
public interface AdministrationService {
	public ClientUser getClientForId(Long clientId);
	public boolean checkLoginData(String login, String password);
	public NDBUser getNDBUserForLogin(String login);
	public List<NDBDroneSession> getNDBDroneSessionsForDroneId(Long droneId);
	public boolean updateUserDronesPreferences(SetPreferencesMessage setPreferencesMessage);

}
