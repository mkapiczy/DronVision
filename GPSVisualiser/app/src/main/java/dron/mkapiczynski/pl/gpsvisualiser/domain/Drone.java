package dron.mkapiczynski.pl.gpsvisualiser.domain;

import android.graphics.Color;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Miix on 2016-01-05.
 */
public class Drone {
    private String deviceId;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double currentAltitude;
    private List<GeoPoint> trail;
    private int color;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public Double getCurrentAltitude() {
        return currentAltitude;
    }

    public void setCurrentAltitude(Double currentAltitude) {
        this.currentAltitude = currentAltitude;
    }

    public List<GeoPoint> getTrail() {
        return trail;
    }

    public void setTrail(List<GeoPoint> trail) {
        this.trail = trail;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
