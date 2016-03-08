package pl.mkapiczynski.dron.helpers;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.openstreetmap.josm.data.coor.LatLon;

import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.domain.Constants;
import pl.mkapiczynski.dron.domain.DegreeLocation;
import pl.mkapiczynski.dron.domain.GeoPoint;

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

	public static double calculateDistanceBetweenTwooLocations(Location prevLocation, Location currLocation) {
		int EarthRadius = 6371; // km
		double dLat = Math.abs((currLocation.getLatitude() - prevLocation.getLatitude()) * Math.PI / 180);
		double dLong = Math.abs((currLocation.getLongitude() - prevLocation.getLongitude()) * Math.PI / 180);
		double prevLatitudeInRadians = prevLocation.getLatitude() * Math.PI / 180;
		double currLongitudeInRadians = currLocation.getLongitude() * Math.PI / 180;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLong / 2) * Math.sin(dLong / 2)
				* Math.cos(prevLatitudeInRadians) * Math.cos(currLongitudeInRadians);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = EarthRadius * c;
		return distance;
	}

	public static List<List<Location>> findHoles(List<DegreeLocation> previousCircle,
			List<DegreeLocation> currentCircle, int dh, int currentCameraAngle, Location droneLocation) {
		List<List<Location>> holesInSearchedAre = new ArrayList<>();
		for (int i = 0; i < currentCircle.size(); i++) {
			for (int j = 0; j < previousCircle.size(); j++) {
				if (currentCircle.get(i).getDegree() == previousCircle.get(j).getDegree()) {
					Location currentLocation = currentCircle.get(i).getLocation();
					Location previousLocation = previousCircle.get(j).getLocation();
					if (previousLocation.getAltitude().compareTo(currentLocation.getAltitude()) < 0) {
						List<DegreeLocation> locationsOnCircle = new ArrayList<>();
						List<Integer> degrees = new ArrayList<>();
						for (int k = 0; k < 360; k += 10) {
							degrees.add(k);
						}
						dh = (int) (droneLocation.getAltitude() - currentLocation.getAltitude());
						do {
							dh += 1;

							double radius = SearchedAreaHelper.calculateRadius(dh, currentCameraAngle);

							locationsOnCircle = SearchedAreaHelper.pointsAsCircle(droneLocation, radius, dh, degrees);

							Location loc = getLocationFromCircleForDegree(currentCircle.get(i).getDegree(),
									locationsOnCircle);
							HgtReader reader = new HgtReader();
							double modelAltitude = reader
									.getElevationFromHgt(new LatLon(loc.getLatitude(), loc.getLongitude()));
							List<Location> singleHole = new ArrayList<>();
							if (modelAltitude < loc.getAltitude()) {
								singleHole.add(loc);
							}
							if (!singleHole.isEmpty()) {
								holesInSearchedAre.add(singleHole);
							}

						} while (((droneLocation.getAltitude() - dh) > previousLocation.getAltitude()));
					}
				}
			}
		}
		return holesInSearchedAre;
	}

	private static Location getLocationFromCircleForDegree(int degree, List<DegreeLocation> locationsOnCircle) {
		Location result = new Location();
		for (int i = 0; i < locationsOnCircle.size(); i++) {
			if (locationsOnCircle.get(i).getDegree() == degree) {
				result = locationsOnCircle.get(i).getLocation();
			}
		}
		return result;
	}

	public static List<Location> convertDegreeLocationListToLocationList(List<DegreeLocation> degreeLocationList) {
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < degreeLocationList.size(); i++) {
			locationList.add(degreeLocationList.get(i).getLocation());
		}
		return locationList;
	}

	private static Location findMidpoint(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.getLatitude());
		double lon1 = Math.toRadians(loc1.getLongitude());
		double lat2 = Math.toRadians(loc2.getLatitude());
		double lon2 = Math.toRadians(loc2.getLongitude());

		double bx = Math.cos(lat2) * Math.cos(lon2 - lon1);
		double by = Math.cos(lat2) * Math.sin(lon2 - lon1);

		double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
				Math.sqrt((Math.cos(lat1) + bx) * (Math.cos(lat1) + bx) + by * by));
		double lon3 = lon1 + Math.atan2(by, Math.cos(lat1) * bx);

		return new Location(lat3, lon3);
	}

	public static void processDegrees(List<Integer> degrees, List<DegreeLocation> locationsOnCircle) {
		if (!degrees.isEmpty()) {
			degrees.clear();
			for (int i = 0; i < locationsOnCircle.size(); i++) {
				degrees.add(locationsOnCircle.get(i).getDegree());
			}
		} else {
			for (int i = 0; i < 360; i += 10) {
				degrees.add(i);
			}
		}
	}

	public static double calculateRadius(int dh, int cameraAngle) {
		return dh * Math.tan(cameraAngle) * 2;
	}

	/**
	 * Do poprawy
	 * 
	 */
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
		List<Location> newSearchedArea = new ArrayList<>();

		/**
		 * TODO Do sprawdzenia, czy sortowanie tutaj jest potrzebne i czy
		 * poprawia, czy pogarsza wydajność
		 */
		List<Location> sortedCurrentSearchedArea = sortGeoPointsListByDistanceAndRemoveRepetitions(currentSearchedArea);
		List<Location> sortedLastSearchedArea = sortGeoPointsListByDistanceAndRemoveRepetitions(lastSearchedArea);

		List<Location> mutualPoints = getMutualPoints(sortedCurrentSearchedArea, sortedLastSearchedArea);

		newSearchedArea = addOuterPoints(sortedCurrentSearchedArea, sortedLastSearchedArea);

		newSearchedArea.addAll(mutualPoints);

		return newSearchedArea;
	}

	public static List<Location> sortGeoPointsListByDistanceAndRemoveRepetitions(List<Location> searchedArea) {
		List<Location> orderedSearchedArea = new ArrayList<>();
		if (searchedArea != null && !searchedArea.isEmpty()) {
			Location firstPoint = searchedArea.get(0);
			orderedSearchedArea.add(firstPoint);

			while (searchedArea.size() > 0) {
				Location lastOrderedSearchedAreaPoint = orderedSearchedArea.get(orderedSearchedArea.size() - 1);
				int nearestPointIndex = findNearestPointIndex(lastOrderedSearchedAreaPoint, searchedArea);
				Location nearestPoint = searchedArea.get(nearestPointIndex);
				if (pointIsTheSamePoint(lastOrderedSearchedAreaPoint, nearestPoint)) {
					searchedArea.remove(nearestPointIndex);
				} else if (pointIsTheSamePoint(firstPoint, nearestPoint)) {
					/**
					 * TODO Do sprawdzenia, czy działa. (Raczej nie :o)
					 */
					/*
					 * Dodaj ten punkt, ale potem zacznij od kolejnego punktu z
					 * searchedArea (od drugiej krawędzi). Pomiń szukanie
					 * najbliższego punktu dla tego jednego przypadku.
					 */

					orderedSearchedArea.add(searchedArea.remove(nearestPointIndex));
					if (searchedArea.size() > 0) {
						List<Location> secondEdge = sortGeoPointsListByDistanceAndRemoveRepetitions(searchedArea);
						for (int i = 0; i < secondEdge.size(); i++) {
							orderedSearchedArea.add(secondEdge.get(i));
						}
					}
				} else {
					orderedSearchedArea.add(searchedArea.remove(nearestPointIndex));
				}
			}

		}
		return orderedSearchedArea;
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

	private static List<Location> addOuterPoints(List<Location> currentSearchedArea, List<Location> lastSearchedArea) {
		List<Location> newSearchedArea = new ArrayList<>();
		for (int i = 0; i < currentSearchedArea.size(); i++) {
			if (!pointInPolygon(currentSearchedArea.get(i), lastSearchedArea)) {
				newSearchedArea.add(currentSearchedArea.get(i));
			}
		}
		for (int i = 0; i < lastSearchedArea.size(); i++) {
			if (!pointInPolygon(lastSearchedArea.get(i), currentSearchedArea)) {
				newSearchedArea.add(lastSearchedArea.get(i));
			}
		}
		return newSearchedArea;
	}

	private static List<Location> getMutualPoints(List<Location> searchedArea, List<Location> areaToAdd) {
		List<Location> mutualPoints = new ArrayList<>();
		for (int i = 0; i < areaToAdd.size(); i++) {
			Location pointFromAreaToAdd = areaToAdd.get(i);
			for (int j = 0; j < searchedArea.size(); j++) {
				Location pointFromSearchedArea = searchedArea.get(j);
				if ((pointFromAreaToAdd.getLatitude().compareTo(pointFromSearchedArea.getLatitude().doubleValue()) == 0)
						&& (pointFromAreaToAdd.getLongitude().compareTo(pointFromSearchedArea.getLongitude()) == 0)) {
					mutualPoints.add(pointFromSearchedArea);
				}
			}
		}
		return mutualPoints;
	}

	private static int findNearestPointIndex(Location point, List<Location> listToSearch) {
		int index = 0;
		double dist = 0;
		for (int i = 0; i < listToSearch.size(); i++) {
			Location currentPoint = listToSearch.get(i);
			double currentPointDist = distFrom(point.getLatitude(), point.getLongitude(), currentPoint.getLatitude(),
					currentPoint.getLongitude());
			if (i == 0) {
				index = i;
				dist = currentPointDist;
			} else if (currentPointDist < dist) {
				index = i;
				dist = currentPointDist;
			}
		}
		return index;
	}

	private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; // meters
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = (earthRadius * c);

		return dist;
	}

	private static boolean pointIsTheSamePoint(Location point, Location nearestPoint) {
		if ((point.getLatitude().compareTo(nearestPoint.getLatitude()) == 0)
				&& (point.getLongitude().compareTo(nearestPoint.getLongitude()) == 0)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean pointInPolygon(Location point, List<Location> path) {
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

	/**
	 * TODO Najprawdopodobniej do wywalenia. Do sprawdzenia, czy nei jest lepsze
	 * od pointInPolygon
	 */
	private boolean coordinateInRegion(List<GeoPoint> region, GeoPoint coord) {
		int i, j;
		boolean isInside = false;
		// create an array of coordinates from the region boundary list
		GeoPoint[] verts = region.toArray(new GeoPoint[region.size()]);
		int sides = verts.length;
		for (i = 0, j = sides - 1; i < sides; j = i++) {
			// verifying if your coordinate is inside your region
			if ((((verts[i].getLongitude() <= coord.getLongitude()) && (coord.getLongitude() < verts[j].getLongitude()))
					|| ((verts[j].getLongitude() <= coord.getLongitude())
							&& (coord.getLongitude() < verts[i].getLongitude())))
					&& (coord.getLatitude() < (verts[j].getLatitude() - verts[i].getLatitude())
							* (coord.getLongitude() - verts[i].getLongitude())
							/ (verts[j].getLongitude() - verts[i].getLongitude()) + verts[i].getLatitude())) {
				isInside = !isInside;
			}
		}
		return isInside;
	}

}
