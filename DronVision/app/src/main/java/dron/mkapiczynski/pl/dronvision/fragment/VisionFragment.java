package dron.mkapiczynski.pl.dronvision.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.map.MapAsyncTask;
import dron.mkapiczynski.pl.dronvision.utils.MapUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisionFragment extends Fragment {
    private static final String TAG = VisionFragment.class.getSimpleName();

    private VisionFragmentActivityListener listener;

    // User Interface
    private MapView mapView;
    private Button refreshConnectionButton;
    private List<Overlay> overlaysStoredUntillSimulationIsOver = new ArrayList<>();

    // Drony, które mają być wizualizowane
    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());

    public VisionFragment() {
        // Required empty public constructor
    }

    private boolean simulationMode = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vision, container, false);

        refreshConnectionButton = (Button) view.findViewById(R.id.websocketConnectionStateButton);

        mapView = (MapView) view.findViewById(R.id.MapView);

        MapUtils.setMapViewDefaultSettings(mapView, getActivity());

        refreshConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRefreshConnectionButtonClicked();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof VisionFragmentActivityListener){
            listener = (VisionFragmentActivityListener) context;
        } else{
            throw new ClassCastException( context.toString() + " musi implementować interfejs: " +
                    "VisionFragment.VisionFragmentActivityListener");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            updateMapView(null);
        }
    }

    public void updateMapView(Drone drone) {
            MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, drone, drones, getActivity(), simulationMode);
            mapAsyncTask.execute();
    }

    public void storeMapOverlaysFotTheTimeOfSimulation(){
        simulationMode = true;
        if(mapView!=null && mapView.getOverlays()!=null) {
            if (overlaysStoredUntillSimulationIsOver!=null){
                overlaysStoredUntillSimulationIsOver.clear();
            }
            overlaysStoredUntillSimulationIsOver = mapView.getOverlays();
            mapView.getOverlays().clear();
            mapView.invalidate();
        }
    }

    public void restorePreviousOverlays(){
        simulationMode = false;
        if(mapView!=null && mapView.getOverlays()!=null) {
            mapView.getOverlays().clear();
            mapView.getOverlays().addAll(overlaysStoredUntillSimulationIsOver);
            mapView.invalidate();
            mapView.refreshDrawableState();
        }
    }

    // interfejs, który będzie implementować aktywność
    public interface VisionFragmentActivityListener {
        public void onRefreshConnectionButtonClicked();
    }

}
