package dron.mkapiczynski.pl.gpsvisualiser.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.Polyline;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dron.mkapiczynski.pl.gpsvisualiser.CustomMapListener;
import dron.mkapiczynski.pl.gpsvisualiser.R;
import dron.mkapiczynski.pl.gpsvisualiser.domain.Drone;
import dron.mkapiczynski.pl.gpsvisualiser.domain.MyGeoPoint;
import dron.mkapiczynski.pl.gpsvisualiser.websocket.MyWebSocketConnection;

;


public class VisualizeActivity extends Activity {
    private static final String TAG = VisualizeActivity.class.getSimpleName();

    private Button refreshConnectionButton;
    // Map objects
    private MapView mapView;
    private MapController mapController;
    private List<OverlayItem> overlayItemList;
    private DefaultResourceProxyImpl defaultResourceProxyImpl;


    // Websocket
    private final MyWebSocketConnection client = new MyWebSocketConnection(this);

    private List<Polyline> dronesTracks = new ArrayList<>();
    private ArrayList<MyGeoPoint> points = new ArrayList<>();

    // private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Drony, które mają być wizualizowane
    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        refreshConnectionButton = (Button) findViewById(R.id.refreshConnectionButton);

        mapView = (MapView) findViewById(R.id.MapView);

        overlayItemList = new ArrayList<>();
        defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);
        setMapViewDefaultSettings();

        client.connectToWebSocketServer();
        //ask for drones ascribed to this client device
        // askForLastDronesLocation();

        refreshConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!client.isConnected()) {
                    client.connectToWebSocketServer();
                }
            }
        });

    }

    private void setMapViewDefaultSettings() {
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        // mapView.setMaxZoomLevel(17);
        mapView.setMinZoomLevel(14);
        mapController = (MapController) mapView.getController();
        /*DisplayMetrics metrics = getResources().getDisplayMetrics();
        double scale = 1 / (metrics.densityDpi * 39.37 * 1.1943);*/
        mapController.setZoom(16);
        mapController.setCenter(new GeoPoint(52.24695,21.105083));
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(getApplicationContext());
        mapView.getOverlays().add(myScaleBarOverlay);
        CustomMapListener customMapListener = new CustomMapListener(VisualizeActivity.this, getApplicationContext(), 16);
        mapView.setMapListener(customMapListener);
    }


    /*
    private void startReestablishingConnectionScheduler(){
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        if (!client.isConnected()) {
                            connectToWebSocketServer();
                        }
                    }
                }, 0, 5, TimeUnit.SECONDS);
    }*/


    public void updateDronesOnMap(Drone drone) {
        if (dronesSetContainsThisDrone(drone)) {
            Iterator<Drone> iterator = drones.iterator();
            while (iterator.hasNext()) {
                Drone currentDroneOnList = iterator.next();
                if (currentDroneOnList.getDeviceId().equals(drone.getDeviceId())) {
                    currentDroneOnList.setCurrentPosition(drone.getCurrentPosition());
                    currentDroneOnList.getLastSearchedArea().clear();
                    currentDroneOnList.getLastSearchedArea().addAll(drone.getSearchedArea());
                }
            }
        } else if (!drones.contains(drone)) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            drone.setColor(color);
            drone.setSearchedArea(new HashSet<GeoPoint>());
            drones.add(drone);
        }
        mapView.getOverlays().clear();
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(getApplicationContext());
        mapView.getOverlays().add(myScaleBarOverlay);
        Iterator<Drone> droneIterator = drones.iterator();
        while (droneIterator.hasNext()) {
            Drone currentIteratedDrone = droneIterator.next();
            updateDroneLastPositionMarker(currentIteratedDrone);
            updateDroneTrailOnMap(currentIteratedDrone);
            currentIteratedDrone.getSearchedArea().addAll(currentIteratedDrone.getLastSearchedArea());
        }


        /**
         * TODO
         * Obliczanie środka mapy do przemyślenia.
         * Chyba śledzenie jednego drona w danej chwili, a reszta tylko wizualizacja.
         */
        GeoPoint center = getLastDroneInSetLocation();

        mapController.animateTo(center);
        mapView.invalidate();
    }

    private boolean dronesSetContainsThisDrone(Drone drone) {
        Iterator<Drone> iterator = drones.iterator();
        while (iterator.hasNext()) {
            Drone currentDroneOnList = iterator.next();
            if (currentDroneOnList.getDeviceId().equals(drone.getDeviceId())) {
                return true;
            }
        }
        return false;
    }

    private void updateDroneTrailOnMap(Drone droneToUpdateTrail) {
        if (droneToUpdateTrail.getSearchedArea().size() > 1) {
            Polygon searchedArea = new Polygon(getApplicationContext());
            List<GeoPoint> list = new ArrayList<>();
            list.addAll(droneToUpdateTrail.getSearchedArea());
            searchedArea.setPoints(list);
            searchedArea.setFillColor(0x12121212);
            searchedArea.setStrokeColor(0x12121212);
            searchedArea.setStrokeWidth(0);
            mapView.getOverlays().add(searchedArea);
        }
    }

    private void updateDroneLastPositionMarker(Drone droneToUpdateLastPositionMarker) {

        Drawable droneIcon = getResources().getDrawable(R.drawable.drone_marker);
        ColorFilter filter = new LightingColorFilter(droneToUpdateLastPositionMarker.getColor(), 1);
        droneIcon.setColorFilter(filter);

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(droneToUpdateLastPositionMarker.getCurrentPosition()));
        marker.setTitle(droneToUpdateLastPositionMarker.getDeviceId());
        marker.setIcon(droneIcon);

        Polygon circle = new Polygon(getApplicationContext());
        circle.setPoints(droneToUpdateLastPositionMarker.getLastSearchedArea());
        circle.setFillColor(0X285EAAF6);
        circle.setStrokeColor(0X285EAAF6);
        circle.setStrokeWidth(0);
        mapView.getOverlays().add(circle);
        mapView.getOverlays().add(marker);
    }

    private GeoPoint getLastDroneInSetLocation() {
        Iterator<Drone> dronesIterator = drones.iterator();
        while (dronesIterator.hasNext()) {
            Drone currentIteratedDrone = dronesIterator.next();
            if (!dronesIterator.hasNext()) {
                return new GeoPoint(currentIteratedDrone.getCurrentPosition().getLatitude(), currentIteratedDrone.getCurrentPosition().getLongitude());
            }
        }
        return null;
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



