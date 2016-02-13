package dron.mkapiczynski.pl.dronvision.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

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
import java.util.Date;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.activity.MainActivity;
import dron.mkapiczynski.pl.dronvision.database.DBDrone;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.helper.CustomListViewAdapter;
import dron.mkapiczynski.pl.dronvision.helper.JsonDateSerializer;
import dron.mkapiczynski.pl.dronvision.helper.SessionManager;
import dron.mkapiczynski.pl.dronvision.message.MessageDecoder;
import dron.mkapiczynski.pl.dronvision.message.GetPreferencesMessage;
import dron.mkapiczynski.pl.dronvision.message.SetPreferencesMessage;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends Fragment {

    private final String PREFERENCES_URL = "http://0.tcp.ngrok.io:18721/dron-server-web/preferences";
    private ListView trackedDronesListView;
    private ListView visualizedDronesListView;
    private CustomListViewAdapter trackedDronesCustomAdapter;
    private CustomListViewAdapter visualizedDronesCustomAdapter;
    private List<DBDrone> assignedDrones;
    private List<DBDrone> trackedDrones;
    private List<DBDrone> visualizedDrones;

    ProgressDialog progress;

    private GetPreferencesTask getPreferencesTask = null;
    private SetPreferencesTask setPreferencesTask = null;

    private SessionManager sessionManager;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    private boolean created=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        trackedDronesListView = (ListView) view.findViewById(R.id.trackedDroneList);
        visualizedDronesListView = (ListView) view.findViewById(R.id.visualizedDroneList);
        created = true;

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
                progress = ProgressDialog.show(getActivity(), "Połączenie z serwerem",
                        "Pobieranie danych z serwera", true);
                getPreferencesTask = new GetPreferencesTask("Mix");
                getPreferencesTask.execute((Void) null);
        } else if (hidden ==true){
            if(created) {
                List<DBDrone> newTrackedDrones = getUpdatedDronesList(assignedDrones, trackedDronesCustomAdapter.getCheckedDrones());
                List<DBDrone> newVisualizedDrones = getUpdatedDronesList(assignedDrones, visualizedDronesCustomAdapter.getCheckedDrones());

                progress = ProgressDialog.show(getActivity(), "Połączenie z serwerem",
                        "Wysyłanie danych do serwera", true);
                setPreferencesTask = new SetPreferencesTask("Mix", newTrackedDrones, newVisualizedDrones, true, true);
                setPreferencesTask.execute((Void) null);
            }

        }
    }

    private List<DBDrone> getUpdatedDronesList(List<DBDrone> assignedDrones, List<DBDrone> checkedDrones){
        List<DBDrone> updatedDronesList = new ArrayList<>();
        for(int i=0; i<assignedDrones.size();i++){
            for(int j=0; j<checkedDrones.size();j++){
                if(assignedDrones.get(i).getDroneId() == checkedDrones.get(j).getDroneId()){
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
            String requestUrl = PREFERENCES_URL;
            try {
                requestUrl += "?login="+ URLEncoder.encode(login, "UTF-8");
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
                if(responseCode == HttpURLConnection.HTTP_OK){

                     BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    GetPreferencesMessage getPreferencesMessage = MessageDecoder.decodePreferencesMessage(sb.toString());
                    if(getPreferencesMessage !=null) {
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
            progress.dismiss();
            if (success) {
                trackedDronesCustomAdapter = new CustomListViewAdapter(getContext(), assignedDrones, trackedDrones);
                trackedDronesListView.setAdapter(trackedDronesCustomAdapter);
                visualizedDronesCustomAdapter = new CustomListViewAdapter(getContext(), assignedDrones, visualizedDrones);
                visualizedDronesListView.setAdapter(visualizedDronesCustomAdapter);
                setListViewHeightBasedOnChildren(trackedDronesListView);
                setListViewHeightBasedOnChildren(visualizedDronesListView);
                sessionManager.setAssignedDrones(assignedDrones);
                sessionManager.setTrackedDrones(trackedDrones);
                sessionManager.setVisuazliedDrones(visualizedDrones);
            } else {
                /*
                Wiadomość o braku dostępu do internetu
                 */
            }
        }

        @Override
        protected void onCancelled() {
            getPreferencesTask = null;
            progress.dismiss();
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
            this.trackedDronesChanged= trackedDronesChanged;
            this.visualizedDronesChanged = visualizedDronesChanged;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SetPreferencesMessage message = new SetPreferencesMessage();
            message.setLogin(login);
            if(trackedDronesChanged){
                message.setTrackedDronesChanged(true);
                message.setTrackedDrones(trackedDrones);
            }
            if(visualizedDronesChanged){
                message.setVisualizedDronesChanged(true);
                message.setVisualizedDrones(visualizedDrones);
            }

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String urlParameters = "message="+gson.toJson(message);
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            String requestUrl = PREFERENCES_URL;

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
                if(responseCode == HttpURLConnection.HTTP_OK){
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
            progress.dismiss();
            if (success) {
                sessionManager.setTrackedDrones(trackedDrones);
                sessionManager.setVisuazliedDrones(visualizedDrones);
                MainActivity activity = (MainActivity) getActivity();
                activity.updateDronesOnMap(null);
            } else {
                /*
                Wiadomość o braku dostępu do internetu
                 */
            }
        }

        @Override
        protected void onCancelled() {
            getPreferencesTask = null;
            progress.dismiss();
        }
    }
}
