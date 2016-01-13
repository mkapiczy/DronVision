package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import pl.mkapiczynski.dron.database.CSTUser;
import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.domain.LoginResponse;
import pl.mkapiczynski.dron.domain.NDTDrone;

@Local
@Stateless(name = "AdministrationService")
public class AdministrationServiceBean implements AdministrationService {

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
	public LoginResponse generateLoginResponse(String login) {
		LoginResponse loginResponse = new LoginResponse();
		String queryStr = "SELECT u FROM CSTUser u WHERE u.userAccount.login = :login";
		Query query = entityManager.createQuery(queryStr);
		query.setParameter("login", login);
		List<CSTUser> userList = query.getResultList();
		CSTUser user = null;
		if (userList != null && !userList.isEmpty()) {
			user = userList.get(0);
		}
		if(user!=null){
			loginResponse.setLogin(user.getUserAccount().getLogin());
			loginResponse.setAssignedDrones(convertDronesToNDTDrones(user.getAssignedDrones()));
			loginResponse.setTrackedDrones(convertDronesToNDTDrones(user.getTrackedDrones()));
			loginResponse.setVisualizedDrones(convertDronesToNDTDrones(user.getVisualizedDrones()));
		}
		return loginResponse;
	}

	private List<NDTDrone> convertDronesToNDTDrones(List<Drone> drones) {
		List<NDTDrone> ndtDronesList = new ArrayList<>();
		for (int i = 0; i < drones.size(); i++) {
			NDTDrone ndtDrone = new NDTDrone();
			Drone drone = drones.get(i);
			ndtDrone.setDroneId(String.valueOf(drone.getDroneId()));
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

}
