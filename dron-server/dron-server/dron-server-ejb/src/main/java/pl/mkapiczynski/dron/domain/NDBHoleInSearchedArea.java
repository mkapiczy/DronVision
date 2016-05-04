package pl.mkapiczynski.dron.domain;

import java.util.List;

/**
 * Niebazodanowa reprezentacja dziury w obszarze przeszukanym do komunikacji z aplikacją DronVision 
 * (aby ułatwić parse'owanie po tamtej stronie)
 * 
 * @author Michal Kapiczynski
 *
 */
public class NDBHoleInSearchedArea {
	List<GeoPoint> holeLocations;

	public List<GeoPoint> getHoleLocations() {
		return holeLocations;
	}

	public void setHoleLocations(List<GeoPoint> holeLocations) {
		this.holeLocations = holeLocations;
	}

}
