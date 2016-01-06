package dron.mkapiczynski.pl.gpsvisualiser.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.json.Json;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import dron.mkapiczynski.pl.gpsvisualiser.R;
import dron.mkapiczynski.pl.gpsvisualiser.domain.Drone;
import dron.mkapiczynski.pl.gpsvisualiser.message.ClientLoginMessage;
import dron.mkapiczynski.pl.gpsvisualiser.message.GeoDataMessage;


public class VisualizeActivity extends AppCompatActivity {
    private static final String TAG = VisualizeActivity.class.getSimpleName();

    // Map objects
    private MapView mapView;
    private MapController mapController;
    private List<OverlayItem> overlayItemList;
    private DefaultResourceProxyImpl defaultResourceProxyImpl;


    // Websocket
    private static final String SERVER = "ws://0.tcp.ngrok.io:52856/dron-server-web/server";
    private final WebSocketConnection client = new WebSocketConnection();
    private List<Polyline> dronesTracks = new ArrayList<>();
    private ArrayList<GeoPoint> points = new ArrayList<>();


    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mapView = (MapView) findViewById(R.id.MapView);

        overlayItemList = new ArrayList<>();
        defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);
        setMapViewDefaultSettings();

        connectToWebSocketServer();
        // askForLastDronesLocation();

    }

    private void setMapViewDefaultSettings() {
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(16);
    }


    private void connectToWebSocketServer() {
        try {
            client.connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d("WEBSOCKETS", "Connected to server");
                    sendLoginMessage();
                    Toast.makeText(getApplicationContext(), "You are now connected to the server", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTextMessage(String jsonMessage) {
                    String messageType = Json.createReader(new StringReader(jsonMessage)).readObject().getString("messageType");
                    if ("GeoDataMessage".equals(messageType)) {
                        GeoDataMessage geoMessage = new GeoDataMessage();
                        geoMessage.decodeGeoDataMessage(jsonMessage);

                        /**
                         * TODO
                         * Do sprawdzenia, czy działa constains bo to w końcu nowy obiekt.
                         * Chyba trzeba wyszukać po deviceId
                         */
                        Drone drone = new Drone();
                        drone.setDeviceId(geoMessage.getDeviceId());
                        drone.setCurrentAltitude(geoMessage.getAltitude());
                        drone.setCurrentLatitude(geoMessage.getLatitude());
                        drone.setCurrentLongitude(geoMessage.getLongitude());

                        if (dronesSetContainsThisDrone(drone)) {
                            Iterator<Drone> iterator = drones.iterator();
                            while (iterator.hasNext()) {
                                Drone currentDroneOnList = iterator.next();
                                if (currentDroneOnList.getDeviceId().equals(drone.getDeviceId())) {
                                    currentDroneOnList.setCurrentLatitude(drone.getCurrentLatitude());
                                    currentDroneOnList.setCurrentLongitude(drone.getCurrentLongitude());
                                }
                            }
                        } else if (!drones.contains(drone)) {
                            drones.add(drone);
                        }

                        updateDronesMarkers(drones);

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

    private void updateDronesMarkers(Set<Drone> drones) {
        overlayItemList.clear();
        Iterator<Drone> dronesIterator = drones.iterator();
        while (dronesIterator.hasNext()) {
            Drone currentDroneOnList = dronesIterator.next();
            Double latitude = Double.parseDouble(currentDroneOnList.getCurrentLatitude());
            Double longitude = Double.parseDouble(currentDroneOnList.getCurrentLongitude());
            GeoPoint dronePositionPoint = new GeoPoint(latitude, longitude);
            points.add(dronePositionPoint);
            OverlayItem newDroneLocationItem = new OverlayItem(currentDroneOnList.getDeviceId(), "Location", dronePositionPoint);
            overlayItemList.add(newDroneLocationItem);
        }

        updateMap(points);
    }

    private void updateMap(List<GeoPoint> points) {
        mapView.getOverlays().clear();

        if (points.size() > 1) {
            for (int i = 0; i < points.size() - 1; i++) {
                List<GeoPoint> pointList = new ArrayList<>();
                List<OverlayItem> items = new ArrayList<>();
                org.osmdroid.bonuspack.overlays.Polyline line = new org.osmdroid.bonuspack.overlays.Polyline(getApplicationContext());
                pointList.add(new GeoPoint(points.get(i).getLatitude(), points.get(i).getLongitude()));
                pointList.add(new GeoPoint(points.get(i + 1).getLatitude(), points.get(i + 1).getLongitude()));
                line.setPoints(pointList);
                line.setWidth(1);
                mapView.getOverlays().add(line);
            }
        }

        /**
         * Current position marker
         */
        MyItemizedIconOverlay myItemizedIconOverlay = new MyItemizedIconOverlay(overlayItemList, null, defaultResourceProxyImpl);
        mapView.getOverlays().add(myItemizedIconOverlay);

        /**
         * TODO
         * Obliczanie środka mapy do przemyślenia.
         * Chyba śledzenie jednego drona w danej chwili, a reszta tylko wizualizacja.
         */
        GeoPoint center = getMapCenterPointForDrones();

        mapController.setCenter(center);
        mapView.invalidate();
    }

    private GeoPoint getMapCenterPointForDrones() {
        Double dronesLatitudeSumm = 0.0;
        Double dronesLongitudeSumm = 0.0;
        Iterator<Drone> dronesIterator = drones.iterator();
        while (dronesIterator.hasNext()) {
            Drone currentDrone = dronesIterator.next();
            dronesLatitudeSumm += Double.parseDouble(currentDrone.getCurrentLatitude());
            dronesLongitudeSumm += Double.parseDouble(currentDrone.getCurrentLongitude());
        }

        Double averageLatitude = dronesLatitudeSumm / drones.size();
        Double averageLongitude = dronesLongitudeSumm / drones.size();
        return new GeoPoint(averageLatitude, averageLongitude);
    }


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
    }
}
/*
    private class MyItemizedLineOverlay extends ItemizedIconOverlay<OverlayItem> {

        public MyItemizedLineOverlay(List<OverlayItem> pList,
                                     org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                                     ResourceProxy pResourceProxy) {
            super(pList, pOnItemGestureListener, pResourceProxy);

        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            //super.draw(canvas, mapview, arg2);


            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            Paint p = new Paint();
            ColorFilter filter = new LightingColorFilter(color, 1);
            p.setColorFilter(filter);

            if (!points.isEmpty()) {
                if(points.size()>1) {
                    for (int i = 0; i < points.size()-1; i++) {
                        List<GeoPoint> pointList = new ArrayList<>();
                        pointList.add(new GeoPoint(points.get(i).getLatitude(), points.get(i).getLongitude()));
                        pointList.add(new GeoPoint(points.get(i + 1).getLatitude(), points.get(i + 1).getLongitude()));
                        canvas.drawLine((float)points.get(i).getLatitude(), (float) points.get(i).getLongitude(),(float)points.get(i + 1).getLatitude(), (float)points.get(i + 1).getLongitude(), p);
                    }
                }
            }
        }
    }*/


/**
 * private class UpdateRoadTask extends AsyncTask<Object, Void, Road> {
 * protected Road doInBackground(Object... params) {
 *
 * @SuppressWarnings("unchecked") ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>)params[0];
 * //RoadManager roadManager = new GoogleRoadManager();
 * RoadManager roadManager = new OSRMRoadManager(getApplicationContext());
 * /*
 * RoadManager roadManager = new MapQuestRoadManager();
 * Locale locale = Locale.getDefault();
 * roadManager.addRequestOption("locale="+locale.getLanguage()+"_"+locale.getCountry());
 */
            /*return roadManager.getRoad(waypoints);
        }

        protected void onPostExecute(Road result) {
            mRoad = result;
            updateMap(result);
        }
    }

    public void getRoadAsync(ArrayList<GeoPoint> points){
        ArrayList<GeoPoint> waypoints = points;
        new UpdateRoadTask().execute(waypoints);
    }*/



