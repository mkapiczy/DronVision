package dron.mkapiczynski.pl.dronvision.message;

import java.util.Date;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.domain.HoleInSearchedArea;
import dron.mkapiczynski.pl.dronvision.domain.MyGeoPoint;


/**
 * Created by Miix on 2016-01-05.
 */
public class GeoDataMessage {
    private String messageType;
    private Long deviceId;
    private String deviceType;
    private String deviceName;
    private Date timestamp;
    private MyGeoPoint lastPosition;
    private List<MyGeoPoint> searchedArea;
    private List<MyGeoPoint> lastSearchedArea;
    private List<HoleInSearchedArea> searchedAreaHoles;
    private List<HoleInSearchedArea> lastSearchedAreaHoles;


    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public List<HoleInSearchedArea> getSearchedAreaHoles() {
        return searchedAreaHoles;
    }

    public void setSearchedAreaHoles(List<HoleInSearchedArea> searchedAreaHoles) {
        this.searchedAreaHoles = searchedAreaHoles;
    }

    public List<HoleInSearchedArea> getLastSearchedAreaHoles() {
        return lastSearchedAreaHoles;
    }

    public void setLastSearchedAreaHoles(List<HoleInSearchedArea> lastSearchedAreaHoles) {
        this.lastSearchedAreaHoles = lastSearchedAreaHoles;
    }

    @Override
    public String toString() {
        return "GeoDataMessage [messageType=" + messageType + ", deviceId=" + deviceId + ", " + "deviceType="
                + deviceType + ", " + "timestamp=" + timestamp + ", " + "lastPosition=" + lastPosition
                + ", searchedArea=" + searchedArea + "]";
    }
}
