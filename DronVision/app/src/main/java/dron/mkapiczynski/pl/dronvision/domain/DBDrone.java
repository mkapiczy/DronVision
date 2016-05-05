package dron.mkapiczynski.pl.dronvision.domain;

import dron.mkapiczynski.pl.dronvision.domain.DroneStatusEnum;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;

/**
 * Created by Miix on 2016-01-13.
 */
/**
 * Klasa reprezentująca drona wykorzystywana do komunikacji z serwerem
 */
public class DBDrone {
    private Long droneId;
    private String droneName;
    private String droneDescription;
    private DroneStatusEnum status;
    private MyGeoPoint lastLocation;

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

    public MyGeoPoint getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(MyGeoPoint lastLocation) {
        this.lastLocation = lastLocation;
    }
}
