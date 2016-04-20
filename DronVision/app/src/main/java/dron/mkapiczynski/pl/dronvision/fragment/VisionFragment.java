package dron.mkapiczynski.pl.dronvision.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.DroneHoleInSearchedArea;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.map.MapAsyncTask;
import dron.mkapiczynski.pl.dronvision.utils.MapUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisionFragment extends Fragment {
    private static final String TAG = VisionFragment.class.getSimpleName();

    private VisionFragmentActivityListener visionFragmentActivityListener;

    // User Interface
    private MapView mapView;
    //private Button refreshConnectionButton;
    private Button simulationRunningButton;
    private Button turnOffCurrentModeButton;
    private Button restartSimulationButton;

    // Drony, które mają być wizualizowane
    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());
    private Set<Drone> simulationDrones = Collections.synchronizedSet(new HashSet<Drone>());
    private Drone lastNotSimulationDrone;

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

        //refreshConnectionButton = (Button) view.findViewById(R.id.websocketConnectionStateButton);
        simulationRunningButton = (Button) view.findViewById(R.id.stopSimulationButton);
        turnOffCurrentModeButton = (Button) view.findViewById(R.id.turnOffSimulationModeButton);
        restartSimulationButton = (Button) view.findViewById(R.id.restartSimulationButton);
        simulationRunningButton.setVisibility(Button.GONE);
        turnOffCurrentModeButton.setVisibility(Button.GONE);
        restartSimulationButton.setVisibility(Button.GONE);

        mapView = (MapView) view.findViewById(R.id.MapView);

        MapUtils.setMapViewDefaultSettings(mapView, getActivity());

        setOnClickListnerForSimulationButton();

        restartSimulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visionFragmentActivityListener.onRestartSimulationButtonClicked();
            }
        });

        turnOffCurrentModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visionFragmentActivityListener.turnOffCurrentModeButtonClicked();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VisionFragmentActivityListener) {
            visionFragmentActivityListener = (VisionFragmentActivityListener) context;
        } else {
            throw new ClassCastException(context.toString() + " musi implementować interfejs: " +
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

    private void setOnClickListnerForSimulationButton() {
        simulationRunningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = simulationRunningButton.getText().toString();
                if ("Zatrzymaj symulację!".equals(buttonText)) {
                    visionFragmentActivityListener.onStopSimulationButtonCliecked();
                } else if ("Wznów symulację!".equals(buttonText)) {
                    visionFragmentActivityListener.onRerunSimulationButtonClicked();
                }

            }
        });
    }
    public boolean isSimulationMode() {
        return simulationMode;
    }

    public boolean isHistoryMode() {
        return historyMode;
    }

    public void turnOnHistoryMode(List<GeoPoint> searcheadAreaPoints, List<DroneHoleInSearchedArea> holeInSearchedArea) {
        historyMode = true;
        if (searcheadAreaPoints.size() > 1) {
            List<Overlay> mapOverlays = mapView.getOverlays();
            Polygon searchedArea = new Polygon(getContext());
            searchedArea.setPoints(searcheadAreaPoints);
            searchedArea.setFillColor(0x32121212);
            searchedArea.setStrokeColor(0x12121212);
            searchedArea.setStrokeWidth(3);
            mapOverlays.clear();
            mapOverlays.add(searchedArea);
            MapUtils.addHolesToMapOverlaysAsPolygons(holeInSearchedArea, mapOverlays, getActivity(), false);
            MapUtils.addScaleBarOverlayToMapView(mapOverlays, getActivity());
            MapController mapController = (MapController) mapView.getController();
            mapController.animateTo(searcheadAreaPoints.get(0));
            mapView.invalidate();
        }
        turnOffCurrentModeButton.setText("Wyjdź z trybu historii");
        turnOffCurrentModeButton.setVisibility(View.VISIBLE);
    }

    public void turnOffHistoryMode() {
        historyMode = false;
        showPostSimulationHistoryModeView();
    }


    public void updateMapView(Drone drone) {
        if (simulationMode) {
            if (simulationIsRunning) {
                updateMapViewInSimulationMode(drone);
            }
        } else if(!simulationMode && !historyMode){
            if (drone != null) {
                updateMapViewInNormalMode(drone);
            } else {
                showPostSimulationHistoryModeView();
            }
        }
    }

    private void updateMapViewInSimulationMode(Drone drone) {
        if (drone != null) {
            if (drone.getDroneId().compareTo(Parameters.SIMULATION_DRONE_ID) == 0) {
                MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, drone, simulationDrones, getActivity(), simulationIsRunning);
                mapAsyncTask.execute();
            }
        }
    }

    private void updateMapViewInNormalMode(Drone drone) {
        if (drone != null && drone.getDroneId().compareTo(Parameters.SIMULATION_DRONE_ID) != 0) {
            lastNotSimulationDrone = drone;
            MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, drone, drones, getActivity(), simulationIsRunning);
            mapAsyncTask.execute();
        }
    }

    public void turnOnSimulationMode() {
        simulationMode = true;
        simulationIsRunning = true;
        showPreSimulationView();
    }


    public void turnOffSimulationMode() {
        simulationMode = false;
        simulationIsRunning = false;
        showPostSimulationHistoryModeView();
    }


    public void stopSimulation(boolean ended) {
        simulationIsRunning = false;
        restartSimulationButton.setVisibility(Button.VISIBLE);
        turnOffCurrentModeButton.setVisibility(Button.VISIBLE);
        simulationRunningButton.setText("Wznów symulację!");
        if (ended) {
            Toast.makeText(getContext(), "Symulacja zakończona!", Toast.LENGTH_SHORT).show();
            simulationRunningButton.setEnabled(false);
        } else {
            Toast.makeText(getContext(), "Zatrzymano symulację!", Toast.LENGTH_SHORT).show();
        }
    }


    public void rerunSimulation() {
        simulationIsRunning = true;
        turnOffCurrentModeButton.setVisibility(Button.GONE);
        restartSimulationButton.setVisibility(Button.GONE);
        simulationRunningButton.setText("Zatrzymaj symulację!");
        Toast.makeText(getContext(), "Symulacja wznowiona!", Toast.LENGTH_SHORT).show();
    }

    private void showPreSimulationView() {
        showSimulationButtons();
        mapView.getController().setCenter(Parameters.SIMULATION_START_LOCATION);
        clearMapView();
    }


    private void showSimulationButtons() {
        simulationRunningButton.setEnabled(true);
        simulationRunningButton.setText("Zatrzymaj symulację!");
        turnOffCurrentModeButton.setText("Wyjdź z trybu symulacji");
        simulationRunningButton.setVisibility(Button.VISIBLE);
        turnOffCurrentModeButton.setVisibility(Button.GONE);
        restartSimulationButton.setVisibility(Button.GONE);
    }

    private void showPostSimulationHistoryModeView() {
        if (lastNotSimulationDrone != null) {
            updateMapView(lastNotSimulationDrone);
        } else {
           clearMapView();
        }
        removeSimulationModeButtonsFromScreen();
    }

    private void removeSimulationModeButtonsFromScreen() {
        simulationRunningButton.setVisibility(Button.GONE);
        turnOffCurrentModeButton.setVisibility(Button.GONE);
        restartSimulationButton.setVisibility(Button.GONE);
    }

    private void clearMapView(){
        simulationDrones.clear();
        MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, null, simulationDrones, getActivity(), simulationMode);
        mapAsyncTask.execute();
    }


    // interfejs, który będzie implementować aktywność
    public interface VisionFragmentActivityListener {
        public void onStopSimulationButtonCliecked();

        public void onRerunSimulationButtonClicked();

        public void onRestartSimulationButtonClicked();

        public void turnOffCurrentModeButtonClicked();
    }

}
