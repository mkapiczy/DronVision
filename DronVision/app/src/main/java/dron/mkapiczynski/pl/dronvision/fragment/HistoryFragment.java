package dron.mkapiczynski.pl.dronvision.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.database.DBDrone;
import dron.mkapiczynski.pl.dronvision.helper.CustomListViewAdapter;
import dron.mkapiczynski.pl.dronvision.helper.SessionManager;


public class HistoryFragment extends Fragment {

    private SessionManager sessionManager;
    private boolean viewCreated = false;

    private CustomListViewAdapter assignedDronesCustomAdapter;
    private ListView assignedDronesListView;
    private List<DBDrone> assignedDrones;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        assignedDronesListView = (ListView) view.findViewById(R.id.assignedDrones);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        viewCreated = true;

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            assignedDrones = sessionManager.getAssignedDrones();
            assignedDronesCustomAdapter = new CustomListViewAdapter(getContext(), assignedDrones, new ArrayList<DBDrone>(), false);
            assignedDronesListView.setAdapter(assignedDronesCustomAdapter);
            setListViewHeightBasedOnChildren(assignedDronesListView);
        } else if (hidden ==true){
            if(viewCreated) {

            }

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
