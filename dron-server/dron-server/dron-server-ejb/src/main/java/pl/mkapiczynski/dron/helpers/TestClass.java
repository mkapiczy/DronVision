package pl.mkapiczynski.dron.helpers;

import org.openstreetmap.josm.data.coor.LatLon;

public class TestClass {

	public static void main(String[] args) {
		HgtReader reader = new HgtReader();
		LatLon position = new LatLon(52.24423,21.073339);
		double elevation = reader.getElevationFromHgt(position);
		System.out.println(elevation);

	}

}
