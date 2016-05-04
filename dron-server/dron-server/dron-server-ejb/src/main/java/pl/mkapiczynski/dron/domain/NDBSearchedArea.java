package pl.mkapiczynski.dron.domain;

import java.util.List;
/**
 * Niebazodanowa reprezentacja obszaru przeszukanego do komunikacji z aplikacją DronVision (aby ułatwić parse'owanie po tamtej stronie)
 * @author Michal Kapiczynski
 *
 */
public class NDBSearchedArea {
	List<GeoPoint> searchedAreaLocations;
	List<NDBHoleInSearchedArea> holesInSearchedArea;

	public List<GeoPoint> getSearchedAreaLocations() {
		return searchedAreaLocations;
	}

	public void setSearchedAreaLocations(List<GeoPoint> searchedAreaLocations) {
		this.searchedAreaLocations = searchedAreaLocations;
	}

	public List<NDBHoleInSearchedArea> getHolesInSearchedArea() {
		return holesInSearchedArea;
	}

	public void setHolesInSearchedArea(List<NDBHoleInSearchedArea> holesInSearchedArea) {
		this.holesInSearchedArea = holesInSearchedArea;
	}

}
