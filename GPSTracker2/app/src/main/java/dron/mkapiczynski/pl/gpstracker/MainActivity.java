package dron.mkapiczynski.pl.gpstracker;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Date;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class MainActivity extends Activity implements LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // GPS
    private LocationManager locationManager;
    private LocationProvider gpsSignalProvider;
    private Location mLastLocation;

    // Flags
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean requestLocationUpdatesFlag = false;

    // Gps parameters
    private static int UPDATE_INTERVAL = 3000; // 3 sec
    private static int FASTEST_INTERVAL = 1500; // 1,5sec
    private static int DISPLACEMENT = 1; // 1 meters

    // UI
    private TextView locationTextView;
    private TextView serverTextView;
    private Button btnShowLocation, btnStartLocationUpdates;

    // Websocket
    private static final String SERVER = "ws://0.tcp.ngrok.io:45960/dron-server-web/server";
    private final WebSocketConnection client = new WebSocketConnection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = (TextView) findViewById(R.id.lblLocation);
        serverTextView = (TextView) findViewById(R.id.serverTextView);
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnStartLocationUpdates = (Button) findViewById(R.id.btnLocationUpdates);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsSignalProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);

        togglePeriodicLocationUpdates();

        connectToWebSocketServer();

        // Show location button click listener
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLastLocation = getLastLocation();
                displayLocationOnUI();

                if(mLastLocation!=null) {
                    sendGeoDataMessageToServer();
                }
            }
        });

        btnStartLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePeriodicLocationUpdates();
            }
        });
    }

    private void connectToWebSocketServer(){
        try {
            client.connect(SERVER, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d("WEBSOCKETS", "Connected to server");
                    serverTextView.setText("You are now connected to the server");
                }

                @Override
                public void onTextMessage(String message) {

                }

                @Override
                public void onClose(int code, String reason) {
                    serverTextView.setText("Connection closed Code:" + code + " Reason: " + reason);
                    Log.d("WEBSOCKETS", "Connection closed Code:" + code + " Reason: " + reason);

                }
            });
        } catch (WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendGeoDataMessageToServer(){
        GeoDataMessage geoDataMessage = new GeoDataMessage();
        geoDataMessage.setDeviceId("Device2");
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        geoDataMessage.setTimestamp(dateFormat.format(date.getTime()));
        geoDataMessage.setLatitude(String.valueOf(mLastLocation.getLatitude()));
        geoDataMessage.setLongitude(String.valueOf(mLastLocation.getLongitude()));
        geoDataMessage.setAltitude(String.valueOf(mLastLocation.getAltitude()));
        if (client.isConnected()) {
            client.sendTextMessage(geoDataMessage.toJson());
        }
    }

    private Location getLastLocation() {
        Location updatedLocation = new Location(gpsSignalProvider.getName());
        try {
            updatedLocation = locationManager.getLastKnownLocation(gpsSignalProvider.getName());
        } catch (SecurityException e) {
            Log.e(TAG, "Exception reading last known location " + e.toString());
        }
        return updatedLocation;
    }

    private void displayLocationOnUI() {
        if (mLastLocation != null) {
            locationTextView.setText(mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude() + ", " + mLastLocation.getAltitude());
        } else {
            locationTextView.setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    private void togglePeriodicLocationUpdates(){
        if(!requestLocationUpdatesFlag){
            btnStartLocationUpdates
                    .setText(getString(R.string.btn_stop_location_updates));

            requestLocationUpdatesFlag = true;

            try {
                locationManager.requestLocationUpdates(gpsSignalProvider.getName(), 500, 0, this);
            } catch (SecurityException e) {
                Log.e("GPS", "Exception setting request Location Updates properties of LocationManager object " + e.toString());
            }

            Log.d(TAG, "Periodic location updates started!");
        } else{
            // Changing the button text
            btnStartLocationUpdates
                    .setText(getString(R.string.btn_start_location_updates));

            requestLocationUpdatesFlag = false;

            // Stopping the location updates
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e){
                Log.e("GPS", "Exception removing Location Updates of LocationManager object " + e.toString());
            }

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            Toast.makeText(getApplicationContext(), "GPS wyłączony. Sprawdź ustawienia", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            Toast.makeText(getApplicationContext(), "Dostawca internetu odłączony. Sprawdź ustawienia", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
        }
    }
    @Override
    public void onLocationChanged(Location location) {

        if(!client.isConnected() || client==null){
            connectToWebSocketServer();
        }

        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocationOnUI();

        sendGeoDataMessageToServer();
    }


}
