package dron.mkapiczynski.pl.gpsvisualiser.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.Polyline;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.gpsvisualiser.R;
import dron.mkapiczynski.pl.gpsvisualiser.domain.Drone;
import dron.mkapiczynski.pl.gpsvisualiser.domain.MyGeoPoint;
import dron.mkapiczynski.pl.gpsvisualiser.helper.MapHelper;
import dron.mkapiczynski.pl.gpsvisualiser.service.DronService;
import dron.mkapiczynski.pl.gpsvisualiser.service.DronServiceBean;
import dron.mkapiczynski.pl.gpsvisualiser.websocket.MyWebSocketConnection;

;


public class VisualizeActivity extends Activity {
    private static final String TAG = VisualizeActivity.class.getSimpleName();

    private Button refreshConnectionButton;
    // Map objects
    private MapHelper mapHelper;
    private MapView mapView;
    private MapController mapController;
    private List<OverlayItem> overlayItemList;
    private DefaultResourceProxyImpl defaultResourceProxyImpl;


    // Websocket
    private final MyWebSocketConnection client = new MyWebSocketConnection(this);

    private List<Polyline> dronesTracks = new ArrayList<>();
    private ArrayList<MyGeoPoint> points = new ArrayList<>();

    // Drony, które mają być wizualizowane
    private DronService dronService = new DronServiceBean();
    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize);
        refreshConnectionButton = (Button) findViewById(R.id.refreshConnectionButton);

        mapView = (MapView) findViewById(R.id.MapView);

        overlayItemList = new ArrayList<>();
        defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);

        mapHelper = new MapHelper(mapView,mapController, this);
        mapHelper.setMapViewDefaultSettings();

        client.connectToWebSocketServer();
        //ask for drones ascribed to this client account

        refreshConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!client.isConnected()) {
                    client.connectToWebSocketServer();
                }
            }
        });

    }

    public void updateDronesOnMap(Drone drone) {
        dronService.updateDronesSet(drones, drone);
        mapHelper.updateDronesOnMapView(drones);
    }

}



