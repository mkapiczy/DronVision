package pl.mkapiczynski.dron.business;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.domain.GeoPoint;

public interface DroneService {
	public Drone getDroneById(Long droneId);
	public boolean createNewDroneSession(Long droneId);
	public void updateDroneSearchedArea(Drone drone, GeoPoint newSearchedLocation);
	public void closeDroneSession(Long droneId);
	public DroneSession getActiveDroneSession(Drone drone);
}
