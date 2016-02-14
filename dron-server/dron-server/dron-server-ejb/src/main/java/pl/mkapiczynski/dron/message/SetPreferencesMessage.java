package pl.mkapiczynski.dron.message;

import java.util.List;

import pl.mkapiczynski.dron.domain.NDBDrone;

public class SetPreferencesMessage {
	private String login;
	private List<NDBDrone> assignedDrones;
	private boolean trackedDronesChanged;
	private List<NDBDrone> trackedDrones;
	private boolean visualizedDronesChanged;
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
	public boolean isTrackedDronesChanged() {
		return trackedDronesChanged;
	}
	public void setTrackedDronesChanged(boolean trackedDronesChanged) {
		this.trackedDronesChanged = trackedDronesChanged;
	}
	public List<NDBDrone> getTrackedDrones() {
		return trackedDrones;
	}
	public void setTrackedDrones(List<NDBDrone> trackedDrones) {
		this.trackedDrones = trackedDrones;
	}
	public boolean isVisualizedDronesChanged() {
		return visualizedDronesChanged;
	}
	public void setVisualizedDronesChanged(boolean visualizedDronesChanged) {
		this.visualizedDronesChanged = visualizedDronesChanged;
	}
	public List<NDBDrone> getVisualizedDrones() {
		return visualizedDrones;
	}
	public void setVisualizedDrones(List<NDBDrone> visualizedDrones) {
		this.visualizedDrones = visualizedDrones;
	}


	
	

}
