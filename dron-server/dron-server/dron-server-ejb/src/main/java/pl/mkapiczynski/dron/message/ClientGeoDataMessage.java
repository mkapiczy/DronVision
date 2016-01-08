package pl.mkapiczynski.dron.message;

import java.util.Date;
import java.util.List;

import domain.GeoPoint;

public class ClientGeoDataMessage implements Message {
	private final String messageType = "ClientGeoDataMessage";
	private String deviceId;
	private String deviceType;
	private Date timestamp;
	private GeoPoint lastPosition;
	private List<GeoPoint> searchedArea;
	private List<GeoPoint> lastSearchedArea;

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

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
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

	public List<GeoPoint> getSearchedArea() {
		return searchedArea;
	}

	public void setSearchedArea(List<GeoPoint> searchedArea) {
		this.searchedArea = searchedArea;
	}

	public List<GeoPoint> getLastSearchedArea() {
		return lastSearchedArea;
	}

	public void setLastSearchedArea(List<GeoPoint> lastSearchedArea) {
		this.lastSearchedArea = lastSearchedArea;
	}

	@Override
	public String toString() {
		return "GeoDataMessage [messageType=" + messageType + ", deviceId=" + deviceId + ", " + "deviceType="
				+ deviceType + ", " + "timestamp=" + timestamp + ", " + "lastPosition=" + lastPosition
				+ ", searchedArea=" + searchedArea + "]";
	}

}
