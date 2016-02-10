package pl.mkapiczynski.dron.business;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

@Local
@Stateless(name = "SearchedAreaService")
public class SearchedAreaServiceBean implements SearchedAreaService {

	@Override
	public List<GeoPoint> calculateSearchedArea(GeoPoint geoLocation) {
		List<GeoPoint> searchedAreaList = GeoPoint.pointsAsCircle(geoLocation, 20.0);
		return searchedAreaList;
	}

}
