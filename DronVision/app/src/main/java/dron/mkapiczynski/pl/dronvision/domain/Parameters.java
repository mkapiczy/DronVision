package dron.mkapiczynski.pl.dronvision.domain;

import org.osmdroid.util.GeoPoint;

import dron.mkapiczynski.pl.dronvision.utils.SessionManager;

/**
 * Created by Miix on 2016-02-14.
 */
public class Parameters {

    public static final String CLIENT_LOGIN_MESSAGE_TYPE = "ClientLoginMessage";

    public static final Long SIMULATION_DRONE_ID = 4l;
    public static final GeoPoint SIMULATION_START_LOCATION = new GeoPoint(49.0744, 22.7263888888889);

    public static final String START_SIMULATION_MESSAGE_TASK = "startSimulation";
    public static final String END_SIMULATION_MESSAGE_TASK = "endSimulation";
    public static final String STOP_SIMULATION_MESSAGE_TASK = "stopSimulation";
    public static final String RERUN_SIMULATION_MESSAGE_TASK = "rerunSimulation";


    public static String getServerAddress(SessionManager sessionManager) {
        String serverHost = sessionManager.getServerHost();
        return  "ws://" + serverHost + "/dron-server-web/server";
    }

    public static String getLoginRequestUrl(SessionManager sessionManager) {
        String serverHost = sessionManager.getServerHost();
        return "http://" + serverHost + "/dron-server-web/login";
    }

    public static String getPreferencesRequestUrl(SessionManager sessionManager) {
        String serverHost = sessionManager.getServerHost();
        return "http://" + serverHost + "/dron-server-web/preferences";
    }

    public static String getHistoryGetSessionsRequestUrl(SessionManager sessionManager) {
        String serverHost = sessionManager.getServerHost();
        return "http://" + serverHost + "/dron-server-web/getDroneSessions";
    }

    public static String getHistoryGetSearchedAreaRequestUrl(SessionManager sessionManager) {
        String serverHost = sessionManager.getServerHost();
        return "http://" + serverHost + "/dron-server-web/getSearchedArea";
    }
}
