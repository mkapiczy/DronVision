package dron.mkapiczynski.pl.dronvision.domain;


import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Miix on 2016-01-05.
 */
/**
 * Klasa reprezentująca drona wykorzystywana do wizualizacji
 */
public class Drone {
    private Long droneId;
    private String droneName;
    private GeoPoint currentPosition;
    private List<GeoPoint> searchedArea;
    private List<GeoPoint> lastSearchedArea;
    private List<MapHoleInSearchedArea> holes;
    private List<MapHoleInSearchedArea> lastHoles;
    private int color;

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

    public GeoPoint getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(GeoPoint currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<GeoPoint> getSearchedArea() {
        return searchedArea;
    }

    public void setSearchedArea(List<GeoPoint> searchedArea) {
        this.searchedArea = searchedArea;
    }

    public List<GeoPoint> getLastSearchedArea() {
        return lastSearchedArea;
    }

    public void setLastSearchedArea(List<GeoPoint> lastSearchedArea) {
        this.lastSearchedArea = lastSearchedArea;
    }

    public List<MapHoleInSearchedArea> getHoles() {
        return holes;
    }

    public void setHoles(List<MapHoleInSearchedArea> holes) {
        this.holes = holes;
    }

    public List<MapHoleInSearchedArea> getLastHoles() {
        return lastHoles;
    }

    public void setLastHoles(List<MapHoleInSearchedArea> lastHoles) {
        this.lastHoles = lastHoles;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
