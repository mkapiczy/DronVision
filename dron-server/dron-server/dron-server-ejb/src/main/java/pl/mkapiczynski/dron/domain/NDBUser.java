package pl.mkapiczynski.dron.domain;

import java.util.List;

public class NDBUser {
	private String login;
	private List<NDBDrone> assignedDrones;
	private List<NDBDrone> trackedDrones;
	private List<NDBDrone> visualizedDrones;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public List<NDBDrone> getAssignedDrones() {
		return assignedDrones;
	}

	public void setAssignedDrones(List<NDBDrone> assignedDrones) {
		this.assignedDrones = assignedDrones;
	}

	public List<NDBDrone> getTrackedDrones() {
		return trackedDrones;
	}

	public void setTrackedDrones(List<NDBDrone> trackedDrones) {
		this.trackedDrones = trackedDrones;
	}

	public List<NDBDrone> getVisualizedDrones() {
		return visualizedDrones;
	}

	public void setVisualizedDrones(List<NDBDrone> visualizedDrones) {
		this.visualizedDrones = visualizedDrones;
	}

}