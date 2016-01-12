package dron.mkapiczynski.pl.dronvision.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.map.MapAsyncTask;
import dron.mkapiczynski.pl.dronvision.map.MapHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisionFragment extends Fragment {
    private static final String TAG = VisionFragment.class.getSimpleName();

    private VisionFragmentActivityListener listener;
    private MapHelper mapHelper;

    // User Interface
    private MapView mapView;
    private Button refreshConnectionButton;

    // Drony, które mają być wizualizowane
    private Set<Drone> drones = Collections.synchronizedSet(new HashSet<Drone>());

    public VisionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vision, container, false);

        refreshConnectionButton = (Button) view.findViewById(R.id.refreshConnectionButton);

        mapView = (MapView) view.findViewById(R.id.MapView);

        mapHelper.setMapViewDefaultSettings(mapView, getActivity());

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

    // interfejs, który będzie implementować aktywność
    public interface VisionFragmentActivityListener {
        public void onRefreshConnectionButtonClicked();
    }

    public void updateMapView(Drone drone){
            MapAsyncTask mapAsyncTask = new MapAsyncTask(mapView, drone, drones, getActivity());
            mapAsyncTask.execute();
    }

}
