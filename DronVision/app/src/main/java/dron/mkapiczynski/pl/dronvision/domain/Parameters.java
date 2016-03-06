package dron.mkapiczynski.pl.dronvision.domain;

/**
 * Created by Miix on 2016-02-14.
 */
public class Parameters {
    public static final String SERVER_HOST =    "0.tcp.ngrok.io:12935";
    public static final String SERVER = "ws://" + SERVER_HOST + "/dron-server-web/server";
    public static final String LOGIN_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/login";
    public static final String PREFERENCES_REQUEST_URL = "http://" + SERVER_HOST + "/dron-server-web/preferences";

    public static final String CLIENT_LOGIN_MESSAGE_TYPE = "ClientLoginMessage";
}
