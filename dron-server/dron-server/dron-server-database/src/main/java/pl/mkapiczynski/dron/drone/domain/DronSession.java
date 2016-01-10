package pl.mkapiczynski.dron.drone.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import pl.mkapiczynski.dron.location.domain.SearchedLocation;

@Entity
@Table(name="DronSession")
public class DronSession {
	@Id
	@GeneratedValue
	private Long sessionId;
	
	private Date sessionStarted;
	
	private Date sessionEnded;
	
	@OneToMany
	private List<SearchedLocation> searchedLocations;

}
