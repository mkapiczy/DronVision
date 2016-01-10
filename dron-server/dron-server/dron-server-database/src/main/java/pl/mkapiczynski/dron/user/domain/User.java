package pl.mkapiczynski.dron.user.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import pl.mkapiczynski.dron.drone.domain.Drone;

@Entity
@Table(name = "User")
public class User {
	
	@Id
	@GeneratedValue
	private Long loid;
	private String userId;
	
	//UserAccount
	@OneToMany
	List<Drone> assignedDrones;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	Drone visualizedDrone;
	
	@OneToMany
	List<Drone> trackedDrones;
}
