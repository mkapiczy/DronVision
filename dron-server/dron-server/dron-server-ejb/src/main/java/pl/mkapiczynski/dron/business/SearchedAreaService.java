package pl.mkapiczynski.dron.business;

import java.util.List;

import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

public interface SearchedAreaService {
	public List<GeoPoint> calculateSearchedArea(GeoPoint geoPoint);
}
