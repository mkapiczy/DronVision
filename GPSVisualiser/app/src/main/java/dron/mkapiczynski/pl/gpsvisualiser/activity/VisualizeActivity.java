package dron.mkapiczynski.pl.gpsvisualiser.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.Polyline;
import org.osmdroid.bonuspack.overlays.GroundOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.json.Json;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import dron.mkapiczynski.pl.gpsvisualiser.CustomMapListener;
import dron.mkapiczynski.pl.gpsvisualiser.R;
import dron.mkapiczynski.pl.gpsvisualiser.decoder.MessageDecoder;
import dron.mkapiczynski.pl.gpsvisualiser.domain.Drone;
import dron.mkapiczynski.pl.gpsvisualiser.message.ClientLoginMessage;
import dron.mkapiczynski.pl.gpsvisualiser.message.GeoDataMessage;


public class VisualizeActivity extends AppCompatActivity {
    private static final String TAG = VisualizeActivity.class.getSimpleName();

    private Button refreshConnectionButton;
    // Map objects
    private MapView mapView;
    private MapController mapController;
    private List<OverlayItem> overlayItemList;
    private DefaultResourceProxyImpl defaultResourceProxyImpl;


    // Websocket
    private static final String SERVER = "ws://0.tcp.ngrok.io:48269/dron-server-web/server";
    private final WebSocketConnection client = new WebSocketConnection();
    private List<Polyline> dronesTracks = new ArrayList<>();
    private ArrayList<GeoPoint> points = new ArrayList<>();

    // private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Drony, które mają być wizualizowane
    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        refreshConnectionButton = (Button) findViewById(R.id.refreshConnectionButton);

        mapView = (MapView) findViewById(R.id.MapView);

