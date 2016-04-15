package pl.mkapiczynski.dron.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.domain.DegreeLocation;

public class UnusedMethods {
	private static final Logger log = Logger.getLogger(UnusedMethods.class);
	
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
	
	public static List<Location> convertDegreeLocationListToLocationList(List<DegreeLocation> degreeLocationList) {
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < degreeLocationList.size(); i++) {
			locationList.add(degreeLocationList.get(i).getLocation());
		}
		return locationList;
	}

	private static List<Location> getOuterPoints(List<Location> points, List<Location> outerFrom) {
		List<Location> outerPoints = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			if (!SearchedAreaHelper.pointInPolygon(points.get(i), outerFrom)) {
				outerPoints.add(points.get(i));
			}
		}
		return outerPoints;
	}

	private static List<Location> addOuterPoints(List<Location> currentSearchedArea, List<Location> lastSearchedArea) {
		List<Location> newSearchedArea = new ArrayList<>();
		for (int i = 0; i < currentSearchedArea.size(); i++) {
			if (!SearchedAreaHelper.pointInPolygon(currentSearchedArea.get(i), lastSearchedArea)) {
				newSearchedArea.add(currentSearchedArea.get(i));
			}
		}
		for (int i = 0; i < lastSearchedArea.size(); i++) {
			if (!SearchedAreaHelper.pointInPolygon(lastSearchedArea.get(i), currentSearchedArea)) {
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
				if ((pointFromAreaToAdd.getLatitude().compareTo(pointFromSearchedArea.getLatitude()) == 0)
						&& (pointFromAreaToAdd.getLongitude().compareTo(pointFromSearchedArea.getLongitude()) == 0)) {
					mutualPoints.add(pointFromSearchedArea);
				}
			}
		}
		return mutualPoints;
	}
	
	public static Location findNearesPointToRemovedHolePoint(Location location1, Location location2,
			List<Location> lastSearchedAreaLocations) {
		double y1 = location1.getLatitude();
		double x1 = location1.getLongitude();
		double y2 = location2.getLatitude();
		double x2 = location2.getLongitude();
		/**
		 * Współczynniki prostej przechodzącej przez dwa punkty
		 */
		double a = 0;
		if (x1 != x2) {
			a = (y1 - y2) / (x1 - x2);
		}
		double b = y1 - a * x1;

		int k = findNearestPointIndex(location2, lastSearchedAreaLocations);
		Location nearestLocation = lastSearchedAreaLocations.get(k);
		if (!laysOnLine(a, b, nearestLocation)) {
			lastSearchedAreaLocations.remove(nearestLocation);
			while (!laysOnLine(a, b, nearestLocation) && !lastSearchedAreaLocations.isEmpty()) {
				k = findNearestPointIndex(location2, lastSearchedAreaLocations);
				if (!lastSearchedAreaLocations.isEmpty()) {
					nearestLocation = lastSearchedAreaLocations.get(k);
				}
				lastSearchedAreaLocations.remove(nearestLocation);
			}
		}
		return nearestLocation;
	}

	public static List<Location> sortGeoPointsListByDistanceAndRemoveRepetitions(List<Location> searchedArea) {
		List<Location> orderedSearchedArea = new ArrayList<>();
		if (searchedArea != null && !searchedArea.isEmpty()) {
			Location firstPoint = searchedArea.get(0);
			orderedSearchedArea.add(searchedArea.remove(0));
			Location lastAddedPoint = firstPoint;
			while (searchedArea.size() > 0) {
				int nearestPointIndex = findNearestPointIndex(lastAddedPoint, searchedArea);
				Location nearestPoint = searchedArea.get(nearestPointIndex);
				orderedSearchedArea.add(nearestPoint);
				lastAddedPoint = searchedArea.remove(nearestPointIndex);

			}

		}
		return orderedSearchedArea;
	}
	private static int findNearestPointIndex(Location point, List<Location> listToSearch) {
		int index = 0;
		BigDecimal dist = new BigDecimal(0);
		for (int i = 0; i < listToSearch.size(); i++) {
			Location currentPoint = listToSearch.get(i);
			BigDecimal currentPointDist = distFrom(point.getLatitude(), point.getLongitude(),
					currentPoint.getLatitude(), currentPoint.getLongitude());
			if (i == 0) {
				index = i;
				dist = currentPointDist;
			} else if (currentPointDist.compareTo(dist) < 0) {
				index = i;
				dist = currentPointDist;
			}
		}
		return index;
	}
	private static BigDecimal distFrom(Double lat1, Double lng1, Double lat2, Double lng2) {
		BigDecimal earthRadius = new BigDecimal(6371000); // meters
		BigDecimal dLat = new BigDecimal(Math.toRadians(lat2 - lat1));
		BigDecimal dLng = new BigDecimal(Math.toRadians(lng2 - lng1));
		BigDecimal a = new BigDecimal((Math.sin(dLat.doubleValue() / 2) * Math.sin(dLat.doubleValue() / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng.doubleValue() / 2)
						* Math.sin(dLng.doubleValue() / 2)));
		BigDecimal c = new BigDecimal(2 * Math.atan2(Math.sqrt(a.doubleValue()), Math.sqrt(1 - a.doubleValue())));
		BigDecimal dist = (earthRadius.multiply(c));
		return dist;
	}
	public static boolean segmentsIntersects(Location p1, Location p2, Location p3, Location p4) {
		BigDecimal x1 = new BigDecimal(p1.getLatitude());
		BigDecimal y1 = new BigDecimal(p1.getLongitude());
		BigDecimal x2 = new BigDecimal(p2.getLatitude());
		BigDecimal y2 = new BigDecimal(p2.getLongitude());

		BigDecimal x3 = new BigDecimal(p3.getLatitude());
		BigDecimal y3 = new BigDecimal(p3.getLongitude());
		BigDecimal x4 = new BigDecimal(p4.getLatitude());
		BigDecimal y4 = new BigDecimal(p4.getLongitude());

		BigDecimal maxX1X2 = null;
		if (x1.compareTo(x2) > 0) {
			maxX1X2 = x1;
		} else {
			maxX1X2 = x2;
		}

		BigDecimal minx3x4 = null;
		if (x3.compareTo(x4) > 0) {
			minx3x4 = x4;
		} else {
			minx3x4 = x3;
		}

		if (maxX1X2.compareTo(minx3x4) < 0) {
			return false;
		}
		BigDecimal a1 = null;
		if (x1.compareTo(x2) != 0) {
			a1 = (y1.subtract(y2)).divide((x1.subtract(x2)), 15, RoundingMode.HALF_UP);
		} else {
			a1 = BigDecimal.ZERO;
		}
		BigDecimal a2 = null;
		if (x3.compareTo(x4) != 0) {
			a2 = (y3.subtract(y4)).divide((x3.subtract(x4)), 15, RoundingMode.HALF_UP);
		} else {
			a2 = BigDecimal.ZERO;
		}
		BigDecimal b1 = y1.subtract(a1.multiply(x1));
		BigDecimal b2 = y3.subtract(a2.multiply(x3));

		if (a1.compareTo(a2) == 0) {
			return false; // równoległe
		}

		BigDecimal xa = (b2.subtract(b1)).divide((a1.subtract(a2)), 15, RoundingMode.HALF_UP);

		BigDecimal maxOfMinimumX = null;
		if ((xa.compareTo(x1) < 0 && xa.compareTo(x2) < 0 && xa.compareTo(x3) < 0 && xa.compareTo(x4) < 0)
				|| (xa.compareTo(x1) > 0 && xa.compareTo(x2) > 0 && xa.compareTo(x3) > 0 && xa.compareTo(x4) > 0)) {
			return false;
		}
		return true;
	}

	public static List<Location> getOnlyVerticesPoints(List<Location> locations) {
		List<Location> verticesLocations = new ArrayList<>();
		CopyOnWriteArrayList<Location> threadSafeLocations = new CopyOnWriteArrayList<>();
		threadSafeLocations.addAll(locations);
		Iterator<Location> locationsIterator = threadSafeLocations.listIterator();
		Location point1 = null;
		Location point2 = null;
		Location point3 = null;
		int i = 1;
		while (locationsIterator.hasNext()) {
			if (i == 1) {
				point1 = locationsIterator.next();
				i++;
			} else if (i == 2) {
				point2 = locationsIterator.next();
				i++;
			} else if (i == 3) {
				point3 = locationsIterator.next();
				if (threePointsLayOnTheLine(point1, point2, point3)) {
					threadSafeLocations.remove(point2);
					point1 = point3;
					i = 2;
				}
				point1 = point2;
				point2 = point3;
				i = 3;
			}
		}
		verticesLocations.addAll(threadSafeLocations);
		return verticesLocations;
	}

	private static boolean threePointsLayOnTheLine(Location point1, Location point2, Location point3) {
		Double x1 = point1.getLatitude();
		Double y1 = point1.getLongitude();
		Double x2 = point2.getLatitude();
		Double y2 = point2.getLongitude();
		Double x3 = point3.getLatitude();
		Double y3 = point3.getLongitude();

		Double determinate = (x1 * (y2 - y3)) + (x2 * (y3 - y1)) + (x3 * (y1 - y2));
		log.info(determinate);
		if (Double.sum(determinate, 4.00E-12) >= 0.5E-12 || Double.sum(determinate, 4.00E-12) <= 0.5E-12) {
			return true;
		} else {
			return false;
		}
	}

	



	private static boolean laysOnLine(double a, double b, Location point) {
		Double x = point.getLatitude();
		Double y = point.getLongitude();
		Double calculatedY = (a * x) + b;
		if ((Double.sum(y, -calculatedY) > -0.5) && (Double.sum(y, -calculatedY) < +0.5)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static void removeRepetitions(List<Location> locations) {
		CopyOnWriteArrayList<Location> threadSafeLocations = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<Location> secondThreadSafeLocations = new CopyOnWriteArrayList<>();
		threadSafeLocations.addAll(locations);
		secondThreadSafeLocations.addAll(locations);
		Iterator<Location> iterator = threadSafeLocations.listIterator();
		while (iterator.hasNext()) {
			Location location1 = iterator.next();
			Iterator<Location> secondIterator = secondThreadSafeLocations.listIterator();
			while (secondIterator.hasNext()) {
				Location location2 = secondIterator.next();
				try {
					if (location1.getLatitude().compareTo(location2.getLatitude()) == 0
							&& location1.getLongitude().compareTo(location2.getLongitude()) == 0
							&& !location1.equals(location2)) {
						threadSafeLocations.remove(location1);
					}
				} catch (NullPointerException e) {
					log.error(e);
				}
			}
		}
		locations.clear();
		locations.addAll(threadSafeLocations);
	}
	
	private static double[] getLineCoeficcients(Location location1, Location location2) {
		double[] coefficients = new double[2];
		double y1 = location1.getLatitude();
		double x1 = location1.getLongitude();
		double y2 = location2.getLatitude();
		double x2 = location2.getLongitude();
		/**
		 * Współczynniki prostej przechodzącej przez dwa punkty
		 */
		double a = 0;
		if (x1 != x2) {
			a = (y1 - y2) / (x1 - x2);
		}
		double b = y1 - a * x1;
		coefficients[0] = a;
		coefficients[1] = b;
		return coefficients;
	}
}
