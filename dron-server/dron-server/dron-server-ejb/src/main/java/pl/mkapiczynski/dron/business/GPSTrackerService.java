package pl.mkapiczynski.dron.business;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.Location;

public interface GPSTrackerService {
	public Drone getDroneById(Long droneId);
	public boolean createNewDroneSession(Long droneId);
	public void closeDroneSession(Long droneId);
	public void updateDroneSearchedArea(Long droneId, Location newSearchedLocation);

}
