package dron.mkapiczynski.pl.dronvision.map;

import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.activity.VisionActivity;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.service.DronService;
import dron.mkapiczynski.pl.dronvision.service.DronServiceBean;

/**
 * Created by Miix on 2016-01-09.
 */
public class MapAsyncTask extends AsyncTask<Void, Void, Boolean> {
    DronService dronService = new DronServiceBean();

    private VisionActivity activity;
    private Drone drone;
    private Set<Drone> drones;
    private MapView mapView;

    public MapAsyncTask(Drone drone, Set<Drone> drones, VisionActivity activity) {
        this.drone = drone;
        this.drones = drones;
        this.activity = activity;
        mapView = (MapView) activity.findViewById(R.id.MapView);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
        MapController mapController = (MapController) mapView.getController();
        mapController.animateTo(drone.getCurrentPosition());
        mapView.invalidate();
    }

    @Override
    protected void onCancelled() {
    }

    private void updateDronesOnMapView(Set<Drone> drones){
        mapView.getOverlays().clear();
        Iterator<Drone> droneIterator = drones.iterator();
        while (droneIterator.hasNext()) {
            Drone currentIteratedDrone = droneIterator.next();
            updateDroneLastPositionMarkerOnMap(currentIteratedDrone);
            updateDroneLastSearchedAreaOnMap(currentIteratedDrone);
            updateDroneSearchedAreaOnMap(currentIteratedDrone);
            currentIteratedDrone.getSearchedArea().addAll(currentIteratedDrone.getLastSearchedArea());
        }
    }

    private void addScaleBarOverlayToMapView() {
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(activity.getApplicationContext());
        mapView.getOverlays().add(myScaleBarOverlay);
    }

    private void updateDroneLastPositionMarkerOnMap(Drone droneToUpdate) {
        Drawable droneIcon = getDroneMarkerIcon(droneToUpdate);

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(droneToUpdate.getCurrentPosition()));
        marker.setTitle(droneToUpdate.getDeviceId());
        marker.setIcon(droneIcon);

        mapView.getOverlays().add(marker);
    }

    private void updateDroneLastSearchedAreaOnMap(Drone droneToUpdate) {
        Polygon lastSearchedArea = new Polygon(activity.getApplicationContext());
        lastSearchedArea.setPoints(droneToUpdate.getLastSearchedArea());
        lastSearchedArea.setFillColor(0X285EAAF6);
        lastSearchedArea.setStrokeColor(0X285EAAF6);
        lastSearchedArea.setStrokeWidth(0);
        mapView.getOverlays().add(lastSearchedArea);
    }

    private void updateDroneSearchedAreaOnMap(Drone droneToUpdateTrail) {
        if (droneToUpdateTrail.getSearchedArea().size() > 1) {
            Polygon searchedArea = new Polygon(activity.getApplicationContext());
            List<GeoPoint> list = new ArrayList<>();
            list.addAll(droneToUpdateTrail.getSearchedArea());
            searchedArea.setPoints(list);
            searchedArea.setFillColor(0x12121212);
            searchedArea.setStrokeColor(0x12121212);
            searchedArea.setStrokeWidth(0);
            mapView.getOverlays().add(searchedArea);
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
