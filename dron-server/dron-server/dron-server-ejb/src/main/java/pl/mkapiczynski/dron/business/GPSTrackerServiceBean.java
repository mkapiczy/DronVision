package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.database.DroneSessionStatus;
import pl.mkapiczynski.dron.database.DroneStatusEnum;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

@Local
@Stateless(name = "GPSTrackerService")
public class GPSTrackerServiceBean implements GPSTrackerService {
	private static final Logger log = Logger.getLogger(GPSTrackerServiceBean.class);

	@PersistenceContext(name = "dron")
	EntityManager entityManager;

	@Override
	public Drone getDroneById(Long droneId) {
		Drone drone = entityManager.find(Drone.class, droneId);
		return drone;
	}

	@Override
	public boolean createNewDroneSession(Long droneId) {
		Drone drone = getDroneById(droneId);
		if (drone != null) {
			drone.setStatus(DroneStatusEnum.ONLINE);
			SearchedArea searchedArea = new SearchedArea();
			DroneSession droneSession = new DroneSession();
			droneSession.setDrone(drone);
			droneSession.setSearchedArea(searchedArea);
			droneSession.setSessionStarted(new Date());
			droneSession.setStatus(DroneSessionStatus.ACTIVE);
			entityManager.persist(droneSession);
			return true;
		} else {
			log.info("No drone with id: " + droneId + " was found");
			return false;
		}
	}

	@Override
	public void updateDroneSearchedArea(Long droneId, Location newSearchedLocation) {
		Drone drone = getDroneById(droneId);
		if (drone != null) {
			DroneSession activeSession = getActiveDroneSession(drone);
			if (activeSession != null && activeSession.getSearchedArea() != null
					&& activeSession.getSearchedArea().getSearchedLocations() != null) {
				List<Location> newSearchedArea = new ArrayList<>();
				newSearchedArea = calculateSearchedArea(newSearchedLocation);
				activeSession.getSearchedArea().getSearchedLocations().addAll(newSearchedArea);
			}

		} else {
			log.info("No drone with id: " + droneId + " was found");
		}

	}

	@Override
	public void closeDroneSession(Long droneId) {
		Drone drone = getDroneById(droneId);
		if(drone!=null){
			DroneSession activeSession = getActiveDroneSession(drone);
			activeSession.setStatus(DroneSessionStatus.FINISHED);
			activeSession.setSessionEnded(new Date());
			drone.setStatus(DroneStatusEnum.OFFLINE);
		} else{
			log.info("No drone with id: " + droneId + " was found");
		}
		
	}
	
	private DroneSession getActiveDroneSession(Drone drone){
		List<DroneSession> droneSessions = drone.getSessions();
		DroneSession activeSession = null;
		for (int i = 0; i < droneSessions.size(); i++) {
			if (droneSessions.get(i).getStatus().equals(DroneSessionStatus.ACTIVE)) {
				activeSession = droneSessions.get(i);
			}
		}
		return activeSession;
	}
	
	private List<Location> calculateSearchedArea(Location location) {
		GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
		List<GeoPoint> searchedAreaList = GeoPoint.pointsAsCircle(point, 20.0);
		List<Location> locationSearchedArea = new ArrayList();
		for(int i=0; i<searchedAreaList.size();i++){
			GeoPoint tempPoint = searchedAreaList.get(i);
			Location loc = new Location();
			loc.setLatitude(tempPoint.getLatitude());
			loc.setLongitude(tempPoint.getLongitude());
			loc.setAltitude(tempPoint.getAltitude());
			locationSearchedArea.add(loc);
		}
		return locationSearchedArea;
	}
}
