package dron.mkapiczynski.pl.dronvision.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.domain.Drone;

/**
 * Created by Miix on 2016-01-14.
 */
public class MapUtils {

    public static void setMapViewDefaultSettings(MapView mapView, Activity activity) {
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(14);
        MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(16);
        mapController.setCenter(new GeoPoint(52.24695, 21.105083));
        addScaleBarOverlayToMapView(mapView.getOverlays(), activity);
        /*CustomMapListener customMapListener = new CustomMapListener(VisualizeActivity.this, getApplicationContext(), 16);
        mapView.setMapListener(customMapListener);*/
        /*DisplayMetrics metrics = getResources().getDisplayMetrics();
        double scale = 1 / (metrics.densityDpi * 39.37 * 1.1943);*/
    }

    public static List<Overlay> updateMapOverlays(Set<Drone> drones, MapView mapView, Activity activity){
        List<Overlay> mapOverlays = new ArrayList<>();
        addScaleBarOverlayToMapView(mapOverlays, activity);

        Iterator<Drone> dronesIterator = drones.iterator();
        while(dronesIterator.hasNext()){
            Drone currentIteratedDrone = dronesIterator.next();
            updateDroneOverlays(currentIteratedDrone, mapOverlays,mapView,activity);
        }

        return mapOverlays;
    }

    private static void updateDroneOverlays(Drone droneToUpdate, List<Overlay> mapOverlays, MapView mapView, Activity activity){
        updateDroneLastPositionMarkerOnMap(droneToUpdate, mapOverlays, mapView, activity);
        updateDroneLastSearchedAreaOnMap(droneToUpdate, mapOverlays, activity);
        updateDroneSearchedAreaOnMap(droneToUpdate, mapOverlays, activity);
    }


    private static void updateDroneLastPositionMarkerOnMap(Drone droneToUpdate, List<Overlay> mapOverlays, MapView mapView, Activity activity) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(droneToUpdate.getCurrentPosition()));
        marker.setTitle(droneToUpdate.getDeviceId());
        Drawable droneIcon = DroneUtils.getDroneMarkerIcon(droneToUpdate, activity);
        marker.setIcon(droneIcon);
        mapOverlays.add(marker);
    }

    private static void updateDroneLastSearchedAreaOnMap(Drone droneToUpdate, List<Overlay> mapOverlays, Activity activity) {
        Polygon lastSearchedArea = new Polygon(activity.getApplicationContext());
        lastSearchedArea.setPoints(droneToUpdate.getLastSearchedArea());
        lastSearchedArea.setFillColor(0X285EAAF6);
        lastSearchedArea.setStrokeColor(0X285EAAF6);
        lastSearchedArea.setStrokeWidth(0);
        mapOverlays.add(lastSearchedArea);
    }

    private static void updateDroneSearchedAreaOnMap(Drone droneToUpdate, List<Overlay> mapOverlays, Activity activity) {
        if (droneToUpdate.getSearchedArea().size() > 1) {
            Polygon searchedArea = new Polygon(activity.getApplicationContext());
            searchedArea.setPoints(droneToUpdate.getSearchedArea());
            searchedArea.setFillColor(0x32121212);
            searchedArea.setStrokeColor(0x12121212);
            searchedArea.setStrokeWidth(5);
            mapOverlays.add(searchedArea);
        }
    }

    private static void addScaleBarOverlayToMapView(List<Overlay> mapOverlays, Activity activity) {
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(activity.getApplicationContext());
        mapOverlays.add(myScaleBarOverlay);
    }
}
