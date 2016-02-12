package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.CSTUser;
import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.domain.NDTDrone;
import pl.mkapiczynski.dron.message.PreferencesResponse;
import pl.mkapiczynski.dron.message.SetPreferencesMessage;

@Local
@Stateless(name = "AdministrationService")
public class AdministrationServiceBean implements AdministrationService {
	private static final Logger log = Logger.getLogger(ClientDeviceServiceBean.class);

	@PersistenceContext(name = "dron")
	EntityManager entityManager;

	@Override
	public boolean checkLoginData(String login, String password) {
		String queryStr = "SELECT u FROM CSTUser u WHERE u.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<CSTUser> userList = query.getResultList();
		CSTUser user = null;
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

	private boolean userPasswordIsCorrect(CSTUser user, String password) {
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
		String queryStr = "SELECT u FROM CSTUser u WHERE u.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<CSTUser> userList = query.getResultList();
		CSTUser user = null;
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

	private List<NDTDrone> convertDronesToNDTDrones(List<Drone> drones) {
		List<NDTDrone> ndtDronesList = new ArrayList<>();
		for (int i = 0; i < drones.size(); i++) {
			NDTDrone ndtDrone = new NDTDrone();
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
		CSTUser userToUpdate = getUserForLogin(login);
		if (userToUpdate != null) {
			List<Drone> userAssignedDrones = userToUpdate.getAssignedDrones();
			if (userAssignedDrones != null) {
				boolean trackedDronesChanged = setPreferencesMessage.isTrackedDronesChanged();
				if (trackedDronesChanged) {
					List<NDTDrone> trackedDrones = setPreferencesMessage.getTrackedDrones();
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
				List<NDTDrone> visualizedDrones = setPreferencesMessage.getVisualizedDrones();
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

	private List<Drone> getDBDronesFromNDTDrones(List<NDTDrone> ndtDrones, List<Drone> assignedDrones) {
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

	public CSTUser getUserForLogin(String login) {
		CSTUser foundUser = null;
		String queryStr = "Select c FROM CSTUser c where c.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<CSTUser> users = query.getResultList();
		if (users != null && users.size() > 0) {
			foundUser = users.get(0);
		}
		return foundUser;

	}

}
