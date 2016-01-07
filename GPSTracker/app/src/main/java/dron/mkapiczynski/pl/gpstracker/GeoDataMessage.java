package dron.mkapiczynski.pl.gpstracker;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Created by Miix on 2015-12-14.
 */
public class GeoDataMessage {
    private static final String MESSAGE_TYPE="GeoDataMessage";
    private static final String DEVICE_TYPE="GPSTracker";
    private String messageType;
    private String deviceId;
    private String deviceType;
    private String latitude;
    private String longitude;
    private String altitude;
    private String timestamp;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toJson() {
        JsonObject jsonObject = Json.createObjectBuilder().add("messageType", MESSAGE_TYPE)
                .add("deviceId", deviceId)
                .add("deviceType", DEVICE_TYPE)
                .add("timestamp", timestamp)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("altitude", altitude).build();
        StringWriter stringWriter = new StringWriter();
        javax.json.JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.write(jsonObject);
        return stringWriter.toString();
    }
}
