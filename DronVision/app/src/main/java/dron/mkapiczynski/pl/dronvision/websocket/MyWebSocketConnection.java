package dron.mkapiczynski.pl.dronvision.websocket;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.activity.MainActivity;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.message.ClientLoginMessage;
import dron.mkapiczynski.pl.dronvision.message.GeoDataMessage;
import dron.mkapiczynski.pl.dronvision.message.MessageDecoder;

/**
 * Created by Miix on 2016-01-08.
 */
public class MyWebSocketConnection extends WebSocketConnection {
    private static final String TAG = MyWebSocketConnection.class.getSimpleName();
    private static final String SERVER = Parameters.SERVER;
    private MainActivity activity;
    private ReestablishConnectionTask reestablishConnectionTask = null;
    private Button refreshConnectionButton;
    private String buttonState;
    private boolean recentlyConnected = true;


    public MyWebSocketConnection(MainActivity activity) {
        super();
        this.activity = activity;
    }

    public void connectToWebSocketServer() {
        try {
            connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d("WEBSOCKETS", "Connected to server");
                    sendLoginMessage();

                    setRefreshConnectionButtonState("CONNECTED");

                    if(reestablishConnectionTask!=null){
                        reestablishConnectionTask.cancel(true);
                        reestablishConnectionTask = null;
                    }

                    recentlyConnected = true;
                }

                @Override
                public void onTextMessage(String jsonMessage) {
                    String messageType = Json.createReader(new StringReader(jsonMessage)).readObject().getString("messageType");
                    if ("ClientGeoDataMessage".equals(messageType)) {
                        GeoDataMessage geoMessage = MessageDecoder.decodeGeoDataMessage(jsonMessage);
                        MyGeoPoint point = geoMessage.getLastPosition();
                        GeoPoint currentDronePosition = new GeoPoint(point.getLatitude(), point.getLongitude(), point.getAltitude());
                        List<GeoPoint> searchedArea = new ArrayList<>();
                        for (int i = 0; i < geoMessage.getSearchedArea().size(); i++) {
                            searchedArea.add(new GeoPoint(geoMessage.getSearchedArea().get(i).getLatitude(), geoMessage.getSearchedArea().get(i).getLongitude()));
                        }
                        List<GeoPoint> lastSearchedArea = new ArrayList<>();
                        for (int i = 0; i < geoMessage.getLastSearchedArea().size(); i++) {
                            lastSearchedArea.add(new GeoPoint(geoMessage.getLastSearchedArea().get(i).getLatitude(), geoMessage.getLastSearchedArea().get(i).getLongitude()));
                        }

                        Drone drone = new Drone();
                        drone.setDroneId(geoMessage.getDeviceId());
                        drone.setCurrentPosition(currentDronePosition);
                        drone.setSearchedArea(searchedArea);
                        drone.setLastSearchedArea(lastSearchedArea);

                        activity.updateDronesOnMap(drone);
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("WEBSOCKETS", "Connection closed Code:" + code + " Reason: " + reason);
                    if(recentlyConnected) {
                        setRefreshConnectionButtonState("DISCONNECTED");
                        reestablishConnectionTask = new ReestablishConnectionTask();
                        reestablishConnectionTask.execute();
                        recentlyConnected = false;
                    }
                }

            });
        } catch (WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public void disconnectFromWebsocketServer() {
        if (isConnected()) {
            disconnect();
        }
    }

    private void sendLoginMessage() {
        ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
        clientLoginMessage.setClientId(1l);
        Gson gson = new Gson();
        if (isConnected()) {
            sendTextMessage(gson.toJson(clientLoginMessage));
        } else {
            Log.i(TAG, "Coudn't send a login message. No connection with server");
        }
    }

    private void setRefreshConnectionButtonState(String state){
        if("CONNECTED".equals(state)){
            refreshConnectionButton = (Button)activity.findViewById(R.id.websocketConnectionStateButton);
            if(refreshConnectionButton !=null) {
                refreshConnectionButton.setBackgroundColor(Color.parseColor("#9609A709"));
                refreshConnectionButton.setTextColor(Color.BLACK);
                refreshConnectionButton.setText(" Połączony ");
                refreshConnectionButton.setEnabled(false);
            }
        } else if ("DISCONNECTED".equals(state)){
            refreshConnectionButton = (Button)activity.findViewById(R.id.websocketConnectionStateButton);
            if(refreshConnectionButton !=null) {
                refreshConnectionButton.setBackgroundColor(Color.parseColor("#96CC0725"));
                refreshConnectionButton.setTextColor(Color.BLACK);
                refreshConnectionButton.setText(" Sprawdź połączenie z internetem ");
                refreshConnectionButton.setEnabled(false);
            }
        }
    }

    public class ReestablishConnectionTask extends AsyncTask<Void, Void, Boolean> {

        ReestablishConnectionTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (!isConnected()) {
                connectToWebSocketServer();
                try {
                    Thread.sleep(1000*2);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Exception " + e);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            reestablishConnectionTask = null;
            if(!isConnected()){
                new ReestablishConnectionTask().execute();
            }
        }

        @Override
        protected void onCancelled() {
            reestablishConnectionTask = null;
        }
    }
}

