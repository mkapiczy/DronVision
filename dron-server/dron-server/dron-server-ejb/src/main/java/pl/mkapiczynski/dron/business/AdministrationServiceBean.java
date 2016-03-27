package pl.mkapiczynski.dron.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.ClientUser;
import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.domain.NDBDrone;
import pl.mkapiczynski.dron.domain.NDBDroneSession;
import pl.mkapiczynski.dron.domain.NDBUser;
import pl.mkapiczynski.dron.helpers.NDBHelper;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;

@Local
@Stateless(name = "AdministrationService")
public class AdministrationServiceBean implements AdministrationService {
	private static final Logger log = Logger.getLogger(ClientDeviceServiceBean.class);

	@PersistenceContext(name = "dron")
	EntityManager entityManager;

	@Override
	public ClientUser getClientForId(Long clientId) {
		ClientUser client = null;
		String queryStr = "SELECT c FROM ClientUser c where c.userId = :clientId";
		TypedQuery<ClientUser> query = entityManager.createQuery(queryStr, ClientUser.class);
		query.setParameter("clientId", clientId);

		List<ClientUser> clientsList = query.getResultList();
		if (clientsList != null && !clientsList.isEmpty()) {
			client = clientsList.get(0);
		}
		return client;
	}

	@Override
	public boolean checkLoginData(String login, String password) {
		String queryStr = "SELECT u FROM ClientUser u WHERE u.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<ClientUser> userList = query.getResultList();
		ClientUser user = null;
		if (userList != null && !userList.isEmpty()) {
			user = userList.get(0);
		}
		if (user != null && password != null) {
			if (userPasswordIsCorrect(user, password)) {
				return true;
			}
		}
		return false;
	}



	@Override
	public NDBUser getNDBUserForLogin(String login) {
		String queryStr = "SELECT u FROM ClientUser u WHERE u.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<ClientUser> userList = query.getResultList();
		ClientUser user = null;
		if (userList != null && !userList.isEmpty()) {
			user = userList.get(0);
		}
		if (user != null) {
			NDBUser ndbUser = new NDBUser();
			ndbUser.setLogin(user.getUserAccount().getLogin());
			ndbUser.setAssignedDrones(NDBHelper.convertDronesToNDBDrones(user.getAssignedDrones()));
			ndbUser.setTrackedDrones(NDBHelper.convertDronesToNDBDrones(user.getTrackedDrones()));
			ndbUser.setVisualizedDrones(NDBHelper.convertDronesToNDBDrones(user.getVisualizedDrones()));
			return ndbUser;
		}
		return null;
	}
	
	@Override
	public List<NDBDroneSession> getNDBDroneSessionsForDroneId(Long droneId){
		List<DroneSession> droneSessions = new ArrayList<>();
		String queryStr = "SELECT d FROM DroneSession d WHERE d.drone.id like :droneId AND d.sessionStarted!=NULL AND d.sessionEnded!=null ORDER BY d.sessionStarted DESC";
		TypedQuery<DroneSession> query = entityManager.createQuery(queryStr, DroneSession.class);
		query.setParameter("droneId", droneId);
		query.setMaxResults(10);
		droneSessions = query.getResultList();
		List<NDBDroneSession> ndbDroneSessions = NDBHelper.convertDroneSessionsToNDBDroneSessions(droneSessions);	
		return ndbDroneSessions;
	}

	@Override
	public boolean updateUserDronesPreferences(SetPreferencesMessage setPreferencesMessage) {
		String login = setPreferencesMessage.getLogin();
		ClientUser userToUpdate = getUserForLogin(login);
		if (userToUpdate != null) {
			List<Drone> userAssignedDrones = userToUpdate.getAssignedDrones();
			if (userAssignedDrones != null) {
				
				boolean trackedDronesChanged = setPreferencesMessage.isTrackedDronesChanged();
				if (trackedDronesChanged) {
					List<NDBDrone> trackedDrones = setPreferencesMessage.getTrackedDrones();
					List<Drone> dbTrackedDrones = NDBHelper.getDBDronesFromNDTDrones(trackedDrones, userAssignedDrones);
					if (dbTrackedDrones != null && !dbTrackedDrones.isEmpty()) {
						userToUpdate.setTrackedDrones(dbTrackedDrones);
					} else {
						userToUpdate.getTrackedDrones().clear();
					}
				}

				boolean visualizedDronesChanged = setPreferencesMessage.isVisualizedDronesChanged();
				if (visualizedDronesChanged) {
					List<NDBDrone> visualizedDrones = setPreferencesMessage.getVisualizedDrones();
					List<Drone> dbVisualizedDrones = NDBHelper.getDBDronesFromNDTDrones(visualizedDrones,
							userAssignedDrones);
					if (dbVisualizedDrones != null && !dbVisualizedDrones.isEmpty()) {
						userToUpdate.setVisualizedDrones(dbVisualizedDrones);
					} else {
						userToUpdate.getVisualizedDrones().clear();
					}
				}

				return true;
			}
		}
		return false;
	}
	
	private boolean userPasswordIsCorrect(ClientUser user, String password) {
		if (user.getUserAccount() != null) {
			if (password.equals(user.getUserAccount().getPassword())) {
				return true;
			}
		}
		return false;
	}

	


	private ClientUser getUserForLogin(String login) {
		ClientUser foundUser = null;
		String queryStr = "Select c FROM ClientUser c where c.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<ClientUser> users = query.getResultList();
		if (users != null && users.size() > 0) {
			foundUser = users.get(0);
		}
		return foundUser;

	}

}
