package pl.mkapiczynski.dron.domain;

import pl.mkapiczynski.dron.database.DroneStatusEnum;

/**
 * Niebazodanowa reprezentacja drona do komunikacji z aplikacją DronVision (aby ułatwić parse'owanie po tamtej stronie)
 * 
 * @author Michal Kapiczynski
 *
 */
public class NDBDrone {
	private Long droneId;
	private String droneName;
	private String droneDescription;
	private DroneStatusEnum status;
	private GeoPoint lastLocation;

	public Long getDroneId() {
		return droneId;
	}

	public void setDroneId(Long droneId) {
		this.droneId = droneId;
	}

	public String getDroneName() {
		return droneName;
	}

	public void setDroneName(String droneName) {
		this.droneName = droneName;
	}

	public String getDroneDescription() {
		return droneDescription;
	}

	public void setDroneDescription(String droneDescription) {
		this.droneDescription = droneDescription;
	}

	public DroneStatusEnum getStatus() {
		return status;
	}

	public void setStatus(DroneStatusEnum status) {
		this.status = status;
	}

	public GeoPoint getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(GeoPoint lastLocation) {
		this.lastLocation = lastLocation;
	}

}