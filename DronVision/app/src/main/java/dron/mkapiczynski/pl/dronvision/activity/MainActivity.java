package dron.mkapiczynski.pl.dronvision.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.fragment.PreferencesFragment;
import dron.mkapiczynski.pl.dronvision.fragment.SettingsFragment;
import dron.mkapiczynski.pl.dronvision.fragment.VisionFragment;
import dron.mkapiczynski.pl.dronvision.websocket.MyWebSocketConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VisionFragment.VisionFragmentActivityListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    // UI
    private Toolbar toolbar;
    private NavigationView navigationView;

    // Fragmenty
    private FragmentManager fragmentManager;
    private VisionFragment visionFragment;
    private PreferencesFragment preferencesFragment;
    private SettingsFragment settingsFragment;


    // Websocket
    private final MyWebSocketConnection client = new MyWebSocketConnection(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        client.connectToWebSocketServer();
        //ask for drones ascribed to this client account


        visionFragment = new VisionFragment();
        preferencesFragment = new PreferencesFragment();
        settingsFragment = new SettingsFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, visionFragment)
                .add(R.id.fragment_container, preferencesFragment)
                .add(R.id.fragment_container, settingsFragment)
                .hide(preferencesFragment)
                .hide(settingsFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnectFromWebsocketServer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (visionFragment.isVisible()) {
            AlertDialog logoutDialog = createLogoutDialog(this);
            logoutDialog.show();
        } else {
            if (visionFragment.isHidden()) {
                if (preferencesFragment.isVisible()) {
                    fragmentManager.beginTransaction().hide(preferencesFragment).show(visionFragment).commit();
                } else if(settingsFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(settingsFragment).show(visionFragment).commit();
                }
            }
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_vision) {
            if (visionFragment.isHidden()) {
                if (preferencesFragment.isVisible()) {
                    fragmentManager.beginTransaction().hide(preferencesFragment).show(visionFragment).commit();
                } else if(settingsFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(settingsFragment).show(visionFragment).commit();
                }
            }
        } else if (id == R.id.nav_preferences) {
            if(preferencesFragment.isHidden()){
                if(visionFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(visionFragment).show(preferencesFragment).commit();
                } else if(settingsFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(settingsFragment).show(preferencesFragment).commit();
                }
            }
        } else if (id == R.id.nav_settings) {
            if(settingsFragment.isHidden()){
                if(visionFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(visionFragment).show(settingsFragment).commit();
                } else if(preferencesFragment.isVisible()){
                    fragmentManager.beginTransaction().hide(preferencesFragment).show(settingsFragment).commit();
                }
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateDronesOnMap(Drone drone) {
        visionFragment.updateMapView(drone);
    }

    @Override
    public void onRefreshConnectionButtonClicked() {
        if (!client.isConnected()) {
            client.connectToWebSocketServer();
        }
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
                        MainActivity.this.finish();
                        Toast.makeText(getApplicationContext(), "Zostałeś wylogowany", Toast.LENGTH_SHORT).show();
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
}
