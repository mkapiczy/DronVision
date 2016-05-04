package pl.mkapiczynski.dron.business;

import java.util.List;

import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.GeoPoint;

/**
 * Interfejs do obsługi operacji związanych z obliczaniem i uaktualnianiem obszaru przeszukanego
 * @author Michal Kapiczynski
 *
 */
public interface SearchedAreaService {
	public SearchedArea calculateSearchedArea(Location geoPoint, Integer maxCameraAngle);
	public void updateSearchedArea(SearchedArea currentSearchedArea, SearchedArea newSearchedArea);
	public void updateSearchedAreaHoles(SearchedArea currentSearchedArea, SearchedArea lastSearchedArea, SearchedArea recentSearchedArea);
}
