package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.GeoPoint;

@Local
@Stateless(name = "SearchedAreaService")
public class SearchedAreaServiceBean implements SearchedAreaService {
	public static final float DEG2RAD = (float) (Math.PI / 180.0);
	public static final float RAD2DEG = (float) (180.0 / Math.PI);

	public static final int RADIUS_EARTH_METERS = 6378137; // http://en.wikipedia.org/wiki/Earth_radius#Equatorial_radius
	public static final double METERS_PER_STATUTE_MILE = 1609.344; // http://en.wikipedia.org/wiki/Mile
	public static final double METERS_PER_NAUTICAL_MILE = 1852; // http://en.wikipedia.org/wiki/Nautical_mile
	public static final double FEET_PER_METER = 3.2808399; // http://en.wikipedia.org/wiki/Feet_%28unit_of_length%29
	public static final int EQUATORCIRCUMFENCE = (int) (2 * Math.PI * RADIUS_EARTH_METERS);

	@Override
	public SearchedArea calculateSearchedArea(Location geoLocation) {
		SearchedArea newSearchedArea = new SearchedArea();
		List<Location> newSearchedAreaLocations = poinsAsCircle(geoLocation, 20.0);
		newSearchedArea.setSearchedLocations(newSearchedAreaLocations);
		return newSearchedArea;
	}

	@Override
	public void updateSearchedArea(SearchedArea currentSearchedArea, SearchedArea lastSearchedArea) {
		List<Location> sortedSearchedAreaLocations = new ArrayList<>();
		if (currentSearchedArea != null && lastSearchedArea != null) {
			List<Location> currentSearchedAreaLocations = currentSearchedArea.getSearchedLocations();
			List<Location> lastSearchedAreaLocations = lastSearchedArea.getSearchedLocations();
			if (currentSearchedAreaLocations != null && !currentSearchedAreaLocations.isEmpty() && lastSearchedAreaLocations != null && !lastSearchedAreaLocations.isEmpty()) {
				List<Location> updatedCurrentSearchedArea = addPointsWhichAreOutOfCurrentSearchedArea(currentSearchedAreaLocations, lastSearchedAreaLocations);
				currentSearchedAreaLocations.clear();
				currentSearchedAreaLocations.addAll(updatedCurrentSearchedArea);
			} else if ((currentSearchedAreaLocations == null || currentSearchedAreaLocations.isEmpty()) && (lastSearchedAreaLocations != null && !lastSearchedAreaLocations.isEmpty())) {
				if(currentSearchedAreaLocations==null){
					currentSearchedAreaLocations = new ArrayList<>();
				}
				currentSearchedAreaLocations.addAll(lastSearchedAreaLocations);
			}
			if(!currentSearchedAreaLocations.isEmpty()){
				sortedSearchedAreaLocations = sortGeoPointsListByDistanceAndRemoveRepetitions(currentSearchedAreaLocations);
			}
		}
		currentSearchedArea.setSearchedLocations(sortedSearchedAreaLocations);
	}
	


	private List<Location> addPointsWhichAreOutOfCurrentSearchedArea(List<Location> currentSearchedArea,
			List<Location> areaToAdd) {
		List<Location> newSearchedArea = new ArrayList<>();

		List<Location> sortedCurrentSearchedArea = sortGeoPointsListByDistanceAndRemoveRepetitions(currentSearchedArea);
		List<Location> sortedAreaToAdd = sortGeoPointsListByDistanceAndRemoveRepetitions(areaToAdd);

		List<Location> mutualPoints = getMutualPoints(sortedCurrentSearchedArea, sortedAreaToAdd);

		for (int i = 0; i < sortedCurrentSearchedArea.size(); i++) {
			if (!pointInPolygon(sortedCurrentSearchedArea.get(i), sortedAreaToAdd)) {
				newSearchedArea.add(sortedCurrentSearchedArea.get(i));
			}
		}

		for (int i = 0; i < sortedAreaToAdd.size(); i++) {
			if (!pointInPolygon(sortedAreaToAdd.get(i), sortedCurrentSearchedArea)) {
				newSearchedArea.add(sortedAreaToAdd.get(i));
			}
		}

		newSearchedArea.addAll(mutualPoints);

		return newSearchedArea;
	}

