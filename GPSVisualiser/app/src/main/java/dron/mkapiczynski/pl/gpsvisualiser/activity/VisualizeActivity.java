package dron.mkapiczynski.pl.gpsvisualiser.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
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
import org.osmdroid.views.overlay.PathOverlay;

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

    private MapView mapView;
    private MapController mapController;
    private List<OverlayItem> overlayItemList;
    private DefaultResourceProxyImpl defaultResourceProxyImpl;

    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());

    private RoadManager roadManager = new OSRMRoadManager(this);

    // Websocket
    private static final String SERVER = "ws://0.tcp.ngrok.io:41115/dron-server-web/chatroom";
    private final WebSocketConnection client = new WebSocketConnection();
    private List<Polyline> dronesTracks = new ArrayList<>();
    private ArrayList<GeoPoint> points = new ArrayList<>();


    private Road mRoad;
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


    private void connectToWebSocketServer(){
        try {
            client.connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d("WEBSOCKETS", "Connected to server");
                    sendLoginMessage();
                    Toast.makeText(getApplicationContext(), "You are now connected to the server", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTextMessage(String message) {
                    String messageType = Json.createReader(new StringReader(message)).readObject().getString("messageType");
                    if("GeoDataMessage".equals(messageType)){
                        GeoDataMessage geoMessage = new GeoDataMessage();
                        geoMessage.setDeviceId(Json.createReader(new StringReader(message)).readObject().getString("deviceId"));
                        geoMessage.setDeviceType(Json.createReader(new StringReader(message)).readObject().getString("deviceType"));
                        geoMessage.setTimestamp(Json.createReader(new StringReader(message)).readObject().getString("timestamp"));
                        geoMessage.setLatitude(Json.createReader(new StringReader(message)).readObject().getString("latitude"));
                        geoMessage.setLongitude(Json.createReader(new StringReader(message)).readObject().getString("longitude"));
                        geoMessage.setAltitude(Json.createReader(new StringReader(message)).readObject().getString("altitude"));

                        Drone drone = new Drone();
                        drone.setDeviceId(geoMessage.getDeviceId());
                        drone.setCurrentAltitude(geoMessage.getAltitude());
                        drone.setCurrentLatitude(geoMessage.getLatitude());
                        drone.setCurrentLongitude(geoMessage.getLongitude());
                        if(!drones.contains(drone)){
                            drones.add(drone);
                        } else{
                           Iterator<Drone> iterator = drones.iterator();
                            while(iterator.hasNext()){
                                if(iterator.next().equals(drone)){
                                    iterator.next().setCurrentLatitude(drone.getCurrentLatitude());
                                    iterator.next().setCurrentLongitude(drone.getCurrentLongitude());
                                }
                            }
                        }

                        updateDronesMarkers(drones);

                    }
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

    private void sendLoginMessage(){
        if(client.isConnected()){
            ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
            clientLoginMessage.setClientId("1");
            client.sendTextMessage(clientLoginMessage.toJson());
        }
    }

    private void updateDronesMarkers(Set<Drone> drones) {
        Iterator<Drone> iterator = drones.iterator();
        overlayItemList.clear();
        while (iterator.hasNext()) {
            Drone drone = iterator.next();
            Double latitude = Double.parseDouble(drone.getCurrentLatitude());
            Double longitude = Double.parseDouble(drone.getCurrentLongitude());
            GeoPoint dronePositionPoint = new GeoPoint(latitude, longitude);
            points.add(dronePositionPoint);
            OverlayItem newDroneLocationItem = new OverlayItem(drone.getDeviceId(), "Location", dronePositionPoint);
            overlayItemList.add(newDroneLocationItem);
        }

        getRoadAsync(points);
    }
    private void updateMap(Road road){
        MyItemizedIconOverlay myItemizedIconOverlay = new MyItemizedIconOverlay(overlayItemList, null, defaultResourceProxyImpl);


        org.osmdroid.bonuspack.overlays.Polyline roadOverlay = RoadManager.buildRoadOverlay(road, this);

        mapView.getOverlays().clear();
        mapView.getOverlays().add(roadOverlay);
        //mapView.getOverlays().add(myItemizedIconOverlay);

        Double averageLatitude = 0.0;
        Double averageLongitude = 0.0;
        for(int i=0; i<overlayItemList.size();i++){
            averageLatitude += overlayItemList.get(i).getPoint().getLatitude();
            averageLongitude += overlayItemList.get(i).getPoint().getLongitude();
        }
        averageLatitude = averageLatitude / overlayItemList.size();
        averageLongitude = averageLongitude / overlayItemList.size();
        GeoPoint center = new GeoPoint(averageLatitude, averageLongitude);
        mapController.setCenter(center);
        mapView.invalidate();
    }


/*
    private void setMyPositionMarkerOnMap(GeoPoint myPositionPoint) {
        OverlayItem newMyLocationItem = new OverlayItem("My Location", "My Location", myPositionPoint);
        GeoPoint myPositionPoint2 = new GeoPoint(52.249, 21.108);
        OverlayItem newMyLocationItem2 = new OverlayItem("My Location", "My Location", myPositionPoint2);
        overlayItemList.clear();
        overlayItemList.add(newMyLocationItem);
        overlayItemList.add(newMyLocationItem2);
        MyItemizedIconOverlay myItemizedIconOverlay = new MyItemizedIconOverlay(overlayItemList, null, defaultResourceProxyImpl);
        mapView.getOverlays().clear();
        mapView.getOverlays().add(myItemizedIconOverlay);
    }*/

    private class MyItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {

        public MyItemizedIconOverlay(List<OverlayItem> pList,
                                     org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
                                     ResourceProxy pResourceProxy) {
            super(pList, pOnItemGestureListener, pResourceProxy);

        }

        @Override
        public void draw(Canvas canvas, MapView mapview, boolean arg2) {
            //super.draw(canvas, mapview, arg2);

            if (!overlayItemList.isEmpty()) {

                //overlayItemArray have only ONE element only, so I hard code to get(0)
                for (int i = 0; i < overlayItemList.size(); i++) {
                    GeoPoint in = (GeoPoint) overlayItemList.get(i).getPoint();
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));



                    Point out = new Point();
                    mapview.getProjection().toPixels(in, out);

                    Bitmap bm = BitmapFactory.decodeResource(getResources(),
                            R.drawable.marker);


                    Paint p = new Paint();
                    ColorFilter filter = new LightingColorFilter(color, 1);
                    p.setColorFilter(filter);

                    canvas.drawBitmap(bm,
                            out.x - bm.getWidth() / 2,  //shift the bitmap center
                            out.y - bm.getHeight() / 2,  //shift the bitmap center
                            p);


                }
            }
        }
    }

    private class UpdateRoadTask extends AsyncTask<Object, Void, Road> {
        protected Road doInBackground(Object... params) {
            @SuppressWarnings("unchecked")
            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>)params[0];
            //RoadManager roadManager = new GoogleRoadManager();
            RoadManager roadManager = new OSRMRoadManager(getApplicationContext());
                        /*
                        RoadManager roadManager = new MapQuestRoadManager();
                        Locale locale = Locale.getDefault();
                        roadManager.addRequestOption("locale="+locale.getLanguage()+"_"+locale.getCountry());
                        */
            return roadManager.getRoad(waypoints);
        }

        protected void onPostExecute(Road result) {
            mRoad = result;
            updateMap(result);
        }
    }

    public void getRoadAsync(ArrayList<GeoPoint> points){
        ArrayList<GeoPoint> waypoints = points;
        new UpdateRoadTask().execute(waypoints);
    }

}
