package dron.mkapiczynski.pl.dronvision.domain;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Miix on 2016-04-07.
 */

/**
 * Klasa reprezentująca dziury w obszarze przeszukanym wykorzystywana przy wizualizacji
 */
public class MapHoleInSearchedArea {
    List<GeoPoint> holeLocations;

    public List<GeoPoint> getHoleLocations() {
        return holeLocations;
    }

    public void setHoleLocations(List<GeoPoint> holeLocations) {
        this.holeLocations = holeLocations;
    }
}
