package dron.mkapiczynski.pl.dronvision.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.activity.MainActivity;
import dron.mkapiczynski.pl.dronvision.database.DBDrone;
import dron.mkapiczynski.pl.dronvision.domain.DroneSession;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.helper.CustomHistoryDroneSessionsListViewAdapter;
import dron.mkapiczynski.pl.dronvision.helper.CustomHistoryDronesListViewAdapter;
import dron.mkapiczynski.pl.dronvision.helper.SessionManager;
import dron.mkapiczynski.pl.dronvision.message.GetDroneSessionsMessage;
import dron.mkapiczynski.pl.dronvision.message.GetSearchedAreaMessage;
import dron.mkapiczynski.pl.dronvision.message.MessageDecoder;


public class HistoryFragment extends Fragment {

    private SessionManager sessionManager;
    private boolean viewCreated = false;

    private CustomHistoryDronesListViewAdapter assignedDronesCustomAdapter;
    private CustomHistoryDroneSessionsListViewAdapter sessionsAdapter;
    private ListView historyListView;
    private List<DBDrone> assignedDrones;
    private List<DroneSession> droneSessions;
    private TextView historyListTitleTextView;
    private GetDroneSessionsTask getDroneSessionsTask;
    private GetSearchedAreaTask getSearchedAreaTask;

