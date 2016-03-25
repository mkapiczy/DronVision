package dron.mkapiczynski.pl.dronvision.websocket;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.json.Json;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;
import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.activity.MainActivity;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.message.ClientLoginMessage;
import dron.mkapiczynski.pl.dronvision.message.GeoDataMessage;
import dron.mkapiczynski.pl.dronvision.message.MessageDecoder;
import dron.mkapiczynski.pl.dronvision.message.SimulationMessage;

/**
 * Created by Miix on 2016-01-08.
 */
public class MyWebSocketConnection extends WebSocketConnection {
    private static final String TAG = MyWebSocketConnection.class.getSimpleName();
    private static final String SERVER = Parameters.getSERVER();
    private MainActivity activity;
    private ReestablishConnectionTask reestablishConnectionTask = null;
    private Button refreshConnectionButton;
    private String buttonState;
    private boolean shouldBeReconnecting = true;
    private boolean recentlyConnected = true;
    private WebSocketOptions webSocketOptions;
    private Date lastMessageDate;


    public MyWebSocketConnection(MainActivity activity) {
        super();
        this.activity = activity;
        webSocketOptions = new WebSocketOptions();
        webSocketOptions.setMaxFramePayloadSize(2000000);
        webSocketOptions.setMaxMessagePayloadSize(2000000);
    }

    public void connectToWebSocketServer() {
        try {
            connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    shouldBeReconnecting = true;
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
                        Date messageSentTime = geoMessage.getTimestamp();
                        if(lastMessageDate==null){
                            lastMessageDate = messageSentTime;
                            handleGeoMessage(geoMessage);
                        } else if(messageSentTime.after(lastMessageDate)){
                            handleGeoMessage(geoMessage);
                            lastMessageDate =messageSentTime;
                        }
                    } else if("SimulationEndedMessage".equals(messageType)){
                        handleSimulationEndedMessage();
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("WEBSOCKETS", "Connection closed Code:" + code + " Reason: " + reason);
                    if(recentlyConnected) {
                        setRefreshConnectionButtonState("DISCONNECTED");
                        if(shouldBeReconnecting) {
                            reestablishConnectionTask = new ReestablishConnectionTask();
                            reestablishConnectionTask.execute();
                        }
                        recentlyConnected = false;
                    }
                }

            }, webSocketOptions);
        } catch (WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    public void disconnectFromWebsocketServer() {
        if (isConnected()) {
            shouldBeReconnecting=false;
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

    public boolean sendSimulationMessageToServer(String task){
        SimulationMessage simulationMessage = new SimulationMessage();
        simulationMessage.setDeviceId(1l);
        simulationMessage.setTask(task);
        Gson gson = new Gson();
        if (isConnected()) {
            sendTextMessage(gson.toJson(simulationMessage));
            return true;
        } else{
            Log.i(TAG, "Coudn't send a message. No connection with server");
            return false;
        }
    }


    private void handleGeoMessage(GeoDataMessage geoMessage){
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
        drone.setDroneName(geoMessage.getDeviceName());
        drone.setCurrentPosition(currentDronePosition);
        drone.setSearchedArea(searchedArea);
        drone.setLastSearchedArea(lastSearchedArea);

        activity.updateDronesOnMap(drone);
    }

    private void handleSimulationEndedMessage(){
        activity.stopSimulation(true);
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

