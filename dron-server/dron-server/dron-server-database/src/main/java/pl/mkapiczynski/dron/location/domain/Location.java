package pl.mkapiczynski.dron.location.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Location")
public class Location {
	
	@Id
	@GeneratedValue
	private Long loid;
	
	private Double latitude;
	
	private Double longitude;
	
	private Double altitude;

}
