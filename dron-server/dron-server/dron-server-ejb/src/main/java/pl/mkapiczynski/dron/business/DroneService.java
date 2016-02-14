package pl.mkapiczynski.dron.business;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.database.Location;

public interface DroneService {
	public Drone getDroneById(Long droneId);
	public boolean createNewDroneSession(Long droneId);
	public void updateDroneSearchedArea(Drone drone);
	public void closeDroneSession(Long droneId);
	public DroneSession getActiveDroneSession(Drone drone);
}