        overlayItemList = new ArrayList<>();
        defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);
        setMapViewDefaultSettings();

        connectToWebSocketServer();
        //ask for drones ascribed to this client device
        // askForLastDronesLocation();

        refreshConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!client.isConnected()) {
                    connectToWebSocketServer();
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
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double scale = 1 / (metrics.densityDpi * 39.37 * 1.1943);
        mapController.setZoom(16);
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(getApplicationContext());
        mapView.getOverlays().add(myScaleBarOverlay);
        CustomMapListener customMapListener = new CustomMapListener(VisualizeActivity.this, getApplicationContext(), 16);
        mapView.setMapListener(customMapListener);
    }


    private void connectToWebSocketServer() {
        try {
            client.connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    /*if(!scheduler.isShutdown()){
                        scheduler.shutdown();
                        scheduler = Executors.newSingleThreadScheduledExecutor();
                    }*/
                    Log.d("WEBSOCKETS", "Connected to server");
                    sendLoginMessage();
                    Toast.makeText(getApplicationContext(), "You are now connected to the server", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTextMessage(String jsonMessage) {
                    String messageType = Json.createReader(new StringReader(jsonMessage)).readObject().getString("messageType");
                    if ("GeoDataMessage".equals(messageType)) {
                        GeoDataMessage geoMessage = MessageDecoder.decodeGeoDataMessage(jsonMessage);

                        /**
                         * TODO
                         * Do sprawdzenia, czy działa constains bo to w końcu nowy obiekt.
                         * Chyba trzeba wyszukać po deviceId
                         */
                        Drone drone = new Drone();
                        drone.setDeviceId(geoMessage.getDeviceId());
                        drone.setCurrentAltitude(Double.parseDouble(geoMessage.getAltitude()));
                        drone.setCurrentLatitude(Double.parseDouble(geoMessage.getLatitude()));
                        drone.setCurrentLongitude(Double.parseDouble(geoMessage.getLongitude()));


                        if (dronesSetContainsThisDrone(drone)) {
                            Iterator<Drone> iterator = drones.iterator();
                            while (iterator.hasNext()) {
                                Drone currentDroneOnList = iterator.next();
                                if (currentDroneOnList.getDeviceId().equals(drone.getDeviceId())) {
                                    currentDroneOnList.setCurrentLatitude(drone.getCurrentLatitude());
                                    currentDroneOnList.setCurrentLongitude(drone.getCurrentLongitude());
                                    currentDroneOnList.getTrail().add(new GeoPoint(drone.getCurrentLatitude(), drone.getCurrentLongitude()));
                                }
                            }
                        } else if (!drones.contains(drone)) {
                            Random rnd = new Random();
                            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            drone.setColor(color);
                            drone.setTrail(new ArrayList<GeoPoint>());
                            drone.getTrail().add(new GeoPoint(drone.getCurrentLatitude(), drone.getCurrentLongitude()));
                            drones.add(drone);
                        }

                        updateDronesOnMap();
                    }
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

                @Override
                public void onClose(int code, String reason) {
                    Toast.makeText(getApplicationContext(), "Connection closed Code:" + code + " Reason: " + reason, Toast.LENGTH_LONG).show();
                    Log.d("WEBSOCKETS", "Connection closed Code:" + code + " Reason: " + reason);
                    //startReestablishingConnectionScheduler();
                }
            });
        } catch (WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendLoginMessage() {
        if (client.isConnected()) {
            ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
            clientLoginMessage.setClientId("1");
            client.sendTextMessage(clientLoginMessage.toJson());
        }
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


    private void updateDronesOnMap() {
        mapView.getOverlays().clear();
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(getApplicationContext());
        mapView.getOverlays().add(myScaleBarOverlay);
        Iterator<Drone> droneIterator = drones.iterator();
        while (droneIterator.hasNext()) {
            Drone currentIteratedDrone = droneIterator.next();
            updateDroneLastPositionMarker(currentIteratedDrone);
            updateDroneTrailOnMap(currentIteratedDrone);
        }


        /**
         * TODO
         * Obliczanie środka mapy do przemyślenia.
         * Chyba śledzenie jednego drona w danej chwili, a reszta tylko wizualizacja.
         */
        GeoPoint center = getLastDroneInSetLocation();
        mapController.animateTo(center);
        // mapController.setCenter(center);
        mapView.invalidate();
    }

    private void updateDroneTrailOnMap(Drone droneToUpdateTrail) {
        if (droneToUpdateTrail.getTrail().size() > 1) {
            org.osmdroid.bonuspack.overlays.Polyline droneTrail = new org.osmdroid.bonuspack.overlays.Polyline(getApplicationContext());
            droneTrail.setPoints(droneToUpdateTrail.getTrail());
            droneTrail.setWidth(3.0f);
            droneTrail.setColor(droneToUpdateTrail.getColor());
            List<GeoPoint> polygonPointsList = new ArrayList<>();
            for (int i = 0; i < droneToUpdateTrail.getTrail().size() - 1; i++) {
                Polygon circle = new Polygon(getApplicationContext());
                circle.setPoints(Polygon.pointsAsCircle(new GeoPoint(droneToUpdateTrail.getTrail().get(i).getLatitude(), droneToUpdateTrail.getTrail().get(i).getLongitude()), 35.0));
                for (int j = 0; j < circle.getPoints().size(); j++) {
                    for (int k = 0; k < polygonPointsList.size(); k++) {
                        if ((Double.compare(polygonPointsList.get(k).getLatitude(), circle.getPoints().get(j).getLatitude()) == 0)
                                && (Double.compare(polygonPointsList.get(k).getLongitude(), circle.getPoints().get(j).getLongitude()) == 0)) {
                            continue;
                        }
                        polygonPointsList.add(circle.getPoints().get(j));
                    }
                }
                Polygon polygon = new Polygon(getApplicationContext());
                polygon.setPoints(polygonPointsList);
                polygon.setFillColor(0x12121212);
                polygon.setStrokeColor(0x12121212);
                polygon.setStrokeWidth(1);
                mapView.getOverlays().add(polygon);
                mapView.getOverlays().add(droneTrail);
            }
        }
    }

    private void updateDroneLastPositionMarker(Drone droneToUpdateLastPositionMarker) {

        Drawable droneIcon = getResources().getDrawable(R.drawable.drone3);
        ColorFilter filter = new LightingColorFilter(droneToUpdateLastPositionMarker.getColor(), 1);
        droneIcon.setColorFilter(filter);

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(droneToUpdateLastPositionMarker.getCurrentLatitude(), droneToUpdateLastPositionMarker.getCurrentLongitude()));
        marker.setTitle(droneToUpdateLastPositionMarker.getDeviceId());
        marker.setIcon(droneIcon);

        Polygon circle = new Polygon(getApplicationContext());
        circle.setPoints(Polygon.pointsAsCircle(new GeoPoint(droneToUpdateLastPositionMarker.getCurrentLatitude(), droneToUpdateLastPositionMarker.getCurrentLongitude()), 35.0));
        circle.setFillColor(0x12121212);
        circle.setStrokeColor(Color.RED);
        circle.setStrokeWidth(2);

        mapView.getOverlays().add(circle);
        mapView.getOverlays().add(marker);
    }

    private GeoPoint getLastDroneInSetLocation() {
        Iterator<Drone> dronesIterator = drones.iterator();
        while (dronesIterator.hasNext()) {
            Drone currentIteratedDrone = dronesIterator.next();
            if (!dronesIterator.hasNext()) {
                return new GeoPoint(currentIteratedDrone.getCurrentLatitude(), currentIteratedDrone.getCurrentLongitude());
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

            GeoPoint in = (GeoPoint) overlayItemList.get(0).getPoint();

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



