package pl.mkapiczynski.dron.message;

import java.util.List;

import pl.mkapiczynski.dron.domain.NDTDrone;

public class PreferencesResponse {
	private String login;
	private List<NDTDrone> assignedDrones;
	private List<NDTDrone> trackedDrones;
	private List<NDTDrone> visualizedDrones;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public List<NDTDrone> getAssignedDrones() {
		return assignedDrones;
	}

	public void setAssignedDrones(List<NDTDrone> assignedDrones) {
		this.assignedDrones = assignedDrones;
	}

	public List<NDTDrone> getTrackedDrones() {
		return trackedDrones;
	}

	public void setTrackedDrones(List<NDTDrone> trackedDrones) {
		this.trackedDrones = trackedDrones;
	}

	public List<NDTDrone> getVisualizedDrones() {
		return visualizedDrones;
	}

	public void setVisualizedDrones(List<NDTDrone> visualizedDrones) {
		this.visualizedDrones = visualizedDrones;
	}

}
