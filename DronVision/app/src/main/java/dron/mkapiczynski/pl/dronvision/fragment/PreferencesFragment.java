package dron.mkapiczynski.pl.dronvision.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.activity.MainActivity;
import dron.mkapiczynski.pl.dronvision.database.DBDrone;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.helper.CustomListViewAdapter;
import dron.mkapiczynski.pl.dronvision.helper.SessionManager;
import dron.mkapiczynski.pl.dronvision.message.MessageDecoder;
import dron.mkapiczynski.pl.dronvision.message.GetPreferencesMessage;
import dron.mkapiczynski.pl.dronvision.message.SetPreferencesMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends Fragment {

    private View preferencesView;
    private View progressView;

    private ListView trackedDronesListView;
    private ListView visualizedDronesListView;
    private ListView followedDroneListView;

    private CustomListViewAdapter trackedDronesCustomAdapter;
    private CustomListViewAdapter visualizedDronesCustomAdapter;
    private CustomListViewAdapter followedDroneCustomAdapter;

    private TextView networkErrorTextView;

    private List<DBDrone> assignedDrones;
    private List<DBDrone> trackedDrones;
    private List<DBDrone> visualizedDrones;
    private DBDrone followedDrone;

    private GetPreferencesTask getPreferencesTask = null;
    private SetPreferencesTask setPreferencesTask = null;

    private SessionManager sessionManager;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    private boolean viewCreated = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        trackedDronesListView = (ListView) view.findViewById(R.id.trackedDroneList);
        visualizedDronesListView = (ListView) view.findViewById(R.id.visualizedDroneList);
        followedDroneListView = (ListView) view.findViewById(R.id.folllowedDroneList);

        networkErrorTextView = (TextView) view.findViewById(R.id.preferencesNetworkErrorTextView);
        networkErrorTextView.setVisibility(View.INVISIBLE);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        preferencesView = view.findViewById(R.id.preferencesView);
        progressView = view.findViewById(R.id.preferencesProgress);

        viewCreated = true;

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            showProgress(true);
            networkErrorTextView.setVisibility(View.INVISIBLE);
            getPreferencesTask = new GetPreferencesTask(sessionManager.getLoggedUserLogin());
            getPreferencesTask.execute((Void) null);
        } else if (hidden == true) {
            if (viewCreated) {
                if (trackedDronesCustomAdapter != null && visualizedDronesCustomAdapter != null) {
                    boolean trackedDronesChanged = trackedDronesCustomAdapter.wasChanged();
                    boolean visualizedDronesChanged = visualizedDronesCustomAdapter.wasChanged();
                    if (trackedDronesChanged || visualizedDronesChanged) {
                        updateTrackedAndVisualizedDronesPreferences(trackedDronesChanged,visualizedDronesChanged);
                    }
                }
                if (followedDroneCustomAdapter != null) {
                    updateFollowedDronePreference();
                }
            }

        }
    }

    private void updateTrackedAndVisualizedDronesPreferences(boolean trackedDronesChanged, boolean visualizedDronesChanged){
        List<DBDrone> newTrackedDrones = getUpdatedDronesList(assignedDrones, trackedDronesCustomAdapter.getCheckedDrones());
        List<DBDrone> newVisualizedDrones = getUpdatedDronesList(assignedDrones, visualizedDronesCustomAdapter.getCheckedDrones());
        setPreferencesTask = new SetPreferencesTask(sessionManager.getLoggedUserLogin(), newTrackedDrones, newVisualizedDrones, trackedDronesChanged, visualizedDronesChanged);
        setPreferencesTask.execute((Void) null);
    }
    private void updateFollowedDronePreference(){
        List<DBDrone> followedDronesList = followedDroneCustomAdapter.getCheckedDrones();
        if (followedDronesList != null && !followedDronesList.isEmpty()) {
            followedDrone = followedDronesList.get(0);
            sessionManager.setFollowedDrone(followedDrone);
        } else {
            sessionManager.setFollowedDrone(null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            preferencesView.setVisibility(show ? View.GONE : View.VISIBLE);
            preferencesView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    preferencesView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            preferencesView.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }

    private List<DBDrone> getUpdatedDronesList(List<DBDrone> assignedDrones, List<DBDrone> checkedDrones) {
        List<DBDrone> updatedDronesList = new ArrayList<>();
        for (int i = 0; i < assignedDrones.size(); i++) {
            for (int j = 0; j < checkedDrones.size(); j++) {
                if (assignedDrones.get(i).getDroneId() == checkedDrones.get(j).getDroneId()) {
                    updatedDronesList.add(assignedDrones.get(i));
                }
            }
        }
        return updatedDronesList;
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

    public class GetPreferencesTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;


        GetPreferencesTask(String login) {
            this.login = login;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String requestUrl = Parameters.PREFERENCES_REQUEST_URL;
            try {
                requestUrl += "?login=" + URLEncoder.encode(login, "UTF-8");
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
                    GetPreferencesMessage getPreferencesMessage = MessageDecoder.decodePreferencesMessage(sb.toString());
                    if (getPreferencesMessage != null) {
                        assignedDrones = getPreferencesMessage.getAssignedDrones();
                        trackedDrones = getPreferencesMessage.getTrackedDrones();
                        visualizedDrones = getPreferencesMessage.getVisualizedDrones();
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
            getPreferencesTask = null;
            if (success) {
                updateListViewsWithReceivedData();
                updateSharedPreferencesWithReceivedData();
            } else {
                clearListViews();
                networkErrorTextView.setVisibility(View.VISIBLE);
            }
            showProgress(false);
        }


        @Override
        protected void onCancelled() {
            getPreferencesTask = null;
            clearListViews();
            networkErrorTextView.setVisibility(View.VISIBLE);
            showProgress(false);
        }

        private void updateListViewsWithReceivedData() {
            followedDrone = sessionManager.getFollowedDrone();
            List<DBDrone> followedDroneList = new ArrayList<>();
            if (followedDrone != null) {
                followedDroneList.add(followedDrone);
            }
            followedDroneCustomAdapter = new CustomListViewAdapter(getContext(), assignedDrones, followedDroneList, true);
            followedDroneListView.setAdapter(followedDroneCustomAdapter);
            trackedDronesCustomAdapter = new CustomListViewAdapter(getContext(), assignedDrones, trackedDrones, false);
            trackedDronesListView.setAdapter(trackedDronesCustomAdapter);
            visualizedDronesCustomAdapter = new CustomListViewAdapter(getContext(), assignedDrones, visualizedDrones, false);
            visualizedDronesListView.setAdapter(visualizedDronesCustomAdapter);
            setListViewHeightBasedOnChildren(trackedDronesListView);
            setListViewHeightBasedOnChildren(visualizedDronesListView);
            setListViewHeightBasedOnChildren(followedDroneListView);
        }

        private void updateSharedPreferencesWithReceivedData() {
            sessionManager.setAssignedDrones(assignedDrones);
            sessionManager.setTrackedDrones(trackedDrones);
            sessionManager.setVisuazliedDrones(visualizedDrones);
        }

        private void clearListViews() {
            followedDroneCustomAdapter = new CustomListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), true);
            followedDroneListView.setAdapter(followedDroneCustomAdapter);
            trackedDronesCustomAdapter = new CustomListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            trackedDronesListView.setAdapter(trackedDronesCustomAdapter);
            visualizedDronesCustomAdapter = new CustomListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            visualizedDronesListView.setAdapter(visualizedDronesCustomAdapter);
            setListViewHeightBasedOnChildren(trackedDronesListView);
            setListViewHeightBasedOnChildren(visualizedDronesListView);
            setListViewHeightBasedOnChildren(followedDroneListView);
            trackedDronesCustomAdapter = null;
            visualizedDronesCustomAdapter = null;
            followedDroneCustomAdapter = null;
        }
    }

    public class SetPreferencesTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final List<DBDrone> trackedDrones;
        private final List<DBDrone> visualizedDrones;
        private final boolean trackedDronesChanged;
        private final boolean visualizedDronesChanged;


        SetPreferencesTask(String login, List<DBDrone> trackedDrones, List<DBDrone> visualizedDrones, boolean trackedDronesChanged, boolean visualizedDronesChanged) {
            this.login = login;
            this.trackedDrones = trackedDrones;
            this.visualizedDrones = visualizedDrones;
            this.trackedDronesChanged = trackedDronesChanged;
            this.visualizedDronesChanged = visualizedDronesChanged;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SetPreferencesMessage message = new SetPreferencesMessage();
            message.setLogin(login);
            if (trackedDronesChanged) {
                message.setTrackedDronesChanged(trackedDronesChanged);
                message.setTrackedDrones(trackedDrones);
            }
            if (visualizedDronesChanged) {
                message.setVisualizedDronesChanged(visualizedDronesChanged);
                message.setVisualizedDrones(visualizedDrones);
            }

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String urlParameters = "message=" + gson.toJson(message);
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            String requestUrl = Parameters.PREFERENCES_REQUEST_URL;

            URL url = null;
            try {
                url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "UTF-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setConnectTimeout(2500);
                conn.setReadTimeout(2500);
                conn.setUseCaches(false);


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
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
            setPreferencesTask = null;
            if (success) {
                updateSharedPreferencesWithReceivedData();
                MainActivity activity = (MainActivity) getActivity();
                activity.updateDronesOnMap(null);
                clearListViews();
            } else {
                Toast.makeText(getContext(), "Nie udało się zapisać preferencji. Sprawdź połączenie z internetem", Toast.LENGTH_SHORT).show();
                clearListViews();
            }
        }

        @Override
        protected void onCancelled() {
            getPreferencesTask = null;
        }

        private void updateSharedPreferencesWithReceivedData() {
            sessionManager.setTrackedDrones(trackedDrones);
            sessionManager.setVisuazliedDrones(visualizedDrones);
        }

        private void clearListViews() {
            followedDroneCustomAdapter = new CustomListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), true);
            followedDroneListView.setAdapter(followedDroneCustomAdapter);
            trackedDronesCustomAdapter = new CustomListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            trackedDronesListView.setAdapter(trackedDronesCustomAdapter);
            visualizedDronesCustomAdapter = new CustomListViewAdapter(getContext(), new ArrayList<DBDrone>(), new ArrayList<DBDrone>(), false);
            visualizedDronesListView.setAdapter(visualizedDronesCustomAdapter);
            setListViewHeightBasedOnChildren(trackedDronesListView);
            setListViewHeightBasedOnChildren(visualizedDronesListView);
            setListViewHeightBasedOnChildren(followedDroneListView);
            trackedDronesCustomAdapter = null;
            visualizedDronesCustomAdapter = null;
            followedDroneCustomAdapter = null;
        }
    }
}