    private boolean droneListShown = true;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        historyListTitleTextView = (TextView) view.findViewById(R.id.historyListTitle);
        historyListView = (ListView) view.findViewById(R.id.assignedDrones);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        viewCreated = true;

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            showDroneListView();
        } else if (hidden == true) {
            if (viewCreated) {

            }

        }
    }

    public void changeHistoryListTitle(String text) {
        historyListTitleTextView.setText(text);
    }

    public void showDroneListView() {
        droneListShown = true;
        assignedDrones = sessionManager.getAssignedDrones();
        assignedDronesCustomAdapter = new CustomHistoryDronesListViewAdapter(this, getContext(), assignedDrones);
        historyListView.setAdapter(assignedDronesCustomAdapter);
        setListViewHeightBasedOnChildren(historyListView);
    }


    public void showDroneSessionIdListView(Long droneId) {
        droneListShown = false;
        getDroneSessionsTask = new GetDroneSessionsTask(droneId, this);
        getDroneSessionsTask.execute((Void) null);
    }

    public boolean isHistoryModeEnabled() {
        return !((MainActivity) getActivity()).isSimulationModeTurned();
    }

    public void showHistory(Long sessionId) {
        getSearchedAreaTask = new GetSearchedAreaTask(sessionId, this);
        getSearchedAreaTask.execute((Void) null);
    }

    public boolean isDroneListShown() {
        return droneListShown;
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

    public class GetDroneSessionsTask extends AsyncTask<Void, Void, Boolean> {

        private final Long droneId;
        private HistoryFragment historyFragment;


        GetDroneSessionsTask(Long droneId, HistoryFragment historyFragment) {
            this.droneId = droneId;
            this.historyFragment = historyFragment;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String requestUrl = Parameters.getHistoryGetSessionsRequestUrl();
            try {
                requestUrl += "?droneId=" + URLEncoder.encode(droneId.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("charset", "UTF-8");
                conn.setConnectTimeout(2500);
                conn.setReadTimeout(2500);
                conn.setUseCaches(false);


                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    GetDroneSessionsMessage getDroneSessionsMessage = MessageDecoder.decodeGetDroneSessionsMessage(sb.toString());
                    if (getDroneSessionsMessage != null) {
                        droneSessions = getDroneSessionsMessage.getDroneSessions();
                    }
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getDroneSessionsTask = null;
            if (success) {
                updateListViewsWithReceivedData();
                updateSharedPreferencesWithReceivedData();
            } else {
                clearListViews();
                //  networkErrorTextView.setVisibility(View.VISIBLE);
            }
            // showProgress(false);
        }


        @Override
        protected void onCancelled() {
            getDroneSessionsTask = null;
            clearListViews();
            //networkErrorTextView.setVisibility(View.VISIBLE);
            // showProgress(false);
        }

        private void updateListViewsWithReceivedData() {
            sessionsAdapter = new CustomHistoryDroneSessionsListViewAdapter(historyFragment, getContext(), droneSessions);
            historyListView.setAdapter(sessionsAdapter);
            setListViewHeightBasedOnChildren(historyListView);
        }

        private void updateSharedPreferencesWithReceivedData() {

        }

        private void clearListViews() {
           /* followedDroneCustomAdapter = new CustomPreferencesListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), true);
            followedDroneListView.setAdapter(followedDroneCustomAdapter);
            trackedDronesCustomAdapter = new CustomPreferencesListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            trackedDronesListView.setAdapter(trackedDronesCustomAdapter);
            visualizedDronesCustomAdapter = new CustomPreferencesListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            visualizedDronesListView.setAdapter(visualizedDronesCustomAdapter);
            setListViewHeightBasedOnChildren(trackedDronesListView);
            setListViewHeightBasedOnChildren(visualizedDronesListView);
            setListViewHeightBasedOnChildren(followedDroneListView);
            trackedDronesCustomAdapter = null;
            visualizedDronesCustomAdapter = null;
            followedDroneCustomAdapter = null;*/
        }
    }

    public class GetSearchedAreaTask extends AsyncTask<Void, Void, Boolean> {

        private final Long sessionId;
        private HistoryFragment historyFragment;
        private List<MyGeoPoint> receivedSearchedArea;


        GetSearchedAreaTask(Long sessionId, HistoryFragment historyFragment) {
            this.sessionId = sessionId;
            this.historyFragment = historyFragment;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String requestUrl = Parameters.getHistoryGetSearchedAreaRequestUrl();
            try {
                requestUrl += "?sessionId=" + URLEncoder.encode(sessionId.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("charset", "UTF-8");
                conn.setConnectTimeout(2500);
                conn.setReadTimeout(2500);
                conn.setUseCaches(false);


                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    GetSearchedAreaMessage getSearchedAreaMessage = MessageDecoder.decodeGetSearchedAreaMessage(sb.toString());
                    if (getSearchedAreaMessage != null) {
                        receivedSearchedArea = getSearchedAreaMessage.getSearchedArea();
                    }
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getDroneSessionsTask = null;
            if (success) {
                updateListViewsWithReceivedData();
                updateSharedPreferencesWithReceivedData();
            } else {
                clearListViews();
                //  networkErrorTextView.setVisibility(View.VISIBLE);
            }
            // showProgress(false);
        }


        @Override
        protected void onCancelled() {
            getDroneSessionsTask = null;
            clearListViews();
            //networkErrorTextView.setVisibility(View.VISIBLE);
            // showProgress(false);
        }

        private void updateListViewsWithReceivedData() {
            if (receivedSearchedArea != null) {
                List<GeoPoint> searchedArea = new ArrayList<>();
                for (int i = 0; i < receivedSearchedArea.size(); i++) {
                    searchedArea.add(new GeoPoint(receivedSearchedArea.get(i).getLatitude(), receivedSearchedArea.get(i).getLongitude()));
                }
                ((MainActivity) historyFragment.getActivity()).turnOnHistoryMode(searchedArea);
            } else {
                Toast.makeText(getContext(), "NULL", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateSharedPreferencesWithReceivedData() {

        }

        private void clearListViews() {
           /* followedDroneCustomAdapter = new CustomPreferencesListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), true);
            followedDroneListView.setAdapter(followedDroneCustomAdapter);
            trackedDronesCustomAdapter = new CustomPreferencesListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            trackedDronesListView.setAdapter(trackedDronesCustomAdapter);
            visualizedDronesCustomAdapter = new CustomPreferencesListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            visualizedDronesListView.setAdapter(visualizedDronesCustomAdapter);
            setListViewHeightBasedOnChildren(trackedDronesListView);
            setListViewHeightBasedOnChildren(visualizedDronesListView);
            setListViewHeightBasedOnChildren(followedDroneListView);
            trackedDronesCustomAdapter = null;
            visualizedDronesCustomAdapter = null;
            followedDroneCustomAdapter = null;*/
        }
    }
}
