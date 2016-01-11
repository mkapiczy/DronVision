package dron.mkapiczynski.pl.dronvision.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;
import dron.mkapiczynski.pl.dronvision.map.MapAsyncTask;
import dron.mkapiczynski.pl.dronvision.map.MapHelper;
import dron.mkapiczynski.pl.dronvision.service.DronService;
import dron.mkapiczynski.pl.dronvision.service.DronServiceBean;
import dron.mkapiczynski.pl.dronvision.websocket.MyWebSocketConnection;

public class VisionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = VisionActivity.class.getSimpleName();
    // UI
    private Toolbar toolbar;
    private NavigationView navigationView;
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
        setContentView(R.layout.activity_vision);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        refreshConnectionButton = (Button) findViewById(R.id.refreshConnectionButton);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapView = (MapView) findViewById(R.id.MapView);

        overlayItemList = new ArrayList<>();
        defaultResourceProxyImpl = new DefaultResourceProxyImpl(this);

        mapHelper = new MapHelper(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            AlertDialog logoutDialog = createLogoutDialog(this);
            logoutDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vision, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_vision) {
            // Handle the camera action
        } else if (id == R.id.nav_preferences) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private AlertDialog createLogoutDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Wylogowanie");

        alertDialogBuilder
                .setMessage("Jesteś pewien, że chcesz się wylogować?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("prevActivity", "GPS");
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
                        VisionActivity.this.finish();
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog logoutDialog = alertDialogBuilder.create();

        return logoutDialog;
    }

    public void updateDronesOnMap(Drone drone) {
        MapAsyncTask mapAsyncTask = new MapAsyncTask(drone,drones, this);
        mapAsyncTask.execute();
        /*dronService.updateDronesSet(drones, drone);
        mapHelper.updateDronesOnMapView(drones);*/
    }
}
