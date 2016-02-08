package pl.mkapiczynski.dron.database;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class Drone {

	@Id
	@GeneratedValue
	@Column(name = "drone_id")
	private Long droneId;

	@Column(name = "name")
	private String droneName;

	@Column(name = "description")
	private String droneDescription;

	@Enumerated(EnumType.STRING)
	private DroneStatusEnum status;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "lastLocation_id")
	private Location lastLocation;

	@ManyToMany(mappedBy = "assignedDrones")
	private List<CSTUser> assignedUsers;

	@OneToMany(mappedBy = "drone", cascade = CascadeType.ALL)
	private List<DroneSession> sessions;

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

	public List<DroneSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<DroneSession> sessions) {
		this.sessions = sessions;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public List<CSTUser> getAssignedUsers() {
		return assignedUsers;
	}

	public void setAssignedUsers(List<CSTUser> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}

}
