package pl.mkapiczynski.dron.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.logging.Logger;
import org.openstreetmap.josm.data.coor.LatLon;

import pl.mkapiczynski.dron.database.HoleInSearchedArea;
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

	public static List<HoleInSearchedArea> findHoles(List<DegreeLocation> previousCircle,
			List<DegreeLocation> currentCircle, int dh, int currentCameraAngle, Location droneLocation) {
		HgtReader reader = new HgtReader();
		List<HoleInSearchedArea> holesInSearchedAre = new ArrayList<>();
		try {
			for (int i = 0; i < currentCircle.size(); i++) {
				for (int j = 0; j < previousCircle.size(); j++) {
					if (currentCircle.get(i).getDegree() == previousCircle.get(j).getDegree()) {
						int degree = currentCircle.get(i).getDegree();
						Location currentCircleLocation = currentCircle.get(i).getLocation();
						Location previousCircleLocation = previousCircle.get(j).getLocation();
						if (currentCircleLocationAltitudeIsBiggerThanPreviousCircleLocationAltitude(
								currentCircleLocation, previousCircleLocation)) {
							List<DegreeLocation> locationsOnCircle = new ArrayList<>();
							List<Integer> degrees = new ArrayList<>();
							degrees.add(degree);
							dh = (int) (droneLocation.getAltitude() - currentCircleLocation.getAltitude());
							List<Location> singleHoleLocations = new ArrayList<>();
							do {
								dh += 1;

								double radius = SearchedAreaHelper.calculateRadius(dh, currentCameraAngle);

								locationsOnCircle = SearchedAreaHelper.pointsAsCircle(droneLocation, radius, dh,
										degrees);
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
							if (!singleHoleLocations.isEmpty() && singleHoleLocations.size()>=2) {
								List<Location> filteredSingleHoleLocations = new ArrayList<>();
									filteredSingleHoleLocations.add(singleHoleLocations.get(0));
									filteredSingleHoleLocations.add(singleHoleLocations.get(singleHoleLocations.size()-1));
									HoleInSearchedArea hole = new HoleInSearchedArea();
									hole.setHoleLocations(filteredSingleHoleLocations);
									holesInSearchedAre.add(hole);
							}
						}
					}
				}
			}
		} catch (OutOfMemoryError e) {
			log.error("OUT OF MEMOTY " + e.getMessage());
		}
		return holesInSearchedAre;
	}
	
	private static boolean currentCircleLocationAltitudeIsBiggerThanPreviousCircleLocationAltitude(
			Location currentCircleLocation, Location previousCircleLocation) {
		if (Double.sum(currentCircleLocation.getAltitude(), -previousCircleLocation.getAltitude()) > 2) {
			return true;

		} else {
			return false;
		}
	}

	public static List<Location> convertDegreeLocationListToLocationList(List<DegreeLocation> degreeLocationList) {
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < degreeLocationList.size(); i++) {
			locationList.add(degreeLocationList.get(i).getLocation());
		}
		return locationList;
	}

	/*private static Location findMidpoint(Location loc1, Location loc2) {
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
	}*/

	public static void processDegrees(List<Integer> degrees, List<DegreeLocation> locationsOnCircle) {
		if (!degrees.isEmpty()) {
			degrees.clear();
			for (int i = 0; i < locationsOnCircle.size(); i++) {
				degrees.add(locationsOnCircle.get(i).getDegree());
			}
		} else {
			for (int i = 0; i < 360; i += 1) {
				degrees.add(i);
			}
		}
	}

	public static double calculateRadius(int dh, int cameraAngle) {
		double cameraAngleInRadians = (((cameraAngle/2) * 3.14)/180);
		return dh * Math.abs(Math.tan(cameraAngleInRadians));
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

		/*List<Location> currentSearchedAreaOuterPoints = getOuterPoints(currentSearchedArea, lastSearchedArea);
		List<Location> lastSearchedAreaOuterPoints = getOuterPoints(lastSearchedArea, currentSearchedArea);
		BigDecimal smallestDistanceForCurrentSearchedArea = null;
		int closestCurrentSeachedAreaIndex=0;
		int closestLastSearchedAreaIndex=0;
		for (int i = 0; i < currentSearchedAreaOuterPoints.size(); i++) {
			Location currentSearchedAreaLocation = currentSearchedAreaOuterPoints.get(i);
			BigDecimal smallestDistance = null;
			int tempClosesstLastSearcheadAreaIndex = 0;
			for (int j = 0; j < lastSearchedAreaOuterPoints.size(); j++) {
				Location lastSearchedAreaLocation = lastSearchedAreaOuterPoints.get(j);
				BigDecimal dist = distFrom(currentSearchedAreaLocation.getLatitude(),
						currentSearchedAreaLocation.getLongitude(), lastSearchedAreaLocation.getLatitude(),
						lastSearchedAreaLocation.getLongitude());
				if (smallestDistance == null) {
					smallestDistance = dist;
					tempClosesstLastSearcheadAreaIndex = j;
				} else if (dist.compareTo(smallestDistance) < 0) {
						smallestDistance = dist;
						tempClosesstLastSearcheadAreaIndex = j;
					}
			}
			if (smallestDistanceForCurrentSearchedArea == null) {
				smallestDistanceForCurrentSearchedArea = smallestDistance;
				closestCurrentSeachedAreaIndex = i;
				closestLastSearchedAreaIndex = tempClosesstLastSearcheadAreaIndex;
			} else if (smallestDistance.compareTo(smallestDistanceForCurrentSearchedArea) < 0) {
					smallestDistanceForCurrentSearchedArea = smallestDistance;
					closestCurrentSeachedAreaIndex = i;
					closestLastSearchedAreaIndex = tempClosesstLastSearcheadAreaIndex;
				}
			
		}
		List<Location> leftCurrent = new ArrayList<>();
		List<Location> rightCurrent = new ArrayList<>();
		for(int k=0; k<currentSearchedAreaOuterPoints.size();k++){
			if(k<=closestCurrentSeachedAreaIndex){
				leftCurrent.add(currentSearchedAreaOuterPoints.get(k));
			} else{
				rightCurrent.add(currentSearchedAreaOuterPoints.get(k));
			}
		}
		
		List<Location> leftLast = new ArrayList<>();
		List<Location> rightLast = new ArrayList<>();
		for(int k=0; k<lastSearchedAreaOuterPoints.size();k++){
			if(k<=closestLastSearchedAreaIndex){
				leftLast.add(lastSearchedAreaOuterPoints.get(k));
			} else{
				rightLast.add(lastSearchedAreaOuterPoints.get(k));
			}
		}
		
		newSearchedArea.addAll(leftCurrent);
		newSearchedArea.addAll(rightLast);
		newSearchedArea.addAll(leftLast);
		newSearchedArea.addAll(rightCurrent);*/
		
		/**
		 * TODO Do sprawdzenia, czy sortowanie tutaj jest potrzebne i czy
		 * poprawia, czy pogarsza wydajność
		 */
		/*List<Location> sortedCurrentSearchedArea = sortGeoPointsListByDistanceAndRemoveRepetitions(currentSearchedArea);
		List<Location> sortedLastSearchedArea = sortGeoPointsListByDistanceAndRemoveRepetitions(lastSearchedArea);

		List<Location> mutualPoints = getMutualPoints(sortedCurrentSearchedArea, sortedLastSearchedArea);

		newSearchedArea = addOuterPoints(sortedCurrentSearchedArea, sortedLastSearchedArea);

		newSearchedArea.addAll(mutualPoints);*/
		List<Location> currentSearchedAreaOuterPoints = getOuterPoints(currentSearchedArea, lastSearchedArea);
		List<Location> lastSearchedAreaOuterPoints = getOuterPoints(lastSearchedArea, currentSearchedArea);
		List<Location> mutualPoints = getMutualPoints(currentSearchedArea, lastSearchedArea);
		
		ArrayList<Location> points = new ArrayList<>();
		points.addAll(currentSearchedAreaOuterPoints);
		points.addAll(lastSearchedAreaOuterPoints);
		points.addAll(mutualPoints);
		
		QuickHull hull = new QuickHull();
		ArrayList<Location> hullLocations = hull.quickHull(points);
		newSearchedArea.addAll(hullLocations);
		return newSearchedArea;
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
	
	public static Location findNearesPointToRemovedHolePoint(Location location1, Location location2, List<Location> lastSearchedAreaLocations){
		double y1 = location1.getLatitude();
		double x1 = location1.getLongitude();
		double y2 = location2.getLatitude();
		double x2 = location2.getLongitude();
		/**
		 * Współczynniki prostej przechodzącej przez dwa punkty
		 */
		double a =0;
		if(x1!=x2){
			 a = (y1-y2) / (x1-x2);
		}
		double b=y1-a*x1;
		
		int k = findNearestPointIndex(location2, lastSearchedAreaLocations);
		Location nearestLocation = lastSearchedAreaLocations.get(k);
		if(!laysOnLine(a,b, nearestLocation)){
			lastSearchedAreaLocations.remove(nearestLocation);
			while(!laysOnLine(a,b, nearestLocation) && !lastSearchedAreaLocations.isEmpty()){
				k = findNearestPointIndex(location2, lastSearchedAreaLocations);
				if(!lastSearchedAreaLocations.isEmpty()){
					nearestLocation = lastSearchedAreaLocations.get(k);
				}
				lastSearchedAreaLocations.remove(nearestLocation);
			}
		}
		return nearestLocation;
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
					point1=point3;
					i=2;
				}
				point1=point2;
				point2=point3;
				i = 3;
			}
		}
		verticesLocations.addAll(threadSafeLocations);
		return verticesLocations;
	}
	
	private static boolean threePointsLayOnTheLine(Location point1, Location point2, Location point3){
		Double x1 = point1.getLatitude();
		Double y1 = point1.getLongitude();
		Double x2 = point2.getLatitude();
		Double y2 = point2.getLongitude();
		Double x3 = point3.getLatitude();
		Double y3 = point3.getLongitude();
		
		Double determinate = (x1*(y2-y3)) + (x2*(y3-y1)) + (x3*(y1-y2));
		log.info(determinate);
		if(Double.sum(determinate, 4.00E-12)>=0.5E-12 || Double.sum(determinate, 4.00E-12)<=0.5E-12 ){
			return true;
		} else{
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
							&& location1.getLongitude().compareTo(location2.getLongitude()) == 0 && !location1.equals(location2)) {
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
	
	private static double[] getLineCoeficcients(Location location1, Location location2){
		double[] coefficients = new double[2];
		double y1 = location1.getLatitude();
		double x1 = location1.getLongitude();
		double y2 = location2.getLatitude();
		double x2 = location2.getLongitude();
		/**
		 * Współczynniki prostej przechodzącej przez dwa punkty
		 */
		double a =0;
		if(x1!=x2){
			 a = (y1-y2) / (x1-x2);
		}
		double b=y1-a*x1;
		coefficients[0] = a;
		coefficients[1] = b;
		return coefficients;
	}
	
	private static boolean laysOnLine(double a, double b, Location point){
		Double x = point.getLatitude();
		Double y = point.getLongitude();
		Double calculatedY = (a*x)+b;
		if((Double.sum(y, -calculatedY)>-0.5) && (Double.sum(y, -calculatedY)<+0.5)){
			return true;
		} else{
			return false;
		}
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

	private static List<Location> getOuterPoints(List<Location> points, List<Location> outerFrom){
		List<Location> outerPoints = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			if (!pointInPolygon(points.get(i), outerFrom)) {
				outerPoints.add(points.get(i));
			}
		}
		return outerPoints;
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
				if ((pointFromAreaToAdd.getLatitude().compareTo(pointFromSearchedArea.getLatitude()) == 0)
						&& (pointFromAreaToAdd.getLongitude().compareTo(pointFromSearchedArea.getLongitude()) == 0)) {
					mutualPoints.add(pointFromSearchedArea);
				}
			}
		}
		return mutualPoints;
	}

	private static int findNearestPointIndex(Location point, List<Location> listToSearch) {
		int index = 0;
		BigDecimal dist =  new BigDecimal(0);
		for (int i = 0; i < listToSearch.size(); i++) {
			Location currentPoint = listToSearch.get(i);
			BigDecimal currentPointDist = distFrom(point.getLatitude(), point.getLongitude(), currentPoint.getLatitude(),
					currentPoint.getLongitude());
			if (i == 0) {
				index = i;
				dist = currentPointDist;
			} else if (currentPointDist.compareTo(dist)<0) {
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
		BigDecimal a = new BigDecimal ((Math.sin(dLat.doubleValue() / 2) * Math.sin(dLat.doubleValue() / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng.doubleValue() / 2) * Math.sin(dLng.doubleValue() / 2)));
		BigDecimal c = new BigDecimal(2 * Math.atan2(Math.sqrt(a.doubleValue()), Math.sqrt(1 - a.doubleValue())));
		BigDecimal dist = (earthRadius.multiply(c));
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
