package dron.mkapiczynski.pl.dronvision.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.database.DBDrone;
import dron.mkapiczynski.pl.dronvision.database.DroneStatusEnum;
import dron.mkapiczynski.pl.dronvision.helper.CustomListViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends Fragment {


    private ListView trackedDronesListView;
    private ListView visualizedDronesListView;

    private List<DBDrone> drones;


    public PreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        trackedDronesListView = (ListView) view.findViewById(R.id.trackedDroneList);
        visualizedDronesListView = (ListView) view.findViewById(R.id.visualizedDroneList);


        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            /**
             * aktualizacja dronów z serwera i bazy
             */
            drones = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                DBDrone dbDrone = new DBDrone();
                dbDrone.setDroneId(i);
                dbDrone.setDroneName("Drone" + i);
                dbDrone.setDroneDescription("To jest drone nr: " + i);
                dbDrone.setTracked(true);
                dbDrone.setVisualized(true);
                dbDrone.setDroneStatus(DroneStatusEnum.ONLINE);
                dbDrone.setLastLocation(new GeoPoint(22.54, 58.64));
                drones.add(dbDrone);
            }
            CustomListViewAdapter customAdapter = new CustomListViewAdapter(getContext(), drones);
            trackedDronesListView.setAdapter(customAdapter);
            visualizedDronesListView.setAdapter(customAdapter);
            setListViewHeightBasedOnChildren(trackedDronesListView);
            setListViewHeightBasedOnChildren(visualizedDronesListView);
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
