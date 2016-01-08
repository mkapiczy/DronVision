package dron.mkapiczynski.pl.gpsvisualiser.domain;


import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.Set;

/**
 * Created by Miix on 2016-01-05.
 */
public class Drone {
    private String deviceId;
    private String deviceName;
    private GeoPoint currentPosition;
    private Set<GeoPoint> searchedArea;
    private List<GeoPoint> lastSearchedArea;
    private int color;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public GeoPoint getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(GeoPoint currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Set<GeoPoint> getSearchedArea() {
        return searchedArea;
    }

    public void setSearchedArea(Set<GeoPoint> searchedArea) {
        this.searchedArea = searchedArea;
    }

    public List<GeoPoint> getLastSearchedArea() {
        return lastSearchedArea;
    }

    public void setLastSearchedArea(List<GeoPoint> lastSearchedArea) {
        this.lastSearchedArea = lastSearchedArea;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
