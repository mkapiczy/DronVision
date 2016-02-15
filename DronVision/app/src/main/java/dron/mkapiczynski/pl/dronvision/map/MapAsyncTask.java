package dron.mkapiczynski.pl.dronvision.map;

import android.app.Activity;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.database.DBDrone;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.helper.SessionManager;
import dron.mkapiczynski.pl.dronvision.utils.DroneUtils;
import dron.mkapiczynski.pl.dronvision.utils.MapUtils;

/**
 * Created by Miix on 2016-01-09.
 */
public class MapAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;

    private MapView mapView;
    private List<Overlay> mapOverlays = new ArrayList<>();


    private Drone drone;
    private Set<Drone> drones;
    private List<DBDrone> trackedDrones;
    private List<DBDrone> visualizedDrones;

    private SessionManager sessionManager;

    public MapAsyncTask(MapView mapView, Drone drone, Set<Drone> drones, Activity activity) {
        this.drone = drone;
        this.drones = drones;
        this.activity = activity;
        this.mapView = mapView;
        sessionManager = new SessionManager(activity.getApplicationContext());
        trackedDrones = sessionManager.getTrackedDrones();
        visualizedDrones = sessionManager.getVisualizedDrones();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mapOverlays.clear();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(drone!=null) {
            DroneUtils.updateDronesSet(drones, drone);
        }
        mapOverlays = MapUtils.updateMapOverlays(drones,trackedDrones,visualizedDrones, mapView, activity);

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //GeoPoint mapCenter = getLastDroneInSetLocation(drones);
        mapView.getOverlays().clear();
        mapView.getOverlays().addAll(mapOverlays);
        mapView.postInvalidateOnAnimation();
        MapController mapController = (MapController) mapView.getController();
        if(drone!=null) {
            mapController.animateTo(drone.getCurrentPosition());
        }
    }

    @Override
    protected void onCancelled() {
    }




}