	private List<Location> getMutualPoints(List<Location> searchedArea, List<Location> areaToAdd) {
		List<Location> mutualPoints = new ArrayList<>();
		for (int i = 0; i < areaToAdd.size(); i++) {
			Location pointFromAreaToAdd = areaToAdd.get(i);
			for (int j = 0; j < searchedArea.size(); j++) {
				Location pointFromSearchedArea = searchedArea.get(j);
				if (pointFromAreaToAdd.getLatitude() == pointFromSearchedArea.getLatitude()
						&& pointFromAreaToAdd.getLongitude() == pointFromSearchedArea.getLongitude()) {
					mutualPoints.add(pointFromSearchedArea);
				}
			}
		}
		return mutualPoints;
	}

	private List<Location> sortGeoPointsListByDistanceAndRemoveRepetitions(List<Location> searchedArea) {

		List<Location> orderedSearchedArea = new ArrayList<>();
		Location firstPoint = searchedArea.get(0);
		orderedSearchedArea.add(firstPoint);

		while (searchedArea.size() > 0) {
			Location point = orderedSearchedArea.get(orderedSearchedArea.size() - 1);
			int nearestPointIndex = findNearestPointIndex(point, searchedArea);
			Location nearestPoint = searchedArea.get(nearestPointIndex);
			if (nearesPointIsTheSamePoint(point, nearestPoint)) {
				searchedArea.remove(nearestPointIndex);
			} /*
				 * else if(nearesPointIsTheSamePoint(firstPoint, nearestPoint))
				 * { /* Dodaj ten punkt, ale potem zacznij od kolejnego punktu z
				 * searchedArea (od drugiej krawędzi). Pomiń szukanie
				 * najbliższego punktu dla tego jednego przypadku.
				 */
			/*
			 * orderedSearchedArea.add(searchedArea.remove(nearestPointIndex));
			 * if(searchedArea.size()>0) { List<Location> secondEdge =
			 * sortGeoPointsListByDistanceAndRemoveRepetitions(searchedArea);
			 * for (int i = 0; i < secondEdge.size(); i++) {
			 * orderedSearchedArea.add(secondEdge.get(i)); } } }
			 */ else {
				orderedSearchedArea.add(searchedArea.remove(nearestPointIndex));
			}
		}

		return orderedSearchedArea;
	}

	private int findNearestPointIndex(Location point, List<Location> listToSearch) {
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

	private double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; // meters
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = (earthRadius * c);

		return dist;
	}

	private boolean nearesPointIsTheSamePoint(Location point, Location nearestPoint) {
		if (point.getLatitude() == nearestPoint.getLatitude() && point.getLongitude() == nearestPoint.getLongitude()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean pointInPolygon(Location point, List<Location> path) {
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

	private boolean rayCrossesSegment(Location point, Location a, Location b) {
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

	private List<Location> poinsAsCircle(Location center, double radiusInMeters) {
		ArrayList<Location> circlePoints = new ArrayList<>();

		for (int f = 0; f < 360; f += 2) {
			Location onCircle = destinationPoint(center, radiusInMeters, (float) f);
			circlePoints.add(onCircle);
		}

		return circlePoints;
	}

	private Location destinationPoint(Location center, final double aDistanceInMeters, final float aBearingInDegrees) {

		// convert distance to angular distance
		final double dist = aDistanceInMeters / RADIUS_EARTH_METERS;

		// convert bearing to radians
		final float brng = DEG2RAD * aBearingInDegrees;

		// get current location in radians
		final double lat1 = DEG2RAD * center.getLatitude();
		final double lon1 = DEG2RAD * center.getLongitude();

		final double lat2 = Math
				.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
		final double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1),
				Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2));

		final double lat2deg = lat2 / DEG2RAD;
		final double lon2deg = lon2 / DEG2RAD;

		return new Location(lat2deg, lon2deg);
	}

}
