package dron.mkapiczynski.pl.dronvision.domain;

import java.util.List;

/**
 * Created by Miix on 2016-04-15.
 */
public class DroneSearchedArea {
    List<MyGeoPoint> searchedAreaLocations;
    List<DroneHoleInSearchedArea> holesInSearchedArea;

    public List<MyGeoPoint> getSearchedAreaLocations() {
        return searchedAreaLocations;
    }

    public void setSearchedAreaLocations(List<MyGeoPoint> searchedAreaLocations) {
        this.searchedAreaLocations = searchedAreaLocations;
    }

    public List<DroneHoleInSearchedArea> getHolesInSearchedArea() {
        return holesInSearchedArea;
    }

    public void setHolesInSearchedArea(List<DroneHoleInSearchedArea> holesInSearchedArea) {
        this.holesInSearchedArea = holesInSearchedArea;
    }
}
