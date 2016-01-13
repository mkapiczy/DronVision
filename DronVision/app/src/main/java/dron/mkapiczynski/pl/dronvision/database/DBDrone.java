package dron.mkapiczynski.pl.dronvision.database;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Miix on 2016-01-13.
 */
public class DBDrone {
    private Integer droneId;
    private String droneName;
    private String droneDescription;
    private DroneStatusEnum droneStatus;
    private GeoPoint lastLocation;
    private Boolean tracked;
    private Boolean visualized;

    public Integer getDroneId() {
        return droneId;
    }

    public void setDroneId(Integer droneId) {
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

    public DroneStatusEnum getDroneStatus() {
        return droneStatus;
    }

    public void setDroneStatus(DroneStatusEnum droneStatus) {
        this.droneStatus = droneStatus;
    }

    public GeoPoint getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(GeoPoint lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Boolean getTracked() {
        return tracked;
    }

    public void setTracked(Boolean tracked) {
        this.tracked = tracked;
    }

    public Boolean getVisualized() {
        return visualized;
    }

    public void setVisualized(Boolean visualized) {
        this.visualized = visualized;
    }
}
