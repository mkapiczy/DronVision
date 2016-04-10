package pl.mkapiczynski.dron.domain;

import java.util.List;

public class NDBHoleInSearchedArea {
	List<GeoPoint> holeLocations;

	public List<GeoPoint> getHoleLocations() {
		return holeLocations;
	}

	public void setHoleLocations(List<GeoPoint> holeLocations) {
		this.holeLocations = holeLocations;
	}

}
