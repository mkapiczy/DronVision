package pl.mkapiczynski.dron.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.jboss.logging.Logger;
import org.openstreetmap.josm.data.coor.LatLon;

import pl.mkapiczynski.dron.database.HoleInSearchedArea;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.DegreeLocation;
import pl.mkapiczynski.dron.helpers.HgtReader;
import pl.mkapiczynski.dron.helpers.SearchedAreaHelper;

@Local
@Stateless(name = "SearchedAreaService")
public class SearchedAreaServiceBean implements SearchedAreaService {

	private static final Logger log = Logger.getLogger(SearchedAreaServiceBean.class);
	private static HgtReader reader = new HgtReader();

	@Override
	public SearchedArea calculateSearchedArea(Location droneLocation, Integer maxCameraAngle) {
		log.debug("Method calculateSearchedArea started: " + new Date());
		SearchedArea newSearchedArea = new SearchedArea();
		List<Location> newSearchedAreaLocations = new ArrayList<>();
		List<HoleInSearchedArea> holes = new ArrayList<>();
		double oboveTheGroundDronesAltitude = droneLocation.getAltitude();
		if (oboveTheGroundDronesAltitude == 0.0) {
			oboveTheGroundDronesAltitude = 30;
		}

		// dobre dane 49.074444444444445,22.726388888888888
		// 49.07527777777778, 22.725
		// 49.099917, 22.746356

		// do dziury 49.07666666666667, 22.724722222222223
		// 11x 6, 10x2
		double dronesPositionModelAltitude = reader
				.getElevationFromHgt(new LatLon(droneLocation.getLatitude(), droneLocation.getLongitude()));

		
		/*
		 * try (PrintWriter out = new PrintWriter(new BufferedWriter(new
		 * FileWriter("altitudes.txt", true)))) { out.println("GPS altitude: " +
		 * droneLocation.getAltitude() + " | Model altitude: " +
		 * dronesPositionModelAltitude); } catch (IOException e) {
		 * log.error("Exception while operating with file : " + e); }
		 */

		if (dronesPositionModelAltitude != 0) {
			droneLocation.setAltitude(dronesPositionModelAltitude + oboveTheGroundDronesAltitude);
			if (maxCameraAngle == null) {
				maxCameraAngle = 60;
			}
			List<DegreeLocation> previousCircle = new ArrayList<>();
			List<DegreeLocation> currentCircle = new ArrayList<>();

			for (int currentCameraAngle = maxCameraAngle; currentCameraAngle > 0; currentCameraAngle -= 5) {
				currentCircle.clear();
				List<DegreeLocation> locationsOnCircle = new ArrayList<>();
				List<Integer> degrees = new ArrayList<>();
				int dh = 2;
				do {
					SearchedAreaHelper.processDegrees(degrees, locationsOnCircle);

					double radius = SearchedAreaHelper.calculateRadius(dh, currentCameraAngle);

					locationsOnCircle = SearchedAreaHelper.pointsAsCircle(droneLocation, radius, dh, degrees);

					List<Location> modelData = SearchedAreaHelper.getModelData(locationsOnCircle);

					/*
					 * if (dh == 2) { int
					 * minimumAltitudeDifferenceBetweenModelAndRealData =
					 * SearchedAreaHelper
					 * .getMinimumAltitudeDifference(locationsOnCircle,
					 * modelData); dh +=
					 * minimumAltitudeDifferenceBetweenModelAndRealData / 2; }
					 */
					List<DegreeLocation> newPoints = SearchedAreaHelper
							.findLocationsCrossingWithTheGround(locationsOnCircle, modelData, true);
					currentCircle.addAll(newPoints);
					if (newPoints.isEmpty()) {
						dh += 6;
					} else {
						dh += 2;
					}

				} while (!degrees.isEmpty());

				if (previousCircle.isEmpty()) {
					previousCircle.addAll(currentCircle);
				} else {
					List<HoleInSearchedArea> holesFromIteration = SearchedAreaHelper.getHoles(previousCircle,
							currentCircle, dh, currentCameraAngle, droneLocation);
					holes.addAll(holesFromIteration);
					previousCircle.clear();
					previousCircle.addAll(currentCircle);
				}
				if (currentCameraAngle == maxCameraAngle) {
					for (int i = 0; i < 360; i++) {
						for (int k = 0; k < currentCircle.size(); k+=2) {
							if (currentCircle.get(k).getDegree() == i) {
								newSearchedAreaLocations.add(currentCircle.get(k).getLocation());
							}
						}
					}
					
					newSearchedArea.setSearchedLocations(newSearchedAreaLocations);
				}

			}
		}
		List<HoleInSearchedArea> concaveHulls = SearchedAreaHelper.getConcaveHullsHoles(holes);
		newSearchedArea.setHolesInSearchedArea(concaveHulls);
		log.debug("Method calculateSearchedArea ended: " + new Date());
		return newSearchedArea;
	}

