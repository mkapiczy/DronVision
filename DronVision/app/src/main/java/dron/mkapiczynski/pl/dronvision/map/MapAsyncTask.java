package dron.mkapiczynski.pl.dronvision.map;

import android.app.Activity;
import android.os.AsyncTask;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.domain.DBDrone;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.utils.SessionManager;
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
    private DBDrone followedDrone;
    private boolean simulationMode;

    private SessionManager sessionManager;

    public MapAsyncTask(MapView mapView, Drone drone, Set<Drone> drones, Activity activity, boolean simulationMode) {
        this.drone = drone;
        this.activity = activity;
        this.mapView = mapView;
        this.drones = drones;
        this.simulationMode = simulationMode;
        sessionManager = new SessionManager(activity.getApplicationContext());
        if (simulationMode) {
            trackedDrones = new ArrayList<>();
            visualizedDrones = new ArrayList<>();
            followedDrone = new DBDrone();
            if (drone != null) {
                DBDrone dbDrone = new DBDrone();
                dbDrone.setDroneId(drone.getDroneId());
                trackedDrones.add(dbDrone);
                visualizedDrones.add(dbDrone);
                followedDrone = dbDrone;
            }
        } else {
            trackedDrones = sessionManager.getTrackedDrones();
            visualizedDrones = sessionManager.getVisualizedDrones();
            followedDrone = sessionManager.getFollowedDrone();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mapOverlays.clear();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (drone != null) {
            DroneUtils.updateDronesSet(drones, drone);
        }
        mapOverlays = MapUtils.updateMapOverlays(drones, trackedDrones, visualizedDrones, mapView, activity);

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        mapView.getOverlays().clear();
        mapView.getOverlays().addAll(mapOverlays);
        mapView.postInvalidateOnAnimation();
        MapController mapController = (MapController) mapView.getController();
        if (drone != null) {
            if (followedDrone != null && followedDrone.getDroneId() != null && followedDrone.getDroneId().compareTo(drone.getDroneId()) == 0) {
                mapController.animateTo(drone.getCurrentPosition());
                sessionManager.setLastMapCenter(drone.getCurrentPosition());
            }
        }
    }

    @Override
    protected void onCancelled() {
    }


}
