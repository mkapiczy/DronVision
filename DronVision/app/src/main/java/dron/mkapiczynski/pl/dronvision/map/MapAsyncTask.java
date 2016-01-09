package dron.mkapiczynski.pl.dronvision.map;

import android.os.AsyncTask;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.util.Iterator;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.activity.VisionActivity;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.service.DronService;
import dron.mkapiczynski.pl.dronvision.service.DronServiceBean;

/**
 * Created by Miix on 2016-01-09.
 */
public class MapAsyncTask  {
    /*DronService dronService = new DronServiceBean();
    MapHelper mapHelper;

    private VisionActivity activity;
    private Drone drone;
    private Set<Drone> drones;
    private MapView mapView;

    public MapAsyncTask(Drone drone, Set<Drone> drones, VisionActivity activity){
        this.drone=drone;
        this.drones=drones;
        this.activity=activity;
        mapView = (MapView) activity.findViewById(R.id.MapView);
        mapHelper = new MapHelper(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mapView.getOverlays().clear();
        mapHelper.addScaleBarOverlayToMapView();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        dronService.updateDronesSet(drones, drone);
        Iterator<Drone> droneIterator = drones.iterator();
        while (droneIterator.hasNext()) {
           mapHelper.updateDronesOnMapView(drones);
        }

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //GeoPoint mapCenter = getLastDroneInSetLocation(drones);
        MapController mapController = (MapController) mapView.getController();
        mapController.animateTo(drone.getCurrentPosition());
        mapView.invalidate();
    }

    @Override
    protected void onCancelled() {

    }*/
}
