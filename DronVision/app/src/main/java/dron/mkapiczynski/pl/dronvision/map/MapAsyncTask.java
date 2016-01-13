package dron.mkapiczynski.pl.dronvision.map;

import android.app.Activity;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.service.DronService;
import dron.mkapiczynski.pl.dronvision.service.DronServiceBean;

/**
 * Created by Miix on 2016-01-09.
 */
public class MapAsyncTask extends AsyncTask<Void, Void, Boolean> {
    DronService dronService = new DronServiceBean();

    private Activity activity;
    private Drone drone;
    private Set<Drone> drones;
    private MapView mapView;
    private List<Overlay> mapOverlays = new ArrayList<>();

    public MapAsyncTask(MapView mapView, Drone drone, Set<Drone> drones, Activity activity) {
        this.drone = drone;
        this.drones = drones;
        this.activity = activity;
        this.mapView = mapView;
        //mapView = (MapView) activity.findViewById(R.id.MapView);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mapOverlays.clear();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        dronService.updateDronesSet(drones, drone);
        addScaleBarOverlayToMapView();
        updateDronesOnMapView(drones);
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //GeoPoint mapCenter = getLastDroneInSetLocation(drones);
        mapView.getOverlays().clear();
        mapView.getOverlays().addAll(mapOverlays);
        //mapView.invalidate();
        mapView.postInvalidateOnAnimation();
        MapController mapController = (MapController) mapView.getController();
        mapController.animateTo(drone.getCurrentPosition());
    }

    @Override
    protected void onCancelled() {
    }

    private void updateDronesOnMapView(Set<Drone> drones){
        Iterator<Drone> droneIterator = drones.iterator();
        while (droneIterator.hasNext()) {
            Drone currentIteratedDrone = droneIterator.next();
            updateDroneLastPositionMarkerOnMap(currentIteratedDrone);
            updateDroneLastSearchedAreaOnMap(currentIteratedDrone);
            updateDroneSearchedAreaOnMap(currentIteratedDrone);
            List<GeoPoint> updatedSearchedArea = updateSearchedAreaSet(currentIteratedDrone.getSearchedArea(), currentIteratedDrone.getLastSearchedArea());
            currentIteratedDrone.getSearchedArea().addAll(updatedSearchedArea);
        }
    }

    private List<GeoPoint> updateSearchedAreaSet(List<GeoPoint> searchedArea, List<GeoPoint> lastSearchedArea){
        if(searchedArea.size()>0) {
           searchedArea = addPointsWhichAreOutOfCurrentSearchedArea(searchedArea,lastSearchedArea);
        } else {
            searchedArea.addAll(lastSearchedArea);
        }

        return sortGeoPointsListByDistance(searchedArea);

    }



    private List<GeoPoint> addPointsWhichAreOutOfCurrentSearchedArea(List<GeoPoint> searchedArea, List<GeoPoint> areaToAdd){
        List<GeoPoint> newSearchedArea= new ArrayList<>();

        searchedArea = sortGeoPointsListByDistance(searchedArea);
        areaToAdd = sortGeoPointsListByDistance(areaToAdd);

        for(int i=0; i<searchedArea.size();i++){
            if(!pointInPolygon(searchedArea.get(i), areaToAdd)){
               newSearchedArea.add(searchedArea.get(i));
            }
        }

        for (int i=0; i<areaToAdd.size();i++) {
            if (!pointInPolygon(areaToAdd.get(i), searchedArea)) {
               newSearchedArea.add(areaToAdd.get(i));
            }
        }

        return newSearchedArea;
    }



    private List<GeoPoint> sortGeoPointsListByDistance(List<GeoPoint> searchedArea){

        List<GeoPoint> orderedSearchedArea = new ArrayList<>();
        orderedSearchedArea.add(searchedArea.remove(0));

        while (searchedArea.size() > 0) {
            GeoPoint point = orderedSearchedArea.get(orderedSearchedArea.size() - 1);
            int nearestPointIndex = findNearestPointIndex(point, searchedArea);
            GeoPoint nearestPoint = searchedArea.get(nearestPointIndex);
            if(nearesPointIsTheSamePoint(point, nearestPoint)){
                searchedArea.remove(nearestPointIndex);
            } else {
                orderedSearchedArea.add(searchedArea.remove(nearestPointIndex));
            }
        }

        return orderedSearchedArea;
    }

    private int findNearestPointIndex(GeoPoint point, List<GeoPoint> listToSearch) {
        int index =0;
        double dist = 0;
        for(int i=0;i<listToSearch.size();i++){
            GeoPoint currentPoint = listToSearch.get(i);
            double currentPointDist = distFrom( point.getLatitude(),  point.getLongitude(),  currentPoint.getLatitude(),  currentPoint.getLongitude());
            if(i==0){
                index = i;
                dist = currentPointDist;
            } else if(currentPointDist<dist){
                index = i;
                dist = currentPointDist;
            }
        }
        return index;
    }


    private boolean nearesPointIsTheSamePoint(GeoPoint point, GeoPoint nearestPoint){
        if(point.getLatitude()==nearestPoint.getLatitude() && point.getLongitude()==nearestPoint.getLongitude()){
            return true;
        } else{
            return false;
        }
    }


