package dron.mkapiczynski.pl.dronvision.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;

import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.fragment.AboutAppFragment;
import dron.mkapiczynski.pl.dronvision.fragment.AboutAuthorFragment;
import dron.mkapiczynski.pl.dronvision.fragment.HistoryFragment;
import dron.mkapiczynski.pl.dronvision.fragment.PreferencesFragment;
import dron.mkapiczynski.pl.dronvision.fragment.SettingsFragment;
import dron.mkapiczynski.pl.dronvision.fragment.SimulationFragment;
import dron.mkapiczynski.pl.dronvision.fragment.VisionFragment;
import dron.mkapiczynski.pl.dronvision.websocket.MyWebSocketConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VisionFragment.VisionFragmentActivityListener, SimulationFragment.SimulationFragmentActivityListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    // UI
    private Toolbar toolbar;
    private NavigationView navigationView;
    private MenuItem currentMenuItem;
    private MenuItem visionMenuItem;

    // Fragmenty
    private FragmentManager fragmentManager;
    private VisionFragment visionFragment;
    private PreferencesFragment preferencesFragment;
    private SimulationFragment simulationFragment;
    private SettingsFragment settingsFragment;
    private HistoryFragment historyFragment;
    private AboutAppFragment aboutAppFragment;
    private AboutAuthorFragment aboutAuthorFragment;


    // Websocket
    private final MyWebSocketConnection client = new MyWebSocketConnection(this);

    private boolean historyMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        visionMenuItem = navigationView.getMenu().findItem(R.id.nav_vision);
        visionMenuItem.setChecked(true);
        currentMenuItem=visionMenuItem;

        client.connectToWebSocketServer();

        initiateFragmentManager();
    }

    private void initiateFragmentManager() {
        visionFragment = new VisionFragment();
        preferencesFragment = new PreferencesFragment();
        settingsFragment = new SettingsFragment();
        historyFragment = new HistoryFragment();
        aboutAppFragment = new AboutAppFragment();
        aboutAuthorFragment = new AboutAuthorFragment();
        simulationFragment = new SimulationFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, visionFragment)
                .add(R.id.fragment_container, preferencesFragment)
                .add(R.id.fragment_container, settingsFragment)
                .add(R.id.fragment_container, historyFragment)
                .add(R.id.fragment_container, aboutAppFragment)
                .add(R.id.fragment_container, aboutAuthorFragment)
                .add(R.id.fragment_container, simulationFragment)
                .hide(preferencesFragment)
                .hide(settingsFragment)
                .hide(historyFragment)
                .hide(aboutAppFragment)
                .hide(aboutAuthorFragment)
                .hide(simulationFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.disconnectFromWebsocketServer();
    }

    @Override
    public void onBackPressed() {
        if (hasWindowFocus()) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (visionFragment.isVisible()) {
                if(historyMode){
                    historyMode=false;
                } else {
                    AlertDialog logoutDialog = createLogoutDialog(this);
                    logoutDialog.show();
                }
            } else if (visionFragment.isHidden()) {
                if(historyFragment.isVisible() && !historyFragment.isDroneListShown()){
                   historyFragment.showDroneListView();
                } else {
                    showFragmentAndHideTheOthers(visionFragment);
                    currentMenuItem.setChecked(false);
                    visionMenuItem.setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        handleMenuItemsChecks(item);

        Fragment fragmentToBeShown = getFragmentToBeShownById(item.getItemId());
        showFragmentAndHideTheOthers(fragmentToBeShown);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleMenuItemsChecks(MenuItem item){
        if(currentMenuItem.getItemId() == R.id.nav_about_author || currentMenuItem.getItemId()==R.id.nav_about_app || item.getItemId()==R.id.nav_about_app || item.getItemId()== R.id.nav_about_author) {
            currentMenuItem.setChecked(false);
            item.setChecked(true);
            if((item.getItemId()==R.id.nav_about_app || item.getItemId()== R.id.nav_about_author)&& visionMenuItem.isChecked()){
                visionMenuItem.setChecked(false);
            }
        }
        currentMenuItem = item;
    }

    private Fragment getFragmentToBeShownById(int id){
        Fragment fragmentToBeShown = new Fragment();
        if (id == R.id.nav_vision) {
            fragmentToBeShown = visionFragment;
        } else if (id == R.id.nav_preferences) {
            fragmentToBeShown=preferencesFragment;
        } else if (id == R.id.nav_settings) {
            fragmentToBeShown=settingsFragment;
        } else if (id == R.id.nav_history) {
            fragmentToBeShown=historyFragment;
        } else if(id == R.id.nav_simulation){
            fragmentToBeShown=simulationFragment;
        } else if (id == R.id.nav_about_app) {
            fragmentToBeShown=aboutAppFragment;
        } else if (id == R.id.nav_about_author) {
            fragmentToBeShown=aboutAuthorFragment;
        }
        return fragmentToBeShown;
    }

    private void showFragmentAndHideTheOthers(Fragment fragmentToBeShown) {
        if (fragmentToBeShown!=null && fragmentToBeShown.isHidden()) {
            List<Fragment> allFragments = fragmentManager.getFragments();
            for (int i = 0; i < allFragments.size(); i++) {
                Fragment currentIteratedFragment = allFragments.get(i);
                if (!currentIteratedFragment.equals(fragmentToBeShown)) {
                    if (currentIteratedFragment.isVisible()) {
                        fragmentManager.beginTransaction().hide(currentIteratedFragment).show(fragmentToBeShown).commit();
                        return;
                    }
                }
            }
        }
    }

    public void updateDronesOnMap(Drone drone) {
        if(!historyMode) {
            visionFragment.updateMapView(drone);
        }
    }

    public void displayHistoryOnMap(List<GeoPoint> searcheadAreaPoints){
        historyMode=true;
        visionFragment.displayHitorySearchedArea(searcheadAreaPoints);
        currentMenuItem.setChecked(false);
        visionMenuItem.setChecked(true);
        showFragmentAndHideTheOthers(visionFragment);
    }


    @Override
    public void onStopSimulationButtonCliecked() {
        if(client.isConnected()){
            client.sendSimulationMessageToServer(Parameters.STOP_SIMULATION_MESSAGE_TASK);
            stopSimulation(false);
        }

    }

    @Override
    public void onRerunSimulationButtonClicked() {
        if(client.isConnected()){
            client.sendSimulationMessageToServer(Parameters.RERUN_SIMULATION_MESSAGE_TASK);
            rerunSimulation();
        }

    }

    @Override
    public void onRestartSimulationButtonClicked() {
        if(client.isConnected()) {
            visionFragment.turnOnSimulationMode();
            client.sendSimulationMessageToServer(Parameters.START_SIMULATION_MESSAGE_TASK);
        }
    }

    @Override
    public void onTurnOffSimulationModeButtonClicked() {
        turnOffSimulationMode();
    }

    @Override
    public void onTurnOnSimulationButtonClickedInSimulationFragment() {
        if(client.isConnected()){
            visionFragment.turnOnSimulationMode();
            client.sendSimulationMessageToServer(Parameters.START_SIMULATION_MESSAGE_TASK);
            currentMenuItem.setChecked(false);
            visionMenuItem.setChecked(true);
            showFragmentAndHideTheOthers(visionFragment);
        }
    }

    public void stopSimulation(boolean ended){
        visionFragment.stopSimulation();
        if(ended){
            Toast.makeText(getApplicationContext(), "Symulacja zakończona!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getApplicationContext(), "Zatrzymano symulację!", Toast.LENGTH_SHORT).show();
        }
    }

    public void rerunSimulation(){
        visionFragment.rerunSimulation();
        Toast.makeText(getApplicationContext(), "Symulacja wznowiona!", Toast.LENGTH_SHORT).show();
    }

    public void turnOffSimulationMode(){
        if(client.isConnected()) {
            client.sendSimulationMessageToServer(Parameters.END_SIMULATION_MESSAGE_TASK);
            visionFragment.turnOffSimulationMode();
            simulationFragment.turnOffSimulationInSimulationFragment();
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
                        client.disconnectFromWebsocketServer();
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
