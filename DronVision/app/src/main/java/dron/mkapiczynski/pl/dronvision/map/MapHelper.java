package dron.mkapiczynski.pl.dronvision.map;

import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
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

/**
 * Created by Miix on 2016-01-08.
 */
public class MapHelper {
    private MapView mapView;
    private VisionActivity activity;

    public MapHelper(VisionActivity activity){
        this.activity = activity;
        mapView = (MapView) activity.findViewById(R.id.MapView);
    }

    public void setMapViewDefaultSettings() {
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(14);
        MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(16);
        mapController.setCenter(new GeoPoint(52.24695, 21.105083));
        addScaleBarOverlayToMapView();
        /*CustomMapListener customMapListener = new CustomMapListener(VisualizeActivity.this, getApplicationContext(), 16);
        mapView.setMapListener(customMapListener);*/
        /*DisplayMetrics metrics = getResources().getDisplayMetrics();
        double scale = 1 / (metrics.densityDpi * 39.37 * 1.1943);*/
    }

    public void updateDronesOnMapView(Set<Drone> drones){
        mapView.getOverlays().clear();
        addScaleBarOverlayToMapView();

        Iterator<Drone> droneIterator = drones.iterator();
        while (droneIterator.hasNext()) {
            Drone currentIteratedDrone = droneIterator.next();
            updateDroneLastPositionMarkerOnMap(currentIteratedDrone);
            updateDroneLastSearchedAreaOnMap(currentIteratedDrone);
            updateDroneSearchedAreaOnMap(currentIteratedDrone);
            currentIteratedDrone.getSearchedArea().addAll(currentIteratedDrone.getLastSearchedArea());
        }

        /**
         * TODO
         * Obliczanie środka mapy do przemyślenia.
         * Chyba śledzenie jednego drona w danej chwili, a reszta ty
         * lko wizualizacja.
         */
        GeoPoint mapCenter = getLastDroneInSetLocation(drones);
        MapController mapController = (MapController) mapView.getController();
        mapController.animateTo(mapCenter);
        mapView.invalidate();
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

    private Drawable getDroneMarkerIcon(Drone dronToUpdate) {
        Drawable droneIcon = activity.getResources().getDrawable(R.drawable.drone_marker);
        ColorFilter filter = new LightingColorFilter(dronToUpdate.getColor(), 1);
        droneIcon.setColorFilter(filter);
        return droneIcon;
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

    private GeoPoint getLastDroneInSetLocation(Set<Drone> drones) {
        Iterator<Drone> dronesIterator = drones.iterator();
        while (dronesIterator.hasNext()) {
            Drone currentIteratedDrone = dronesIterator.next();
            if (!dronesIterator.hasNext()) {
                return new GeoPoint(currentIteratedDrone.getCurrentPosition().getLatitude(), currentIteratedDrone.getCurrentPosition().getLongitude());
            }
        }
        return null;
    }

    public class DronUpdateTask extends AsyncTask<Void, Void, Boolean> {

        private final Drone drone;

        DronUpdateTask(Drone drone) {
           this.drone = drone;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            updateDroneLastPositionMarkerOnMap(drone);
            updateDroneLastSearchedAreaOnMap(drone);
            updateDroneSearchedAreaOnMap(drone);
            drone.getSearchedArea().addAll(drone.getLastSearchedArea());
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

        }

        @Override
        protected void onCancelled() {

        }
    }


/*
    private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {
        private Paint paint;
        public MyItemizedIconOverlay(List<OverlayItem> pList,
                                     org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                                     ResourceProxy pResourceProxy) {
            super(pList, pOnItemGestureListener, pResourceProxy);

            this.paint = getRandomPaintColorForMarker();
        }

        private Paint getRandomPaintColorForMarker(){
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            Paint paint = new Paint();
            ColorFilter filter = new LightingColorFilter(color, 1);
            paint.setColorFilter(filter);
            return paint;
        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            //super.draw(canvas, mapview, arg2);

            MyGeoPoint in = (MyGeoPoint) overlayItemList.get(0).getPoint();

            Point out = new Point();
            mapview.getProjection().toPixels(in, out);

            Bitmap bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.marker);

            canvas.drawBitmap(bm,
                    out.x - bm.getWidth() / 2,
                    out.y - bm.getHeight() / 2,
                    this.paint);
        }
    }*/
}
