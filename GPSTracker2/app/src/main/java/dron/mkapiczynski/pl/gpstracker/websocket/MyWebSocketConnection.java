package dron.mkapiczynski.pl.gpstracker.websocket;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import dron.mkapiczynski.pl.gpstracker.R;
import dron.mkapiczynski.pl.gpstracker.domain.GeoPoint;
import dron.mkapiczynski.pl.gpstracker.jsonHelper.JsonDateSerializer;
import dron.mkapiczynski.pl.gpstracker.message.GeoDataMessage;
import dron.mkapiczynski.pl.gpstracker.message.TrackerLoginMessage;

/**
 * Created by Miix on 2016-01-08.
 */
public class MyWebSocketConnection extends WebSocketConnection {
    private static final String TAG = MyWebSocketConnection.class.getSimpleName();
    private static final String SERVER = "ws://0.tcp.ngrok.io:54211/dron-server-web/server";
    private Activity activity;
    private boolean deviceIsLoggedIn = false;

    public MyWebSocketConnection(Activity activity){
        super();
        this.activity = activity;
    }

    public void connectToWebSocketServer(){
        try {
            connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d("WEBSOCKETS", "Connected to server");
                    TextView serverTextView = (TextView) activity.findViewById(R.id.serverTextView);
                    serverTextView.setText("You are now connected to the server");
                    if(sendTrackerLoginMessage()){
                        deviceIsLoggedIn=true;
                    }
                }

                @Override
                public void onTextMessage(String message) {

                }

                @Override
                public void onClose(int code, String reason) {
                    TextView serverTextView = (TextView) activity.findViewById(R.id.serverTextView);
                    serverTextView.setText("Connection closed Code:" + code + " Reason: " + reason);
                    Log.d("WEBSOCKETS", "Connection closed Code:" + code + " Reason: " + reason);
                    deviceIsLoggedIn=false;
                }
            });
        } catch (WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendGeoDataMessageToServer(Location mLastLocation){
        GeoDataMessage geoDataMessage = new GeoDataMessage();
        geoDataMessage.setDeviceId("Device1");
        Date date = new Date();
        geoDataMessage.setTimestamp(date);
        GeoPoint position = new GeoPoint(mLastLocation.getLatitude(),mLastLocation.getLongitude(),mLastLocation.getAltitude());
        geoDataMessage.setLastPosition(position);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateSerializer());
        Gson gson = gsonBuilder.create();

        if (isConnected()) {
            sendTextMessage(gson.toJson(geoDataMessage));
        } else{
            Log.i(TAG, "Coudn't send a message. No connection with server");
        }
    }

    private boolean sendTrackerLoginMessage(){
        TrackerLoginMessage trackerLoginMessage = new TrackerLoginMessage();
        trackerLoginMessage.setDeviceId("Device1");
        Gson gson = new Gson();
        if (isConnected()) {
            sendTextMessage(gson.toJson(trackerLoginMessage));
            return true;
        } else{
            Log.i(TAG, "Coudn't send a message. No connection with server");
            return false;
        }
    }
}
