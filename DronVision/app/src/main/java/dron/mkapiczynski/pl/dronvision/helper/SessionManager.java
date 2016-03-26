package dron.mkapiczynski.pl.dronvision.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Type;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.database.DBDrone;


public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";
    private static final String KEY_SERVER_HOST = "ServerAddress";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_LAST_MAP_CENTER = "lastMapCenter";
    private static final String KEY_FOLLOWED_DRONE = "followedDrone";
    private static final String KEY_ASSIGNED_DRONES = "assignedDrones";
    private static final String KEY_TRACKED_DRONES = "trackedDrones";
    private static final String KEY_VIZUALIZED_DRONES = "visualizedDrones";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setServerHost(String serverPort){
        String serverHost = "0.tcp.ngrok.io:" + serverPort;
        editor.putString(KEY_SERVER_HOST, serverHost);
        editor.commit();
    }

    public String getServerHost(){
        return pref.getString(KEY_SERVER_HOST,"");
    }

    public void setLastMapCenter(GeoPoint lastMapCenter) {
        if (lastMapCenter != null) {
            Gson gson = new Gson();
            String s = gson.toJson(lastMapCenter);
            editor.putString(KEY_LAST_MAP_CENTER, s);
            editor.commit();
        }
    }

    public GeoPoint getLastMapCenter() {
        Gson gson = new Gson();
        String lastMapCenterJsonString = pref.getString(KEY_LAST_MAP_CENTER, "");
        GeoPoint lastMapCenter = gson.fromJson(lastMapCenterJsonString, GeoPoint.class);
        return lastMapCenter;
    }

    public void setFollowedDrone(DBDrone followedDrone) {
        if (followedDrone != null) {
            Gson gson = new Gson();
            String s = gson.toJson(followedDrone);
            editor.putString(KEY_FOLLOWED_DRONE, s);
        } else {
            editor.putString(KEY_FOLLOWED_DRONE, "");
        }

        editor.commit();

    }

    public DBDrone getFollowedDrone() {
        Gson gson = new Gson();
        String followedDroneJsonString = pref.getString(KEY_FOLLOWED_DRONE, "");
        DBDrone stalkedDrone = gson.fromJson(followedDroneJsonString, DBDrone.class);
        return stalkedDrone;
    }

    public void setAssignedDrones(List<DBDrone> assignedDrones) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<DBDrone>>() {
        }.getType();
        String s = gson.toJson(assignedDrones, listType);
        editor.putString(KEY_ASSIGNED_DRONES, s);

        editor.commit();

    }

    public List<DBDrone> getAssignedDrones() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<DBDrone>>() {
        }.getType();
        String assignedDronesJsonString = pref.getString(KEY_ASSIGNED_DRONES, "");
        List<DBDrone> assignedDrones = gson.fromJson(assignedDronesJsonString, listType);
        return assignedDrones;
    }

    public void setTrackedDrones(List<DBDrone> trackedDrones) {
        Gson gson = new Gson();
        Type listOfTestObject = new TypeToken<List<DBDrone>>() {
        }.getType();
        String s = gson.toJson(trackedDrones, listOfTestObject);
        editor.putString(KEY_TRACKED_DRONES, s);

        editor.commit();

    }

    public List<DBDrone> getTrackedDrones() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<DBDrone>>() {
        }.getType();
        String trackedDronesJsonString = pref.getString(KEY_TRACKED_DRONES, "");
        List<DBDrone> trackedDrones = gson.fromJson(trackedDronesJsonString, listType);
        return trackedDrones;
    }

    public void setVisuazliedDrones(List<DBDrone> visuazliedDrones) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<DBDrone>>() {
        }.getType();
        String s = gson.toJson(visuazliedDrones, listType);
        editor.putString(KEY_VIZUALIZED_DRONES, s);

        editor.commit();

    }

    public List<DBDrone> getVisualizedDrones() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<DBDrone>>() {
        }.getType();
        String visualizedDronesJsonString = pref.getString(KEY_VIZUALIZED_DRONES, "");
        List<DBDrone> visualizedDrones = gson.fromJson(visualizedDronesJsonString, listType);
        return visualizedDrones;
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setLoggedUserLogin(String login) {
        editor.putString("login", login);

        editor.commit();
        Log.d(TAG, "User with login: " + login + " session data saved");
    }

    public String getLoggedUserLogin() {
        String login = pref.getString("login", "");

        return login;
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}