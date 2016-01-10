package pl.mkapiczynski.dron.drone.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import pl.mkapiczynski.dron.drone.enums.DroneStatusEnum;
import pl.mkapiczynski.dron.location.domain.Location;
import pl.mkapiczynski.dron.user.domain.User;

@Entity
@Table(name="Drone")
public class Drone {
	
	@Id
	@GeneratedValue
	private Long droneId;
	
	@Column(name="name")
	private String droneName;
	
	@Column(name="description")
	private String droneDescription;
	
	private DroneStatusEnum status;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Location lastLocation;
	
	@OneToMany
	private List<User> assignedUsers;

}
