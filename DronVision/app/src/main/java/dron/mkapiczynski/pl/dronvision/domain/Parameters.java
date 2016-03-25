package dron.mkapiczynski.pl.dronvision.domain;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Miix on 2016-02-14.
 */
public class Parameters {
    private static String SERVER_HOST = "0.tcp.ngrok.io:";
    private static String SERVER;
    private static String LOGIN_REQUEST_URL;
    private static String PREFERENCES_REQUEST_URL;
    private static String HISTORY_GET_SESSIONS_REQUEST_URL;
    private static String HISTORY_GET_SEARCHED_AREA_REQUEST_URL;

    public static final String CLIENT_LOGIN_MESSAGE_TYPE = "ClientLoginMessage";

    public static final Long SIMULATION_DRONE_ID = 4l;
    public static final GeoPoint SIMULATION_START_LOCATION = new GeoPoint(49.0744, 22.7263888888889);

    public static final String START_SIMULATION_MESSAGE_TASK = "startSimulation";
    public static final String END_SIMULATION_MESSAGE_TASK = "endSimulation";
    public static final String STOP_SIMULATION_MESSAGE_TASK = "stopSimulation";
    public static final String RERUN_SIMULATION_MESSAGE_TASK = "rerunSimulation";

    public static void setInitializationParameters(String hostPortFromFile) {
        SERVER_HOST += hostPortFromFile;
        SERVER = "ws://" + SERVER_HOST + "/dron-server-web/server";
        LOGIN_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/login";
        PREFERENCES_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/preferences";
        HISTORY_GET_SESSIONS_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/getDroneSessions";
        HISTORY_GET_SEARCHED_AREA_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/getSearchedArea";
        ;
    }

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static String getSERVER() {
        return SERVER;
    }

    public static String getLoginRequestUrl() {
        return LOGIN_REQUEST_URL;
    }

    public static String getPreferencesRequestUrl() {
        return PREFERENCES_REQUEST_URL;
    }

    public static String getHistoryGetSessionsRequestUrl() {
        return HISTORY_GET_SESSIONS_REQUEST_URL;
    }

    public static String getHistoryGetSearchedAreaRequestUrl() {
        return HISTORY_GET_SEARCHED_AREA_REQUEST_URL;
    }
}
