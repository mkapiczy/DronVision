package dron.mkapiczynski.pl.gpsvisualiser.message;

import org.osmdroid.util.GeoPoint;

import java.util.Date;
import java.util.List;

import dron.mkapiczynski.pl.gpsvisualiser.domain.MyGeoPoint;

/**
 * Created by Miix on 2016-01-05.
 */
public class GeoDataMessage {
    private String messageType;
    private String deviceId;
    private String deviceType;
    private Date timestamp;
    private MyGeoPoint lastPosition;
    private List<MyGeoPoint> searchedArea;
    private List<MyGeoPoint> lastSearchedArea;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public MyGeoPoint getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(MyGeoPoint lastPosition) {
        this.lastPosition = lastPosition;
    }

    public List<MyGeoPoint> getSearchedArea() {
        return searchedArea;
    }

    public void setSearchedArea(List<MyGeoPoint> searchedArea) {
        this.searchedArea = searchedArea;
    }

    public List<MyGeoPoint> getLastSearchedArea() {
        return lastSearchedArea;
    }

    public void setLastSearchedArea(List<MyGeoPoint> lastSearchedArea) {
        this.lastSearchedArea = lastSearchedArea;
    }

    @Override
    public String toString() {
        return "GeoDataMessage [messageType=" + messageType + ", deviceId=" + deviceId + ", " + "deviceType="
                + deviceType + ", " + "timestamp=" + timestamp + ", " + "lastPosition=" + lastPosition
                + ", searchedArea=" + searchedArea + "]";
    }
}
