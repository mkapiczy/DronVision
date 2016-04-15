package pl.mkapiczynski.dron.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;
import org.opensphere.geometry.algorithm.ConcaveHull;
import org.openstreetmap.josm.data.coor.LatLon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import pl.mkapiczynski.dron.database.HoleInSearchedArea;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.domain.Constants;
import pl.mkapiczynski.dron.domain.DegreeLocation;

public class SearchedAreaHelper {

	private static final Logger log = Logger.getLogger(SearchedAreaHelper.class);

	public static List<DegreeLocation> pointsAsCircle(Location center, double radiusInMeters, int dh,
			List<Integer> degrees) {
		List<DegreeLocation> circlePoints = new ArrayList<>();
		for (int i = 0; i < degrees.size(); i++) {
			int f = degrees.get(i);
			Location onCircle = destinationPoint(center, radiusInMeters, (float) f);
			onCircle.setAltitude(center.getAltitude() - dh);
			DegreeLocation newLocation = new DegreeLocation();
			newLocation.setLocation(onCircle);
			newLocation.setDegree(f);
			circlePoints.add(newLocation);
		}
		return circlePoints;
	}

	public static int getMinimumAltitudeDifference(List<DegreeLocation> realData, List<Location> modelData) {
		double minimumAltitudeDifference = 2000;
		for (int i = 0; i < realData.size(); i++) {
			Location realPoint = realData.get(i).getLocation();
			for (int j = 0; j < modelData.size(); j++) {
				Location modelPoint = modelData.get(j);
				if (Double.compare(realPoint.getLatitude(), modelPoint.getLatitude()) == 0
						&& Double.compare(realPoint.getLongitude(), modelPoint.getLongitude()) == 0) {
					if (realPoint.getAltitude() != null && modelPoint.getAltitude() != null) {
						double difference = realPoint.getAltitude() - modelPoint.getAltitude();
						if (difference < minimumAltitudeDifference) {
							minimumAltitudeDifference = difference;
						}
					}
				}
			}

		}
		return (int) minimumAltitudeDifference;
	}

	public static List<Location> getModelData(List<DegreeLocation> realData) {
		HgtReader reader = new HgtReader();
		List<Location> result = new ArrayList<>();
		for (int i = 0; i < realData.size(); i++) {
			Location modelLocation = new Location();
			Location realLocation = realData.get(i).getLocation();
			modelLocation.setLatitude(realLocation.getLatitude());
			modelLocation.setLongitude(realLocation.getLongitude());
			double modelLocationAltitude = reader
					.getElevationFromHgt(new LatLon(realLocation.getLatitude(), realLocation.getLongitude()));
			if (modelLocationAltitude != 0) {
				modelLocation.setAltitude(modelLocationAltitude);
			}
			if (i > (realData.size() / 2)) {
				modelLocation.setAltitude(modelLocationAltitude);
			}
			result.add(modelLocation);
		}
		return result;
	}

	public static List<HoleInSearchedArea> findHoles(List<DegreeLocation> previousCircle,
			List<DegreeLocation> currentCircle, int dh, int currentCameraAngle, Location droneLocation) {
		HgtReader reader = new HgtReader();
		List<HoleInSearchedArea> holesInSearchedAre = new ArrayList<>();
		for (int i = 0; i < currentCircle.size(); i += 1) {
			for (int j = 0; j < previousCircle.size(); j += 1) {
				if (currentCircle.get(i).getDegree() == previousCircle.get(j).getDegree()) {
					int degree = currentCircle.get(i).getDegree();
					Location currentCircleLocation = currentCircle.get(i).getLocation();
					Location previousCircleLocation = previousCircle.get(j).getLocation();
					if (currentCircleLocationAltitudeIsBiggerThanPreviousCircleLocationAltitude(currentCircleLocation,
							previousCircleLocation)) {
						List<DegreeLocation> locationsOnCircle = new ArrayList<>();
						List<Integer> degrees = new ArrayList<>();
						degrees.add(degree);
						dh = (int) (droneLocation.getAltitude() - currentCircleLocation.getAltitude());
						List<Location> singleHoleLocations = new ArrayList<>();
						do {
							dh += 1;

							double radius = SearchedAreaHelper.calculateRadius(dh, currentCameraAngle);

							locationsOnCircle = SearchedAreaHelper.pointsAsCircle(droneLocation, radius, dh, degrees);
							Location loc = new Location();
							if (locationsOnCircle != null && !locationsOnCircle.isEmpty()) {
								loc = locationsOnCircle.get(0).getLocation();
							}

							double modelAltitude = reader
									.getElevationFromHgt(new LatLon(loc.getLatitude(), loc.getLongitude()));
							if (loc.getAltitude() > modelAltitude) {
								singleHoleLocations.add(loc);
							}

						} while (new Double(Double.sum(droneLocation.getAltitude(), -dh))
								.compareTo(previousCircleLocation.getAltitude()) > 0);
						if (!singleHoleLocations.isEmpty() && singleHoleLocations.size() >= 2) {
							List<Location> filteredSingleHoleLocations = new ArrayList<>();
							filteredSingleHoleLocations.add(singleHoleLocations.get(0));
							filteredSingleHoleLocations.add(singleHoleLocations.get(singleHoleLocations.size() - 1));
							HoleInSearchedArea hole = new HoleInSearchedArea();
							hole.setHoleLocations(filteredSingleHoleLocations);
							holesInSearchedAre.add(hole);
						}
					}
				}
			}
		}
		return holesInSearchedAre;
	}

