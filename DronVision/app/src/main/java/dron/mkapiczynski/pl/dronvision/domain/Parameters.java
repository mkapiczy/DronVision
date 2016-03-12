package dron.mkapiczynski.pl.dronvision.domain;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Miix on 2016-02-14.
 */
public class Parameters {
    public static final String SERVER_HOST =    "0.tcp.ngrok.io:16744";
    public static final String SERVER = "ws://" + SERVER_HOST + "/dron-server-web/server";
    public static final String LOGIN_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/login";
    public static final String PREFERENCES_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/preferences";
    public static final String HISTORY_GET_SESSIONS_REQUEST_URL =  "http://" + SERVER_HOST + "/dron-server-web/getDroneSessions";
    public static final String HISTORY_GET_SEARCHED_AREA_REQUEST_URL =  "http://" + SERVER_HOST + "/dron-server-web/getSearchedArea";

    public static final String CLIENT_LOGIN_MESSAGE_TYPE = "ClientLoginMessage";

    public static final Long SIMULATION_DRONE_ID = 4l;
    public static final GeoPoint SIMULATION_START_LOCATION = new GeoPoint(49.0744,22.7263888888889);

    public static final String START_SIMULATION_MESSAGE_TASK = "startSimulation";
    public static final String END_SIMULATION_MESSAGE_TASK = "endSimulation";
    public static final String STOP_SIMULATION_MESSAGE_TASK = "stopSimulation";
    public static final String RERUN_SIMULATION_MESSAGE_TASK = "rerunSimulation";

}
