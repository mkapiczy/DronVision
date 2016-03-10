package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.ClientUser;
import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.domain.NDBDrone;
import pl.mkapiczynski.dron.message.PreferencesResponse;
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

	private boolean userPasswordIsCorrect(ClientUser user, String password) {
		if (user.getUserAccount() != null) {
			if (password.equals(user.getUserAccount().getPassword())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PreferencesResponse getPreferencesForClient(String login) {
		PreferencesResponse preferencesResponse = new PreferencesResponse();
		String queryStr = "SELECT u FROM ClientUser u WHERE u.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<ClientUser> userList = query.getResultList();
		ClientUser user = null;
		if (userList != null && !userList.isEmpty()) {
			user = userList.get(0);
		}
		if (user != null) {
			preferencesResponse.setLogin(user.getUserAccount().getLogin());
			preferencesResponse.setAssignedDrones(convertDronesToNDTDrones(user.getAssignedDrones()));
			preferencesResponse.setTrackedDrones(convertDronesToNDTDrones(user.getTrackedDrones()));
			preferencesResponse.setVisualizedDrones(convertDronesToNDTDrones(user.getVisualizedDrones()));
		}
		return preferencesResponse;
	}

	private List<NDBDrone> convertDronesToNDTDrones(List<Drone> drones) {
		List<NDBDrone> ndtDronesList = new ArrayList<>();
		for (int i = 0; i < drones.size(); i++) {
			NDBDrone ndtDrone = new NDBDrone();
			Drone drone = drones.get(i);
			ndtDrone.setDroneId(drone.getDroneId());
			ndtDrone.setDroneName(drone.getDroneName());
			ndtDrone.setDroneDescription(drone.getDroneDescription());
			ndtDrone.setStatus(drone.getStatus());
			if (drone.getLastLocation() != null) {
				ndtDrone.setLastLocation(
						new GeoPoint(drone.getLastLocation().getLatitude(), drone.getLastLocation().getLongitude()));
			}
			ndtDronesList.add(ndtDrone);
		}
		return ndtDronesList;
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
					List<Drone> dbTrackedDrones = getDBDronesFromNDTDrones(trackedDrones, userAssignedDrones);
					if (dbTrackedDrones != null && !dbTrackedDrones.isEmpty()) {
						userToUpdate.setTrackedDrones(dbTrackedDrones);
					} else {
						userToUpdate.getTrackedDrones().clear();
					}
				}
			}

			boolean visualizedDronesChanged = setPreferencesMessage.isVisualizedDronesChanged();
			if (visualizedDronesChanged) {
				List<NDBDrone> visualizedDrones = setPreferencesMessage.getVisualizedDrones();
				List<Drone> dbVisualizedDrones = getDBDronesFromNDTDrones(visualizedDrones, userAssignedDrones);
				if (dbVisualizedDrones != null && !dbVisualizedDrones.isEmpty()) {
					userToUpdate.setVisualizedDrones(dbVisualizedDrones);
				} else {
					userToUpdate.getVisualizedDrones().clear();
				}
			}

			return true;
		}
		return false;

	}

	private List<Drone> getDBDronesFromNDTDrones(List<NDBDrone> ndtDrones, List<Drone> assignedDrones) {
		List<Drone> drones = new ArrayList<>();
		for (int i = 0; i < assignedDrones.size(); i++) {
			for (int j = 0; j < ndtDrones.size(); j++) {
				if (assignedDrones.get(i).getDroneId() == ndtDrones.get(j).getDroneId()) {
					drones.add(assignedDrones.get(i));
				}
			}
		}
		return drones;
	}

	public ClientUser getUserForLogin(String login) {
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
