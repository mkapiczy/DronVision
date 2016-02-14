package dron.mkapiczynski.pl.dronvision.websocket;

import android.util.Log;
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
import dron.mkapiczynski.pl.dronvision.activity.MainActivity;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.message.MessageDecoder;
import dron.mkapiczynski.pl.dronvision.domain.Drone;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;
import dron.mkapiczynski.pl.dronvision.message.ClientLoginMessage;
import dron.mkapiczynski.pl.dronvision.message.GeoDataMessage;

/**
 * Created by Miix on 2016-01-08.
 */
public class MyWebSocketConnection extends WebSocketConnection {
    private static final String TAG = MyWebSocketConnection.class.getSimpleName();
    private static final String SERVER = Parameters.SERVER;
    private MainActivity activity;
    private boolean deviceIsLoggedIn = false;

    public MyWebSocketConnection(MainActivity activity){
        super();
        this.activity = activity;
    }

    public void connectToWebSocketServer() {
        try {
            connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d("WEBSOCKETS", "Connected to server");
                    if(sendLoginMessage()){
                        deviceIsLoggedIn=true;
                    }
                    Toast.makeText(activity.getApplicationContext(), "You are now connected to the server", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTextMessage(String jsonMessage) {
                    String messageType = Json.createReader(new StringReader(jsonMessage)).readObject().getString("messageType");
                    if ("ClientGeoDataMessage".equals(messageType)) {
                        GeoDataMessage geoMessage = MessageDecoder.decodeGeoDataMessage(jsonMessage);
                        MyGeoPoint point = geoMessage.getLastPosition();
                        GeoPoint currentDronePosition = new GeoPoint(point.getLatitude(), point.getLongitude(),point.getAltitude());
                        List<GeoPoint> searchedArea = new ArrayList<>();
                        for(int i=0; i<geoMessage.getSearchedArea().size();i++){
                            searchedArea.add(new GeoPoint(geoMessage.getSearchedArea().get(i).getLatitude(), geoMessage.getSearchedArea().get(i).getLongitude()));
                        }
                        List<GeoPoint> lastSearchedArea = new ArrayList<>();
                        for(int i=0; i<geoMessage.getLastSearchedArea().size();i++){
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
                    Toast.makeText(activity.getApplicationContext(), "Connection closed Code:" + code + " Reason: " + reason, Toast.LENGTH_LONG).show();
                    Log.d("WEBSOCKETS", "Connection closed Code:" + code + " Reason: " + reason);
                    deviceIsLoggedIn=false;
                    //startReestablishingConnectionScheduler();
                }
            });
        } catch (WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void disconnectFromWebsocketServer(){
        if(isConnected()){
            disconnect();
        }
    }
    private boolean sendLoginMessage() {
        ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
        clientLoginMessage.setClientId(1l);
        Gson gson = new Gson();
        if (isConnected()) {
            sendTextMessage(gson.toJson(clientLoginMessage));
            return true;
        } else{
            Log.i(TAG, "Coudn't send a login message. No connection with server");
            return false;
        }
    }

}