	@Override
	public void updateSearchedArea(SearchedArea currentSearchedArea, SearchedArea lastSearchedArea) {
		log.debug("Method updateSearchedArea started: " + new Date());
		if (currentSearchedArea != null && lastSearchedArea != null) {
			List<Location> currentSearchedAreaLocations = currentSearchedArea.getSearchedLocations();
			List<Location> lastSearchedAreaLocations = lastSearchedArea.getSearchedLocations();
			List<Location> updatedSearchedAreaLocations = updateSearchedAreaLocationWithLastSearchedAreaLocation(
					currentSearchedAreaLocations, lastSearchedAreaLocations);
			currentSearchedArea.setSearchedLocations(updatedSearchedAreaLocations);
		}
		log.debug("Method updateSearchedArea ended: " + new Date());
	}

	private List<Location> updateSearchedAreaLocationWithLastSearchedAreaLocation(
			List<Location> currentSearchedAreaLocations, List<Location> lastSearchedAreaLocations) {
		log.debug("Method updateSearchedAreaLocationWithLastSearchedAreaLocation started: " + new Date());
		if (currentSearchedAreaLocations != null && !currentSearchedAreaLocations.isEmpty()
				&& lastSearchedAreaLocations != null && !lastSearchedAreaLocations.isEmpty()) {
			List<Location> updatedCurrentSearchedAreaLocations = SearchedAreaHelper
					.addLastSearchedAreaPointsWhichAreOutOfCurrentSearchedArea(currentSearchedAreaLocations,
							lastSearchedAreaLocations);
			currentSearchedAreaLocations.clear();
			currentSearchedAreaLocations.addAll(updatedCurrentSearchedAreaLocations);
		} else if ((currentSearchedAreaLocations == null || currentSearchedAreaLocations.isEmpty())
				&& (lastSearchedAreaLocations != null && !lastSearchedAreaLocations.isEmpty())) {
			if (currentSearchedAreaLocations == null) {
				currentSearchedAreaLocations = new ArrayList<>();
			}
			currentSearchedAreaLocations.addAll(lastSearchedAreaLocations);
		}
		log.debug("Method updateSearchedAreaLocationWithLastSearchedAreaLocation ended: " + new Date());
		return currentSearchedAreaLocations;
	}

	@Override
	public void updateSearchedAreaHoles(SearchedArea currentSearchedArea, SearchedArea lastSearchedArea,
			SearchedArea recentSearchedArea) {
		log.debug("Method updateSearchedAreaHoles started: " + new Date());

		List<Location> recentSearchedAreaLocations = recentSearchedArea.getSearchedLocations();
		List<HoleInSearchedArea> currentSearchedAreaHoles = currentSearchedArea.getHolesInSearchedArea();
		currentSearchedAreaHoles.addAll(lastSearchedArea.getHolesInSearchedArea());

		CopyOnWriteArrayList<HoleInSearchedArea> threadSafeCurrentSearchedAreaHoles = new CopyOnWriteArrayList<>();
		threadSafeCurrentSearchedAreaHoles.addAll(currentSearchedAreaHoles);

		Iterator<HoleInSearchedArea> holeIterator = threadSafeCurrentSearchedAreaHoles.iterator();
		List<HoleInSearchedArea> holesToRemove = new ArrayList<>();
		while (holeIterator.hasNext()) {
			HoleInSearchedArea currentIteratedHole = holeIterator.next();
			if (currentIteratedHole.getHoleLocations() != null) {
				Iterator<Location> locationsIterator = currentIteratedHole.getHoleLocations().iterator();
				List<Location> locationsToRemove = new ArrayList();
				while (locationsIterator.hasNext()) {
					Location currentIteratedHoleLocation = locationsIterator.next();
					if (SearchedAreaHelper.pointInPolygon(currentIteratedHoleLocation, recentSearchedAreaLocations)
							|| !SearchedAreaHelper.pointInPolygon(currentIteratedHoleLocation,
									currentSearchedArea.getSearchedLocations())) {
						locationsToRemove.add(currentIteratedHoleLocation);
					}
				}
				if(!locationsToRemove.isEmpty()){
					currentIteratedHole.getHoleLocations().removeAll(locationsToRemove);
				}
				if (currentIteratedHole.getHoleLocations().isEmpty()) {
					holesToRemove.add(currentIteratedHole);
				}
			}
		}
		threadSafeCurrentSearchedAreaHoles.removeAll(holesToRemove);
		currentSearchedAreaHoles.clear();
		currentSearchedAreaHoles.addAll(threadSafeCurrentSearchedAreaHoles);
		if(recentSearchedArea.getHolesInSearchedArea()!=null && !currentSearchedArea.getSearchedLocations().isEmpty()){
			currentSearchedAreaHoles.addAll(recentSearchedArea.getHolesInSearchedArea());
			recentSearchedArea.getHolesInSearchedArea().clear();
		}
		
		log.debug("Method updateSearchedAreaHoles ended: " + new Date());
	}
}
