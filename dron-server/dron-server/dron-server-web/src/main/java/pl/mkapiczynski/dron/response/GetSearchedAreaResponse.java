package pl.mkapiczynski.dron.response;

import java.util.List;

import pl.mkapiczynski.dron.domain.GeoPoint;

public class GetSearchedAreaResponse {
	private List<GeoPoint> searchedArea;

	public List<GeoPoint> getSearchedArea() {
		return searchedArea;
	}

	public void setSearchedArea(List<GeoPoint> searchedArea) {
		this.searchedArea = searchedArea;
	}

}
