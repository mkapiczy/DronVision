package pl.mkapiczynski.dron.database;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSTUser")
public class CSTUser {

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private Long userId;

	@OneToOne(optional = false)
	@JoinColumn(name = "account_id", unique = true, nullable = false)
	private UserAccount userAccount;

	@ManyToMany
	@JoinTable(name = "User_AssignedDrones", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "assignedDrone_id", referencedColumnName = "drone_id"))
	List<Drone> assignedDrones;

	/*
	 * @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	 * 
	 * @JoinColumn(name = "visualizedDrone_id") Drone visualizedDrone;
	 */

	@ManyToMany
	@JoinTable(name = "User_TrackedDrones", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "trackedDrone_id", referencedColumnName = "drone_id"))
	List<Drone> trackedDrones;

	@ManyToMany
	@JoinTable(name = "User_VisualizedDrones", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "visualizedDrone_id", referencedColumnName = "drone_id"))
	List<Drone> visualizedDrones;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public List<Drone> getAssignedDrones() {
		return assignedDrones;
	}

	public void setAssignedDrones(List<Drone> assignedDrones) {
		this.assignedDrones = assignedDrones;
	}

	public List<Drone> getTrackedDrones() {
		return trackedDrones;
	}

	public void setTrackedDrones(List<Drone> trackedDrones) {
		this.trackedDrones = trackedDrones;
	}

	public List<Drone> getVisualizedDrones() {
		return visualizedDrones;
	}

	public void setVisualizedDrones(List<Drone> visualizedDrones) {
		this.visualizedDrones = visualizedDrones;
	}

}
