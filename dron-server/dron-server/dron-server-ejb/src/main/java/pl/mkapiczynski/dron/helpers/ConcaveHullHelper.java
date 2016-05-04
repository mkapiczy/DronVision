package pl.mkapiczynski.dron.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;
import org.opensphere.geometry.algorithm.ConcaveHull;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import pl.mkapiczynski.dron.database.Location;

/**
 * Helper do wyznaczania otoczki wklęsłej
 * 
 * @author Michal Kapiczynski
 *
 */
public class ConcaveHullHelper {
	
	private static final Logger log = Logger.getLogger(ConcaveHullHelper.class);
	
	public static List<Location> getConcaveHull(List<Location> locations) {
		log.debug("Method concaveHull started: " + new Date());
		if (locations != null && locations.size() > 3) {
			GeometryFactory gf = new GeometryFactory();

			densifyPoints(locations, 0.0001);
			//densifyPoints(locations, 0.0001);

			List<Geometry> list = new ArrayList<>();

			for (int i = 0; i < locations.size(); i++) {
				list.add(gf
						.createPoint(new Coordinate(locations.get(i).getLatitude(), locations.get(i).getLongitude())));
			}

			Geometry[] points = list.toArray(new Geometry[list.size()]);
			GeometryCollection geometries = new GeometryCollection(points, gf);

			ConcaveHull ch = new ConcaveHull(geometries, 0.0005);
			Geometry concave = ch.getConcaveHull();
			Coordinate[] concaveCoordinates = concave.getCoordinates();

			List<Coordinate> coords = Arrays.asList(concaveCoordinates);

			List<Location> result = new ArrayList<>();
			for (int i = 0; i < coords.size(); i++) {
				result.add(new Location(coords.get(i).x, coords.get(i).y));
			}
			log.debug("Method concaveHull ended: " + new Date());
			return result;
		} else {
			return locations;
		}

	}
	
	

	/**
	 * Metoda iteruje po liście punktów, i jeśli jakieś dwa punkty są od siebie oddalone o wiekszą odległośc niż
	 * @threshold wówczas pomiędzy te dwa punkty wstawia dodatkowe punkty.
	 * W efekcie wszystkie punkty listy są od siebie oddalone o odległośc nie większoą niż threshold
	 * @param list
	 * @param threshold
	 */
	private static void densifyPoints(List<Location> list, double threshold) {
		CopyOnWriteArrayList<Location> threadSafeLocations = new CopyOnWriteArrayList<>();
		threadSafeLocations.addAll(list);
		Location previous = null;
		for (int i = 0; i < threadSafeLocations.size(); i++) {
			Location current = threadSafeLocations.get(i);
			if (previous != null) {
				if (distanceBetweenTwoPoints(previous, current) > threshold) {
					Location midPoint = getMidpoint(previous, current);
					threadSafeLocations.add(i, midPoint);
					densifyPoints(threadSafeLocations, threshold);
				}
			}
			previous = current;
		}

		list.clear();
		list.addAll(threadSafeLocations);
	}
	
	

	private static Location getMidpoint(Location loc1, Location loc2) {
		Double latitude = (loc1.getLatitude() + loc2.getLatitude()) / 2;
		Double longitude = (loc1.getLongitude() + loc2.getLongitude()) / 2;
		return new Location(latitude, longitude);
	}

	private static double distanceBetweenTwoPoints(Location p1, Location p2) {
		double length = 0;
		double x1 = p1.getLatitude();
		double y1 = p2.getLongitude();

		double x2 = p2.getLatitude();
		double y2 = p2.getLongitude();

		double dx = x2 - x1;
		double dy = y2 - y1;

		length += Math.sqrt(dx * dx + dy * dy);

		return length;
	}
}
