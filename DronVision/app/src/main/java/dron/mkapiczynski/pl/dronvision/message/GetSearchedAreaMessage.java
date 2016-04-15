package dron.mkapiczynski.pl.dronvision.message;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import dron.mkapiczynski.pl.dronvision.domain.DroneSearchedArea;
import dron.mkapiczynski.pl.dronvision.domain.DroneSession;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;

/**
 * Created by Miix on 2016-02-12.
 */
public class GetSearchedAreaMessage {
    private DroneSearchedArea searchedArea;

    public DroneSearchedArea getSearchedArea() {
        return searchedArea;
    }

    public void setSearchedArea(DroneSearchedArea searchedArea) {
        this.searchedArea = searchedArea;
    }
}
