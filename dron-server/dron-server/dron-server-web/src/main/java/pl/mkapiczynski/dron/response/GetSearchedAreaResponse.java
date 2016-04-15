package pl.mkapiczynski.dron.response;

import java.util.List;

import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.domain.NDBSearchedArea;

public class GetSearchedAreaResponse {
	private NDBSearchedArea searchedArea;

	public NDBSearchedArea getSearchedArea() {
		return searchedArea;
	}

	public void setSearchedArea(NDBSearchedArea searchedArea) {
		this.searchedArea = searchedArea;
	}

}
