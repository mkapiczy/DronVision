package dron.mkapiczynski.pl.dronvision.domain;

import java.util.Comparator;

/**
 * Created by Miix on 2016-01-08.
 */
public class MyGeoPoint {
    private double latitude;
    private double longitude;
    private double altitude;

    public MyGeoPoint() {

    }

    public MyGeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MyGeoPoint(double latitude, double longitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    private static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private static Comparator<MyGeoPoint> createComparator(MyGeoPoint p)
    {
        final MyGeoPoint finalP = new MyGeoPoint(p.getLatitude(), p.getLongitude());
        return new Comparator<MyGeoPoint>()
        {
            @Override
            public int compare(MyGeoPoint p0, MyGeoPoint p1)
            {
                double ds0 = distFrom((float)finalP.getLatitude(),(float)finalP.getLongitude(), (float)p0.getLatitude(), (float)p0.getLongitude());
                double ds1 = distFrom((float)finalP.getLatitude(),(float)finalP.getLongitude(), (float)p1.getLatitude(), (float)p1.getLongitude());
                return Double.compare(ds0, ds1);
            }

        };
    }
}
