package dron.mkapiczynski.pl.gpsvisualiser.message;

import java.io.StringReader;

import javax.json.Json;

/**
 * Created by Miix on 2016-01-05.
 */
public class GeoDataMessage {
    private String messageType;
    private String deviceId;
    private String deviceType;
    private String timestamp;
    private String latitude;
    private String longitude;
    private String altitude;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public void decodeGeoDataMessage(String jsonMessage){
        this.deviceId = (Json.createReader(new StringReader(jsonMessage)).readObject().getString("deviceId"));
        this.deviceType = (Json.createReader(new StringReader(jsonMessage)).readObject().getString("deviceType"));
        this.timestamp = (Json.createReader(new StringReader(jsonMessage)).readObject().getString("timestamp"));
        this.latitude = (Json.createReader(new StringReader(jsonMessage)).readObject().getString("latitude"));
        this.longitude = (Json.createReader(new StringReader(jsonMessage)).readObject().getString("longitude"));
        this.altitude =(Json.createReader(new StringReader(jsonMessage)).readObject().getString("altitude"));
    }
}
