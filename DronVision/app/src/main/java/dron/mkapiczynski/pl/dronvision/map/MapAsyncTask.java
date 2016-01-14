package dron.mkapiczynski.pl.dronvision.map;

import android.app.Activity;
import android.os.AsyncTask;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.utils.DroneUtils;
import dron.mkapiczynski.pl.dronvision.utils.MapUtils;

/**
 * Created by Miix on 2016-01-09.
 */
public class MapAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private Drone drone;
    private Set<Drone> drones;
    private MapView mapView;
    private List<Overlay> mapOverlays = new ArrayList<>();

    public MapAsyncTask(MapView mapView, Drone drone, Set<Drone> drones, Activity activity) {
        this.drone = drone;
        this.drones = drones;
        this.activity = activity;
        this.mapView = mapView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mapOverlays.clear();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        DroneUtils.updateDronesSet(drones, drone);
        mapOverlays = MapUtils.updateMapOverlays(drones, mapView, activity);
        DroneUtils.updateDronesSearchedArea(drones);
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //GeoPoint mapCenter = getLastDroneInSetLocation(drones);
        mapView.getOverlays().clear();
        mapView.getOverlays().addAll(mapOverlays);
        mapView.postInvalidateOnAnimation();
        MapController mapController = (MapController) mapView.getController();
        mapController.animateTo(drone.getCurrentPosition());
    }

    @Override
    protected void onCancelled() {
    }




}
