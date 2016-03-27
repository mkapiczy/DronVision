package pl.mkapiczynski.dron.helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.domain.NDBDrone;
import pl.mkapiczynski.dron.domain.NDBDroneSession;

public class NDBHelper {
	
	public static List<NDBDrone> convertDronesToNDBDrones(List<Drone> drones) {
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
	
	public static List<Drone> getDBDronesFromNDTDrones(List<NDBDrone> ndtDrones, List<Drone> assignedDrones) {
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
	
	public static List<NDBDroneSession> convertDroneSessionsToNDBDroneSessions(List<DroneSession> droneSessions){
		List<NDBDroneSession> ndbDroneSessions = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < droneSessions.size(); i++) {
			DroneSession currentIteratedDroneSession = droneSessions.get(i);
			NDBDroneSession ndbDroneSession = new NDBDroneSession();
			ndbDroneSession.setSessionId(currentIteratedDroneSession.getSessionId());
			ndbDroneSession.setDroneId(currentIteratedDroneSession.getDrone().getDroneId());
			ndbDroneSession.setDroneName(currentIteratedDroneSession.getDrone().getDroneName());
			String startDate = dateFormat.format(currentIteratedDroneSession.getSessionStarted());
			String endDate = dateFormat.format(currentIteratedDroneSession.getSessionEnded());
			ndbDroneSession.setSessionStarted(startDate);
			ndbDroneSession.setSessionEnded(endDate);
			ndbDroneSessions.add(ndbDroneSession);
		}
		return ndbDroneSessions;
	}
	
}
