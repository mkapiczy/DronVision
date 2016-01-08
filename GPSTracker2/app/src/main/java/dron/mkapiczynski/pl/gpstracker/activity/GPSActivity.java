package dron.mkapiczynski.pl.gpstracker.activity;

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

import dron.mkapiczynski.pl.gpstracker.websocket.MyWebSocketConnection;
import dron.mkapiczynski.pl.gpstracker.R;


public class GPSActivity extends Activity implements LocationListener {

    private static final String TAG = GPSActivity.class.getSimpleName();

    // GPS
    private LocationManager locationManager;
    private LocationProvider gpsSignalProvider;
    private Location mLastLocation;

    // Flags

    private boolean requestLocationUpdatesFlag = false;

    // Gps parameters
    private static int UPDATE_INTERVAL = 1500; // 5 sec
    private static int DISPLACEMENT = 3; // 3 meters

    // UI
    private TextView locationTextView;
    private TextView serverTextView;
    private Button btnShowLocation, btnStartLocationUpdates;

    // Websocket
    private static final String SERVER = "ws://0.tcp.ngrok.io:47929/dron-server-web/server";
    private final MyWebSocketConnection client = new MyWebSocketConnection(this);


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

        client.connectToWebSocketServer();

        // Show location button click listener
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLastLocation = getLastLocation();
                displayLocationOnUI();

                if (!client.isConnected() || client == null) {
                    client.connectToWebSocketServer();
                }

                if (mLastLocation != null) {
                    client.sendGeoDataMessageToServer(mLastLocation);
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

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

        if (!client.isConnected() || client == null) {
            client.connectToWebSocketServer();
        }

        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocationOnUI();

        client.sendGeoDataMessageToServer(mLastLocation);
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

    private void togglePeriodicLocationUpdates() {
        if (!requestLocationUpdatesFlag) {
            startLocationUpdates();
        } else {
            stopLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(gpsSignalProvider.getName(), UPDATE_INTERVAL, DISPLACEMENT, this);
            requestLocationUpdatesFlag = true;
            btnStartLocationUpdates.setText(getString(R.string.btn_stop_location_updates));
        } catch (SecurityException e) {
            Log.e("GPS", "Exception setting request Location Updates properties of LocationManager object " + e.toString());
        }
        Log.d(TAG, "Periodic location updates started!");
    }

    private void stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this);
            requestLocationUpdatesFlag = false;
            btnStartLocationUpdates.setText(getString(R.string.btn_start_location_updates));
        } catch (SecurityException e) {
            Log.e("GPS", "Exception removing Location Updates of LocationManager object " + e.toString());
        }
        Log.d(TAG, "Periodic location updates stopped!");
    }


}