    public boolean pointInPolygon(GeoPoint point, List<GeoPoint> path) {
        // ray casting alogrithm http://rosettacode.org/wiki/Ray-casting_algorithm
        int crossings = 0;
        //path.remove(path.size()-1); //remove the last point that is added automatically by getPoints()

        // for each edge
        for (int i=0; i < path.size(); i++) {
            GeoPoint a = path.get(i);
            int j = i + 1;
            //to close the last edge, you have to take the first point of your polygon
            if (j >= path.size()) {
                j = 0;
            }
            GeoPoint b = path.get(j);
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
        //create an array of coordinates from the region boundary list
        GeoPoint[] verts = region.toArray(new GeoPoint[region.size()]);
        int sides = verts.length;
        for (i = 0, j = sides - 1; i < sides; j = i++) {
            //verifying if your coordinate is inside your region
            if (
                    (
                            (
                                    (verts[i].getLongitude() <= coord.getLongitude()) && (coord.getLongitude() < verts[j].getLongitude())
                            ) || (
                                    (verts[j].getLongitude() <= coord.getLongitude()) && (coord.getLongitude() < verts[i].getLongitude())
                            )
                    ) &&
                            (coord.getLatitude() < (verts[j].getLatitude() - verts[i].getLatitude()) * (coord.getLongitude() - verts[i].getLongitude()) / (verts[j].getLongitude() - verts[i].getLongitude()) + verts[i].getLatitude())
                    ) {
                isInside = !isInside;
            }
        }
        return isInside;
    }

    public boolean rayCrossesSegment(GeoPoint point, GeoPoint a,GeoPoint b) {
        // Ray Casting algorithm checks, for each segment, if the point is 1) to the left of the segment and 2) not above nor below the segment. If these two conditions are met, it returns true
        double px = point.getLongitude(),
                py = point.getLatitude(),
                ax = a.getLongitude(),
                ay = a.getLatitude(),
                bx = b.getLongitude(),
                by = b.getLatitude();
        if (ay > by) {
            ax = b.getLongitude();
            ay = b.getLatitude();
            bx = a.getLongitude();
            by = a.getLatitude();
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0 || ax <0 || bx <0) { px += 360; ax+=360; bx+=360; }
        // if the point has the same latitude as a or b, increase slightly py
        if (py == ay || py == by) py += 0.00000001;


        // if the point is above, below or to the right of the segment, it returns false
        if ((py > by || py < ay) || (px > Math.max(ax, bx))){
            return false;
        }
        // if the point is not above, below or to the right and is to the left, return true
        else if (px < Math.min(ax, bx)){
            return true;
        }
        // if the two above conditions are not met, you have to compare the slope of segment [a,b] (the red one here) and segment [a,p] (the blue one here) to see if your point is to the left of segment [a,b] or not
        else {
            double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
            double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
            return (blue >= red);
        }

    }


    private double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist =  (earthRadius * c);

        return dist;
    }

    private void addScaleBarOverlayToMapView() {
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(activity.getApplicationContext());
        mapOverlays.add(myScaleBarOverlay);
    }

    private void updateDroneLastPositionMarkerOnMap(Drone droneToUpdate) {
        Drawable droneIcon = getDroneMarkerIcon(droneToUpdate);

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(droneToUpdate.getCurrentPosition()));
        marker.setTitle(droneToUpdate.getDeviceId());
        marker.setIcon(droneIcon);

        mapOverlays.add(marker);
    }

    private void updateDroneLastSearchedAreaOnMap(Drone droneToUpdate) {
        Polygon lastSearchedArea = new Polygon(activity.getApplicationContext());
        lastSearchedArea.setPoints(droneToUpdate.getLastSearchedArea());
        lastSearchedArea.setFillColor(0X285EAAF6);
        lastSearchedArea.setStrokeColor(0X285EAAF6);
        lastSearchedArea.setStrokeWidth(0);
        mapOverlays.add(lastSearchedArea);
    }

    private void updateDroneSearchedAreaOnMap(Drone droneToUpdateTrail) {
        if (droneToUpdateTrail.getSearchedArea().size() > 1) {
            Polygon searchedArea = new Polygon(activity.getApplicationContext());
           /* PathOverlay pathOverlay = new PathOverlay(0x12121212,  activity);
            pathOverlay.getPaint().setStyle(Paint.Style.FILL);
            pathOverlay.addPoints(list);*/
            searchedArea.setPoints(droneToUpdateTrail.getSearchedArea());
            searchedArea.setFillColor(0x32121212);
            searchedArea.setStrokeColor(0x12121212);
            searchedArea.setStrokeWidth(5);
            mapOverlays.add(searchedArea);
        }
    }


    private Drawable getDroneMarkerIcon(Drone dronToUpdate) {
        Drawable droneIcon = activity.getResources().getDrawable(R.drawable.drone_marker);
        ColorFilter filter = new LightingColorFilter(dronToUpdate.getColor(), 1);
        droneIcon.setColorFilter(filter);
        return droneIcon;
    }


    private GeoPoint getLastDroneInSetLocation(Set<Drone> drones) {
        Iterator<Drone> dronesIterator = drones.iterator();
        if(dronesIterator.hasNext()) {
            return drones.iterator().next().getCurrentPosition();
        } else{
            return null;
        }
    }

}