	private static boolean currentCircleLocationAltitudeIsBiggerThanPreviousCircleLocationAltitude(
			Location currentCircleLocation, Location previousCircleLocation) {
		if (Double.sum(currentCircleLocation.getAltitude(), -previousCircleLocation.getAltitude()) > 20) {
			return true;
		} else {
			return false;
		}
	}

	public static void processDegrees(List<Integer> degrees, List<DegreeLocation> locationsOnCircle) {
		if (!degrees.isEmpty()) {
			degrees.clear();
			for (int i = 0; i < locationsOnCircle.size(); i++) {
				degrees.add(locationsOnCircle.get(i).getDegree());
			}
		} else {
			for (int i = 0; i < 360; i += 2) {
				degrees.add(i);
			}
		}
	}

	public static double calculateRadius(int dh, int cameraAngle) {
		double cameraAngleInRadians = (((cameraAngle / 2) * 3.14) / 180);
		return dh * Math.abs(Math.tan(cameraAngleInRadians));
	}

	public static List<DegreeLocation> findLocationsCrossingWithTheGround(List<DegreeLocation> realData,
			List<Location> modelData, boolean remove) {
		List<DegreeLocation> result = new ArrayList<>();
		List<DegreeLocation> realToIterate = new ArrayList<>();
		realToIterate.addAll(realData);
		for (int i = 0; i < realToIterate.size(); i++) {
			Location realPoint = realToIterate.get(i).getLocation();
			for (int j = 0; j < modelData.size(); j++) {
				Location modelPoint = modelData.get(j);
				if (pointIsTheSamePoint(realPoint, modelPoint)) {
					if (modelPoint.getAltitude().compareTo(realPoint.getAltitude()) >= 0) {
						DegreeLocation degreeLocation = new DegreeLocation();
						degreeLocation.setLocation(modelPoint);
						degreeLocation.setDegree(realToIterate.get(i).getDegree());
						result.add(degreeLocation);
						if (remove) {
							removePoint(realData, realPoint);
						}
						break;
					}
				}
			}

		}
		return result;
	}

	public static List<Location> addLastSearchedAreaPointsWhichAreOutOfCurrentSearchedArea(
			List<Location> currentSearchedArea, List<Location> lastSearchedArea) {
		log.info("Method addLastSearchedAreaPointsWhichAreOutOfCurrentSearchedArea started: " + new Date());
		List<Location> newSearchedArea = new ArrayList<>();
		newSearchedArea = concaveHull(currentSearchedArea, lastSearchedArea);
		log.info("Method addLastSearchedAreaPointsWhichAreOutOfCurrentSearchedArea ended: " + new Date());
		return newSearchedArea;
	}

