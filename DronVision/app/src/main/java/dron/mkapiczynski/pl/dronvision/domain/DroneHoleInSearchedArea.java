package dron.mkapiczynski.pl.dronvision.domain;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Miix on 2016-04-07.
 */

/**
 * Klasa reprezentująca dziury w obszarze przeszukanym wykorzystywana przy komunikacji z serwerem
 */
public class DroneHoleInSearchedArea {
    List<MyGeoPoint> holeLocations;

    public List<MyGeoPoint> getHoleLocations() {
        return holeLocations;
    }

    public void setHoleLocations(List<MyGeoPoint> holeLocations) {
        this.holeLocations = holeLocations;
    }
}
