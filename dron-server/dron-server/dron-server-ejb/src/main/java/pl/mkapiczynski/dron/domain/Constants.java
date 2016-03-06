package pl.mkapiczynski.dron.domain;

public class Constants {
	public static final String GPS_TRACKED_DEVICE_TYPE = "GPSTracker";
	public static final String GEO_DATA_MESSAGE_TYPE = "GeoDataMessage";
	public static final String TRACKER_LOGIN_MESSAGE_TYPE = "TrackerLoginMessage";
	public static final String CLIENT_LOGIN_MESSAGE_TYPE = "ClientLoginMessage";
	public static final String TRACKER_SIMULATION_MESSAGE_TYPE = "TrackerSimulationMessage";

	/**
	 * SearchedArea Constants
	 */
	public static final float DEG2RAD = (float) (Math.PI / 180.0);
	public static final float RAD2DEG = (float) (180.0 / Math.PI);

	public static final int RADIUS_EARTH_METERS = 6378137; // http://en.wikipedia.org/wiki/Earth_radius#Equatorial_radius
	public static final double METERS_PER_STATUTE_MILE = 1609.344; // http://en.wikipedia.org/wiki/Mile
	public static final double METERS_PER_NAUTICAL_MILE = 1852; // http://en.wikipedia.org/wiki/Nautical_mile
	public static final double FEET_PER_METER = 3.2808399; // http://en.wikipedia.org/wiki/Feet_%28unit_of_length%29
	public static final int EQUATORCIRCUMFENCE = (int) (2 * Math.PI * RADIUS_EARTH_METERS);

}
