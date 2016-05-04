package pl.mkapiczynski.dron.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.logging.Logger;
import org.openstreetmap.josm.data.coor.LatLon;

import pl.mkapiczynski.dron.database.HoleInSearchedArea;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.domain.Constants;
import pl.mkapiczynski.dron.domain.DegreeHole;
import pl.mkapiczynski.dron.domain.DegreeLocation;

/**
 * Helper wspomagający operacje wyznaczania obszaru przeszukanego
 * 
 * @author Michal Kapiczynski
 *
 */
public class SearchedAreaHelper {

	private static final Logger log = Logger.getLogger(SearchedAreaHelper.class);

	private static int degreeDifference = 1;

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

	public static List<Location> addLastSearchedAreaPointsWhichAreOutOfCurrentSearchedArea(
			List<Location> currentSearchedArea, List<Location> lastSearchedArea) {
		log.debug("Method addLastSearchedAreaPointsWhichAreOutOfCurrentSearchedArea started: " + new Date());
		List<Location> newSearchedArea = new ArrayList<>();
		List<Location> mergedLocations = mergeToLocationsLists(currentSearchedArea, lastSearchedArea);
		newSearchedArea = ConcaveHullHelper.getConcaveHull(mergedLocations);
		log.debug("Method addLastSearchedAreaPointsWhichAreOutOfCurrentSearchedArea ended: " + new Date());
		return newSearchedArea;
	}

	public static List<HoleInSearchedArea> getHoles(List<DegreeLocation> previousCircle,
			List<DegreeLocation> currentCircle, int dh, int currentCameraAngle, Location droneLocation) {
		List<DegreeHole> degreeHoles = getHolesSegregatedByDegrees(previousCircle, currentCircle, dh,
				currentCameraAngle, droneLocation);
		List<HoleInSearchedArea> holes = getMergedHolesFromHolesSegregatedByDegree(degreeHoles);
		return holes;
	}

	private static List<DegreeHole> getHolesSegregatedByDegrees(List<DegreeLocation> previousCircle,
			List<DegreeLocation> currentCircle, int dh, int currentCameraAngle, Location droneLocation) {
		List<DegreeHole> degreeHoleList = new ArrayList<>();
		HgtReader reader = new HgtReader();
		for (int degree = 0; degree < 360; degree += degreeDifference) {
			Location previousCircleLocation = getLocationForDegree(previousCircle, degree);
			Location currentCircleLocation = getLocationForDegree(currentCircle, degree);
			if (previousCircleLocation != null && currentCircleLocation != null) {
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
					if (singleHoleLocations != null && !singleHoleLocations.isEmpty()) {
						List<Location> filteredSingleHoleLocations = new ArrayList<>();
						filteredSingleHoleLocations.add(singleHoleLocations.get(0));
						filteredSingleHoleLocations.add(singleHoleLocations.get(singleHoleLocations.size() - 1));
						DegreeHole degreeHole = new DegreeHole();
						degreeHole.setDegree(degree);
						degreeHole.setLocaions(filteredSingleHoleLocations);
						degreeHoleList.add(degreeHole);
					}
				}
			}
		}
		return degreeHoleList;
	}

	private static List<HoleInSearchedArea> getMergedHolesFromHolesSegregatedByDegree(List<DegreeHole> degreeHoles) {
		List<HoleInSearchedArea> holes = new ArrayList<>();

		for (int degree = 0; degree < 360; degree += degreeDifference) {
			DegreeHole holeForCurrentDegree = getDegreeHoleInSearchedAreaFoDegree(degreeHoles, degree);
			if (holeForCurrentDegree != null) {
				List<Location> holeLocations = new ArrayList<>();
				holeLocations.addAll(holeForCurrentDegree.getLocaions());
				degreeHoles.remove(holeForCurrentDegree);

				for (int nextDegree = degree + degreeDifference; nextDegree < 360; nextDegree += degreeDifference) {
					DegreeHole holeForNextDegree = getDegreeHoleInSearchedAreaFoDegree(degreeHoles, nextDegree);
					if (holeForNextDegree != null) {
						holeLocations.addAll(holeForNextDegree.getLocaions());
						degreeHoles.remove(holeForNextDegree);
					} else {
						break;
					}
				}

				for (int nextDegree = degree - degreeDifference; nextDegree > (-358
						+ degree); degree -= degreeDifference) {
					int tempNextDegree = nextDegree;
					if (nextDegree < 0) {
						tempNextDegree = 360 + nextDegree;
					}
					DegreeHole holeForNextDegree = getDegreeHoleInSearchedAreaFoDegree(degreeHoles, tempNextDegree);
					if (holeForNextDegree != null) {
						holeLocations.addAll(holeForNextDegree.getLocaions());
						degreeHoles.remove(holeForNextDegree);
					} else {
						break;
					}
				}

				HoleInSearchedArea newHole = new HoleInSearchedArea();
				newHole.setHoleLocations(holeLocations);
				holes.add(newHole);
			}

		}
		return holes;
	}

	private static DegreeHole getDegreeHoleInSearchedAreaFoDegree(List<DegreeHole> degreeHoles, int degree) {
		for (int i = 0; i < degreeHoles.size(); i++) {
			if (degreeHoles.get(i).getDegree() == degree) {
				return degreeHoles.get(i);
			}
		}
		return null;
	}

	private static Location getLocationForDegree(List<DegreeLocation> locations, int degree) {
		for (int i = 0; i < locations.size(); i++) {
			if (locations.get(i).getDegree() == degree) {
				return locations.get(i).getLocation();
			}
		}
		return null;
	}

	public static List<HoleInSearchedArea> getConcaveHullsHoles(List<HoleInSearchedArea> holes) {
		List<HoleInSearchedArea> concaveHoles = new ArrayList<>();

		for (int i = 0; i < holes.size(); i++) {
			List<Location> singleDividedHoleLocations = holes.get(i).getHoleLocations();
			List<Location> concaveLocations = ConcaveHullHelper.getConcaveHull(singleDividedHoleLocations);
			HoleInSearchedArea newHole = new HoleInSearchedArea();
			newHole.setHoleLocations(concaveLocations);
			concaveHoles.add(newHole);
		}
		return concaveHoles;
	}

	private static boolean currentCircleLocationAltitudeIsBiggerThanPreviousCircleLocationAltitude(
			Location currentCircleLocation, Location previousCircleLocation) {
		if (Double.sum(currentCircleLocation.getAltitude(), -previousCircleLocation.getAltitude()) > 20) {
			return true;
		} else {
			return false;
		}
	}

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

	public static void processDegrees(List<Integer> degrees, List<DegreeLocation> locationsOnCircle) {
		if (!degrees.isEmpty()) {
			degrees.clear();
			for (int i = 0; i < locationsOnCircle.size(); i++) {
				degrees.add(locationsOnCircle.get(i).getDegree());
			}
		} else {
			for (int i = 0; i < 360; i += degreeDifference) {
				degrees.add(i);
			}
		}
	}

	public static double calculateRadius(int dh, int cameraAngle) {
		double cameraAngleInRadians = (((cameraAngle / 2) * 3.14) / 180);
		return dh * Math.abs(Math.tan(cameraAngleInRadians));
	}

	private static List<Location> mergeToLocationsLists(List<Location> list1, List<Location> list2) {
		List<Location> result = new ArrayList<>();
		result.addAll(list1);
		result.addAll(list2);
		return result;
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
