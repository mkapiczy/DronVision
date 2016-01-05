package dron.mkapiczynski.pl.gpsvisualiser.domain;

import java.util.List;

/**
 * Created by Miix on 2016-01-05.
 */
public class Drone {
    private String deviceId;
    private String currentLatitude;
    private String currentLongitude;
    private String currentAltitude;
    private List<String[]> location;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCurrentAltitude() {
        return currentAltitude;
    }

    public void setCurrentAltitude(String currentAltitude) {
        this.currentAltitude = currentAltitude;
    }

    public List<String[]> getLocation() {
        return location;
    }

    public void setLocation(List<String[]> location) {
        this.location = location;
    }
}
