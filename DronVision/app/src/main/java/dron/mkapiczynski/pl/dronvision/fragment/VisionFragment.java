package dron.mkapiczynski.pl.dronvision.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
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
    private Button simulationButton;
    private Button turnOffSimulationModeButton;
    private Button restartSimulationButton;
    private List<Overlay> overlaysStoredUntillSimulationIsOver = new ArrayList<>();

    // Drony, które mają być wizualizowane
    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());
    private Set<Drone> simulationDrones = Collections.synchronizedSet(new HashSet<Drone>());
    private Drone lastRealDrone;

    public VisionFragment() {
        // Required empty public constructor
    }

    private boolean simulationMode = false;
    private boolean historyMode = false;
    private boolean simulationIsRunning = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vision, container, false);

        refreshConnectionButton = (Button) view.findViewById(R.id.websocketConnectionStateButton);
        simulationButton = (Button) view.findViewById(R.id.stopSimulationButton);
        turnOffSimulationModeButton = (Button) view.findViewById(R.id.turnOffSimulationModeButton);
        restartSimulationButton = (Button) view.findViewById(R.id.restartSimulationButton);
         simulationButton.setVisibility(Button.GONE);
        turnOffSimulationModeButton.setVisibility(Button.GONE);
        restartSimulationButton.setVisibility(Button.GONE);

        mapView = (MapView) view.findViewById(R.id.MapView);

        MapUtils.setMapViewDefaultSettings(mapView, getActivity());


        setOnClickListnerForSimulationButton();

        restartSimulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRestartSimulationButtonClicked();
            }
        });

        turnOffSimulationModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTurnOffSimulationModeButtonClicked();
            }
        });

        return view;
    }

    private void setOnClickListnerForSimulationButton(){
        simulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = simulationButton.getText().toString();
                if ("Zatrzymaj symulację!".equals(buttonText)) {
                    listener.onStopSimulationButtonCliecked();
                } else if ("Wznów symulację!".equals(buttonText)) {
                    listener.onRerunSimulationButtonClicked();
                }

            }
        });
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

    public void turnOnHistoryMode(List<GeoPoint> searcheadAreaPoints){
        if(!simulationMode) {
            historyMode = true;
            if (searcheadAreaPoints.size() > 1) {
                Polygon searchedArea = new Polygon(getContext());
                searchedArea.setPoints(searcheadAreaPoints);
                searchedArea.setFillColor(0x32121212);
                searchedArea.setStrokeColor(0x12121212);
                searchedArea.setStrokeWidth(3);
                mapView.getOverlays().clear();
                mapView.getOverlays().add(searchedArea);
                MapUtils.addScaleBarOverlayToMapView(mapView.getOverlays(), getActivity());
                MapController mapController = (MapController) mapView.getController();
                mapController.animateTo(searcheadAreaPoints.get(0));
                mapView.invalidate();
            }
            turnOffSimulationModeButton.setText("Wyjdź z trybu historii");
            turnOffSimulationModeButton.setVisibility(View.VISIBLE);
        }
    }

    public void disableRerunSimulationButton(){
        simulationButton.setEnabled(false);
    }

    public void turnOffHistoryMode(){
        historyMode=false;
        simulationDrones.clear();
        if(lastRealDrone!=null){
            updateMapView(lastRealDrone);
        } else{
            MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, null, simulationDrones, getActivity(), simulationMode);
            mapAsyncTask.execute();
        }
        turnOffSimulationModeButton.setVisibility(Button.GONE);
    }
    public void updateMapView(Drone drone) {
        if(simulationMode){
            if(simulationIsRunning) {
                if (drone != null) {
                    if (drone.getDroneId().compareTo(Parameters.SIMULATION_DRONE_ID) == 0) {
                        MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, drone, simulationDrones, getActivity(), simulationIsRunning);
                        mapAsyncTask.execute();
                    }
                }
            }
        } else {
            if(drone!=null && drone.getDroneId().compareTo(Parameters.SIMULATION_DRONE_ID)!=0) {
                lastRealDrone = drone;
                MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, drone, drones, getActivity(), simulationIsRunning);
                mapAsyncTask.execute();
            }
        }
    }

    public void turnOnSimulationMode(){
        if(!historyMode) {
            simulationButton.setEnabled(true);
            simulationButton.setText("Zatrzymaj symulację!");
            turnOffSimulationModeButton.setText("Wyjdź z trybu historii");
            simulationButton.setVisibility(Button.VISIBLE);
            turnOffSimulationModeButton.setVisibility(Button.GONE);
            restartSimulationButton.setVisibility(Button.GONE);
            simulationMode = true;
            simulationIsRunning = true;
            simulationDrones.clear();
            mapView.getController().setCenter(Parameters.SIMULATION_START_LOCATION);
            MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, null, simulationDrones, getActivity(), simulationMode);
            mapAsyncTask.execute();
        }
    }

    public void turnOffSimulationMode(){
        simulationMode=false;
        simulationIsRunning=false;
        simulationDrones.clear();
        if(lastRealDrone!=null){
            updateMapView(lastRealDrone);
        } else{
            MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, null, simulationDrones, getActivity(), simulationMode);
            mapAsyncTask.execute();
        }
        simulationButton.setVisibility(Button.GONE);
        turnOffSimulationModeButton.setVisibility(Button.GONE);
        restartSimulationButton.setVisibility(Button.GONE);
    }


    public void stopSimulation(){
        simulationIsRunning=false;
        if(simulationMode) {
            turnOffSimulationModeButton.setVisibility(Button.VISIBLE);
            restartSimulationButton.setVisibility(Button.VISIBLE);
        }
        simulationButton.setText("Wznów symulację!");
    }

    public void rerunSimulation(){
        simulationIsRunning=true;
        turnOffSimulationModeButton.setVisibility(Button.GONE);
        restartSimulationButton.setVisibility(Button.GONE);
        simulationButton.setText("Zatrzymaj symulację!");
    }



    // interfejs, który będzie implementować aktywność
    public interface VisionFragmentActivityListener {
        public void onStopSimulationButtonCliecked();
        public void onRerunSimulationButtonClicked();
        public void onRestartSimulationButtonClicked();
        public void onTurnOffSimulationModeButtonClicked();
    }

}