	private static List<Location> concaveHull(List<Location> currentSearchedArea, List<Location> lastSearchedArea) {
		log.info("Method concaveHull started: " + new Date());
		GeometryFactory gf = new GeometryFactory();

		densifyPoints(currentSearchedArea, 0.0001);
		densifyPoints(currentSearchedArea, 0.0001);

		List<Geometry> list = new ArrayList<>();

		for (int i = 0; i < currentSearchedArea.size(); i++) {
			list.add(gf.createPoint(new Coordinate(currentSearchedArea.get(i).getLatitude(),
					currentSearchedArea.get(i).getLongitude())));
		}
		for (int i = 0; i < lastSearchedArea.size(); i++) {
			list.add(gf.createPoint(
					new Coordinate(lastSearchedArea.get(i).getLatitude(), lastSearchedArea.get(i).getLongitude())));
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
		log.info("Method concaveHull ended: " + new Date());
		return result;
	}

	private static void densifyPoints(List<Location> list, double threshold) {
		CopyOnWriteArrayList<Location> threadSafeLocations = new CopyOnWriteArrayList<>();
		threadSafeLocations.addAll(list);
		Location previous = null;
		for (int i = 0; i < threadSafeLocations.size(); i++) {
			Location current = threadSafeLocations.get(i);
			if (previous != null) {
				if (length(previous, current) > threshold) {
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

	private static double length(Location p1, Location p2) {
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

	private static Location destinationPoint(Location center, final double aDistanceInMeters,
			final float aBearingInDegrees) {

		// convert distance to angular distance
		final double dist = aDistanceInMeters / Constants.RADIUS_EARTH_METERS;

		// convert bearing to radians
		final float brng = Constants.DEG2RAD * aBearingInDegrees;

		// get current location in radians
		final double lat1 = Constants.DEG2RAD * center.getLatitude();
		final double lon1 = Constants.DEG2RAD * center.getLongitude();

		final double lat2 = Math
				.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
		final double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1),
				Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2));

		final double lat2deg = lat2 / Constants.DEG2RAD;
		final double lon2deg = lon2 / Constants.DEG2RAD;

		return new Location(lat2deg, lon2deg);
	}

	private static void removePoint(List<DegreeLocation> list, Location point) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLocation().getLatitude().compareTo(point.getLatitude()) == 0
					&& list.get(i).getLocation().getLongitude().compareTo(point.getLongitude()) == 0) {
				list.remove(i);
			}
		}
	}

	private static boolean pointIsTheSamePoint(Location point, Location nearestPoint) {
		if ((point.getLatitude().compareTo(nearestPoint.getLatitude()) == 0)
				&& (point.getLongitude().compareTo(nearestPoint.getLongitude()) == 0)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean pointInPolygon(Location point, List<Location> path) {
		// ray casting alogrithm
		// http://rosettacode.org/wiki/Ray-casting_algorithm
		int crossings = 0;
		// path.remove(path.size()-1); //remove the last point that is added
		// automatically by getPoints()

		// for each edge
		for (int i = 0; i < path.size(); i++) {
			Location a = path.get(i);
			int j = i + 1;
			// to close the last edge, you have to take the first point of your
			// polygon
			if (j >= path.size()) {
				j = 0;
			}
			Location b = path.get(j);
			if (rayCrossesSegment(point, a, b)) {
				crossings++;
			}
		}

		// odd number of crossings?
		return (crossings % 2 == 1);
	}

	private static boolean rayCrossesSegment(Location point, Location a, Location b) {
		// Ray Casting algorithm checks, for each segment, if the point is 1) to
		// the left of the segment and 2) not above nor below the segment. If
		// these two conditions are met, it returns true
		double px = point.getLongitude(), py = point.getLatitude(), ax = a.getLongitude(), ay = a.getLatitude(),
				bx = b.getLongitude(), by = b.getLatitude();
		if (ay > by) {
			ax = b.getLongitude();
			ay = b.getLatitude();
			bx = a.getLongitude();
			by = a.getLatitude();
		}
		// alter longitude to cater for 180 degree crossings
		if (px < 0 || ax < 0 || bx < 0) {
			px += 360;
			ax += 360;
			bx += 360;
		}
		// if the point has the same latitude as a or b, increase slightly py
		if (py == ay || py == by)
			py += 0.00000001;

		// if the point is above, below or to the right of the segment, it
		// returns false
		if ((py > by || py < ay) || (px > Math.max(ax, bx))) {
			return false;
		}
		// if the point is not above, below or to the right and is to the left,
		// return true
		else if (px < Math.min(ax, bx)) {
			return true;
		}
		// if the two above conditions are not met, you have to compare the
		// slope of segment [a,b] (the red one here) and segment [a,p] (the blue
		// one here) to see if your point is to the left of segment [a,b] or not
		else {
			double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
			double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
			return (blue >= red);
		}

	}
}
