package pl.mkapiczynski.dron.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import pl.mkapiczynski.dron.domain.GeoPoint;

@Entity
@Table(name = "Location")
public class Location {

	@Id
	@GeneratedValue
	private Long id;

	private Double latitude;

	private Double longitude;

	private Double altitude;
	
	public Location(){
		
	}
	
	public Location(GeoPoint geoPoint){
		this.latitude = geoPoint.getLatitude();
		this.longitude = geoPoint.getLongitude();
		this.altitude = geoPoint.getAltitude();
	}

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

}
