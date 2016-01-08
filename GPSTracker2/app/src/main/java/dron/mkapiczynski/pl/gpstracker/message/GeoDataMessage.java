package dron.mkapiczynski.pl.gpstracker.message;


import java.util.Date;

import dron.mkapiczynski.pl.gpstracker.domain.GeoPoint;

/**
 * Created by Miix on 2015-12-14.
 */
public class GeoDataMessage {
    private static final String MESSAGE_TYPE = "GeoDataMessage";
    private static final String DEVICE_TYPE = "GPSTracker";
    private final String messageType = MESSAGE_TYPE;
    private String deviceId;
    private final String deviceType = DEVICE_TYPE;
    private Date timestamp;
    private GeoPoint lastPosition;

    public GeoDataMessage() {
    }

    public String getMessageType() {
        return messageType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public GeoPoint getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(GeoPoint lastPosition) {
        this.lastPosition = lastPosition;
    }

    @Override
    public String toString() {
        return "GeoDataMessage [messageType=" + MESSAGE_TYPE + ", deviceId=" + deviceId + ", " +
                "deviceType=" + DEVICE_TYPE + ", " + "timestamp=" + timestamp + ", " +
                "lastPosition=" + lastPosition + "]";
    }

}
