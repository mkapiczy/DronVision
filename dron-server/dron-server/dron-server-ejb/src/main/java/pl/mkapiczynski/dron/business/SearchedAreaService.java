package pl.mkapiczynski.dron.business;

import java.util.List;

import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.GeoPoint;

public interface SearchedAreaService {
	public SearchedArea calculateSearchedArea(Location geoPoint);
	public void updateSearchedArea(SearchedArea currentSearchedArea, SearchedArea newSearchedArea);
}
