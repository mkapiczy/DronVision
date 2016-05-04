package pl.mkapiczynski.dron.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Table;

/**
 * Symulacja
 * 
 * @author Michal Kapiczynski
 *
 */
@Entity
@Table
public class Simulation {

	@Id
	@GeneratedValue
	private Long id;

	private Double latitude;

	private Double longitude;

	private Double altitude;
	
	private boolean simulated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public boolean isSimulated() {
		return simulated;
	}

	public void setSimulated(boolean simulated) {
		this.simulated = simulated;
	}
	
	

}
