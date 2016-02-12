package dron.mkapiczynski.pl.dronvision.message;

import java.util.List;

import dron.mkapiczynski.pl.dronvision.database.DBDrone;

/**
 * Created by Miix on 2016-02-12.
 */
public class GetPreferencesMessage {
    private String login;
    private List<DBDrone> assignedDrones;
    private List<DBDrone> trackedDrones;
    private List<DBDrone> visualizedDrones;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<DBDrone> getAssignedDrones() {
        return assignedDrones;
    }

    public void setAssignedDrones(List<DBDrone> assignedDrones) {
        this.assignedDrones = assignedDrones;
    }

    public List<DBDrone> getTrackedDrones() {
        return trackedDrones;
    }

    public void setTrackedDrones(List<DBDrone> trackedDrones) {
        this.trackedDrones = trackedDrones;
    }

    public List<DBDrone> getVisualizedDrones() {
        return visualizedDrones;
    }

    public void setVisualizedDrones(List<DBDrone> visualizedDrones) {
        this.visualizedDrones = visualizedDrones;
    }
}
