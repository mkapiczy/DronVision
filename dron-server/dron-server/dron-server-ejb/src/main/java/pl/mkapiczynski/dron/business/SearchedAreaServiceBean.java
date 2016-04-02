package pl.mkapiczynski.dron.business;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.jboss.logging.Logger;
import org.openstreetmap.josm.data.coor.LatLon;

import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.domain.DegreeLocation;
import pl.mkapiczynski.dron.helpers.HgtReader;
import pl.mkapiczynski.dron.helpers.SearchedAreaHelper;

@Local
@Stateless(name = "SearchedAreaService")
public class SearchedAreaServiceBean implements SearchedAreaService {

	private static final Logger log = Logger.getLogger(SearchedAreaServiceBean.class);

	@Override
	public SearchedArea calculateSearchedArea(Location droneLocation) {
		SearchedArea newSearchedArea = new SearchedArea();
		List<Location> newSearchedAreaLocations = new ArrayList<>();
		int oboveTheGroundDronesAltitude = 30;

		// dobre dane 49.074444444444445,22.726388888888888
		// 49.07527777777778, 22.725
		// 49.099917, 22.746356
		HgtReader reader = new HgtReader();
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
			int maxCameraAngle = 60;
			List<DegreeLocation> previousCircle = new ArrayList<>();
			List<DegreeLocation> currentCircle = new ArrayList<>();

			for (int currentCameraAngle = maxCameraAngle; currentCameraAngle > 0; currentCameraAngle -= 10) {
				currentCircle.clear();
				List<DegreeLocation> locationsOnCircle = new ArrayList<>();
				List<Integer> degrees = new ArrayList<>();
				int dh = 2;
				do {
					SearchedAreaHelper.processDegrees(degrees, locationsOnCircle);

					double radius = SearchedAreaHelper.calculateRadius(dh, currentCameraAngle);

					locationsOnCircle = SearchedAreaHelper.pointsAsCircle(droneLocation, radius, dh, degrees);

					List<Location> modelData = SearchedAreaHelper.getModelData(locationsOnCircle);

					if (dh == 2) {
						int minimumAltitudeDifferenceBetweenModelAndRealData = SearchedAreaHelper
								.getMinimumAltitudeDifference(locationsOnCircle, modelData);
						dh += minimumAltitudeDifferenceBetweenModelAndRealData / 2;
					}
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
					//SearchedAreaHelper.findHoles(previousCircle, currentCircle, dh, currentCameraAngle, droneLocation);
					previousCircle.clear();
					previousCircle.addAll(currentCircle);
				}
				if (currentCameraAngle == maxCameraAngle) {
					for (int k = 0; k < currentCircle.size(); k++) {
						newSearchedAreaLocations.add(currentCircle.get(k).getLocation());
					}
					List<Location> sorted = SearchedAreaHelper
							.sortGeoPointsListByDistanceAndRemoveRepetitions(newSearchedAreaLocations);
					newSearchedArea.setSearchedLocations(sorted);
				}

			}
		}

		return newSearchedArea;
	}


	@Override
	public void updateSearchedArea(SearchedArea currentSearchedArea, SearchedArea lastSearchedArea) {
		if (currentSearchedArea != null && lastSearchedArea != null) {
			List<Location> currentSearchedAreaLocations = currentSearchedArea.getSearchedLocations();
			List<Location> lastSearchedAreaLocations = lastSearchedArea.getSearchedLocations();
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
			List<Location> sortedSearchedAreaLocations = SearchedAreaHelper
					.sortGeoPointsListByDistanceAndRemoveRepetitions(currentSearchedAreaLocations);
			currentSearchedArea.setSearchedLocations(sortedSearchedAreaLocations);
		}
	}

}
