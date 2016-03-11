package pl.mkapiczynski.dron.business;

import java.util.List;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.domain.GeoPoint;

public interface DroneService {
	public Drone getDroneById(Long droneId);
	public boolean droneHasActiveSession(Long droneId);
	public boolean createNewDroneSession(Long droneId);
	public void updateDroneSearchedArea(Drone drone);
	public void closeDroneActiveSession(Long droneId);
	public DroneSession getActiveDroneSession(Drone drone);
	public List<DroneSession> getDroneSessions(Long droneId);
	public List<GeoPoint> getSearchedAreaForSession(Long sessionId);
}
