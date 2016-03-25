package dron.mkapiczynski.pl.gpstracker.domain;

/**
 * Created by Miix on 2016-02-14.
 */
public class Parameters {
    private static String SERVER_HOST = "ws://0.tcp.ngrok.io:";
    private static String SERVER;

    public static void setInitialParametersValues(String ngrokPortFromFile) {
        SERVER_HOST = SERVER_HOST + ngrokPortFromFile;
        SERVER = SERVER_HOST + "/dron-server-web/server";
    }

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static String getSERVER() {
        return SERVER;
    }
}
