package pl.mkapiczynski.dron.business;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
	private static HgtReader reader = new  HgtReader();

	@Override
	public SearchedArea calculateSearchedArea(Location droneLocation, Integer maxCameraAngle) { 
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

		log.info("GPS altitude: " + droneLocation.getAltitude() + " | Model altitude: " + dronesPositionModelAltitude);
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("altitudes.txt", true)))) {
		    out.println("GPS altitude: " + droneLocation.getAltitude() + " | Model altitude: " + dronesPositionModelAltitude);
		}catch (IOException e) {
		   log.error("Exception while operating with file : " + e);
		}
		
		if (dronesPositionModelAltitude != 0) {
			droneLocation.setAltitude(dronesPositionModelAltitude + oboveTheGroundDronesAltitude);
			if(maxCameraAngle==null){
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

					/*if (dh == 2) {
						int minimumAltitudeDifferenceBetweenModelAndRealData = SearchedAreaHelper
								.getMinimumAltitudeDifference(locationsOnCircle, modelData);
						dh += minimumAltitudeDifferenceBetweenModelAndRealData / 2;
					}*/
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
					List<HoleInSearchedArea> holesFromIteration = SearchedAreaHelper.findHoles(previousCircle, currentCircle, dh, currentCameraAngle, droneLocation);
					holes.addAll(holesFromIteration);
					previousCircle.clear();
					previousCircle.addAll(currentCircle);
				}
				if (currentCameraAngle == maxCameraAngle) {
					for (int i = 0; i < 360; i++) {
						for (int k = 0; k < currentCircle.size(); k++) {
							if (currentCircle.get(k).getDegree() == i) {
								newSearchedAreaLocations.add(currentCircle.get(k).getLocation());
							}
						}
					}
					/*List<Location> sorted = SearchedAreaHelper
							.sortGeoPointsListByDistanceAndRemoveRepetitions(newSearchedAreaLocations);*/
					//List<Location> onlyVerticesLocations = SearchedAreaHelper.getOnlyVerticesPoints(newSearchedAreaLocations);
					newSearchedArea.setSearchedLocations(newSearchedAreaLocations);
				}

			}
		}
		log.info("Holes final amount " + holes.size());
		newSearchedArea.setHolesInSearchedArea(holes);

		return newSearchedArea;
	}


	@Override
	public void updateSearchedArea(SearchedArea currentSearchedArea, SearchedArea lastSearchedArea) {
		if (currentSearchedArea != null && lastSearchedArea != null) {
			List<Location> currentSearchedAreaLocations = currentSearchedArea.getSearchedLocations();
			List<Location> lastSearchedAreaLocations = lastSearchedArea.getSearchedLocations();
			List<Location> updatedSearchedAreaLocations = updateSearchedAreaLocationWithLastSearchedAreaLocation(currentSearchedAreaLocations, lastSearchedAreaLocations);
			currentSearchedArea.setSearchedLocations(updatedSearchedAreaLocations);
		}
	}
	
	private List<Location> updateSearchedAreaLocationWithLastSearchedAreaLocation(List<Location> currentSearchedAreaLocations, List<Location> lastSearchedAreaLocations){
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
		
		return currentSearchedAreaLocations;
	}
	
	@Override
	public void updateSearchedAreaHoles(SearchedArea currentSearchedArea, SearchedArea lastSearchedArea,
			SearchedArea recentSearchedArea) {
		List<HoleInSearchedArea> currentSearchedAreaHoles = currentSearchedArea.getHolesInSearchedArea();
		List<HoleInSearchedArea> lastSearchedAreaHoles = lastSearchedArea.getHolesInSearchedArea();
		List<Location> recentSearchedAreaLocations = recentSearchedArea.getSearchedLocations();
		currentSearchedAreaHoles.addAll(lastSearchedAreaHoles);
		CopyOnWriteArrayList<HoleInSearchedArea> threadSafeCurrentSearchedAreaHoles = new CopyOnWriteArrayList<>();
		threadSafeCurrentSearchedAreaHoles.addAll(currentSearchedAreaHoles);
		Iterator<HoleInSearchedArea> iterator = threadSafeCurrentSearchedAreaHoles.listIterator();
		while (iterator.hasNext()) {
			HoleInSearchedArea currentIteratedHole = iterator.next();
			for (int i = 0; i < currentIteratedHole.getHoleLocations().size(); i++) {
				Location holeLocation = currentIteratedHole.getHoleLocations().get(i);
				boolean holeLocationInRecentSearchedArea = SearchedAreaHelper.pointInPolygon(holeLocation,
						recentSearchedAreaLocations);
				if (holeLocationInRecentSearchedArea) {
					threadSafeCurrentSearchedAreaHoles.remove(currentIteratedHole);
				}
			}

		}
		currentSearchedAreaHoles.clear();
		currentSearchedAreaHoles.addAll(threadSafeCurrentSearchedAreaHoles);
	}
	/**
	 * Dodaj najbliższy punkt z nowej searchedArea leżący na prostej
	 * tych dwóch punktów
	 */
	/*
	 * List<Location> tempLocations = new ArrayList<>();
	 * tempLocations.addAll(recentSearchedAreaLocations); Location
	 * locationToAdd =
	 * SearchedAreaHelper.findNearesPointToRemovedHolePoint(
	 * holeLocation1, holeLocation2, tempLocations);
	 * currentSearchedAreaHoles.add(holeLocation2);
	 * currentSearchedAreaHoles.add(locationToAdd);
	 * currentSearchedAreaHoles.add(holeLocation1);
	 */
}
