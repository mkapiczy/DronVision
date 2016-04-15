package dron.mkapiczynski.pl.dronvision.domain;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Miix on 2016-04-15.
 */
public class DroneSearchedArea {
    List<MyGeoPoint> searchedAreaLocations;
    List<HoleInSearchedArea> holesInSearchedArea;

    public List<MyGeoPoint> getSearchedAreaLocations() {
        return searchedAreaLocations;
    }

    public void setSearchedAreaLocations(List<MyGeoPoint> searchedAreaLocations) {
        this.searchedAreaLocations = searchedAreaLocations;
    }

    public List<HoleInSearchedArea> getHolesInSearchedArea() {
        return holesInSearchedArea;
    }

    public void setHolesInSearchedArea(List<HoleInSearchedArea> holesInSearchedArea) {
        this.holesInSearchedArea = holesInSearchedArea;
    }
}
