package pl.mkapiczynski.dron.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.business.SearchedAreaService;
import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.database.DroneSessionStatus;
import pl.mkapiczynski.dron.database.DroneStatusEnum;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.GeoPoint;

public class DroneUtils {
	
	private static final Logger log = Logger.getLogger(DroneUtils.class);
	
	@Inject
	static SearchedAreaService searchedAreaService;
	
	@PersistenceContext(name = "dron")
	private static EntityManager entityManager;
	
	public static Drone getDroneById(Long droneId) {
		Drone drone = entityManager.find(Drone.class, droneId);
		return drone;
	}


	public static boolean createNewDroneSession(Long droneId) {
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


	public static void updateDroneSearchedArea(Long droneId, GeoPoint newSearchedLocation) {
		Drone drone = getDroneById(droneId);
		if (drone != null) {
			DroneSession activeSession = getActiveDroneSession(drone);
			if (activeSession != null && activeSession.getSearchedArea() != null) {
				List<GeoPoint> newSearchedAreaGeoPoint = 
				newSearchedAreaGeoPoint = searchedAreaService.calculateSearchedArea(newSearchedLocation);
				List<Location> newSearchedArea = convertGeoPointSearchedAreaToLocationSearchedArea(newSearchedAreaGeoPoint);
				if(activeSession.getSearchedArea().getSearchedLocations()!=null){
					activeSession.getSearchedArea().getSearchedLocations().addAll(newSearchedArea);
				} else{
					activeSession.getSearchedArea().setSearchedLocations(new ArrayList<Location>());
					activeSession.getSearchedArea().getSearchedLocations().addAll(newSearchedArea);
				}
			}

		} else {
			log.info("No drone with id: " + droneId + " was found");
		}

	}


	public static void closeDroneSession(Long droneId) {
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
	
	private static DroneSession getActiveDroneSession(Drone drone){
		List<DroneSession> droneSessions = drone.getSessions();
		DroneSession activeSession = null;
		for (int i = 0; i < droneSessions.size(); i++) {
			if (droneSessions.get(i).getStatus().equals(DroneSessionStatus.ACTIVE)) {
				activeSession = droneSessions.get(i);
			}
		}
		return activeSession;
	}
	
	private static List<Location> convertGeoPointSearchedAreaToLocationSearchedArea(List<GeoPoint> geoPointSearchedArea) {
		List<Location> locationSearchedArea = new ArrayList();
		for(int i=0; i<geoPointSearchedArea.size();i++){
			GeoPoint tempPoint = geoPointSearchedArea.get(i);
			Location loc = new Location();
			loc.setLatitude(tempPoint.getLatitude());
			loc.setLongitude(tempPoint.getLongitude());
			loc.setAltitude(tempPoint.getAltitude());
			locationSearchedArea.add(loc);
		}
		return locationSearchedArea;
	}
}
