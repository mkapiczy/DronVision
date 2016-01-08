package pl.mkapiczynski.dron.message;

import java.util.Date;

import domain.GeoPoint;

public class TrackerGeoDataMessage implements Message {
	private String messageType;
	private String deviceId;
	private String deviceType;
	private Date timestamp;
	private GeoPoint lastPosition;

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

	public GeoPoint getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(GeoPoint lastPosition) {
		this.lastPosition = lastPosition;
	}

	@Override
	public String toString() {
		return "GeoDataMessage [messageType=" + messageType + ", deviceId=" + deviceId + ", " + "deviceType="
				+ deviceType + ", " + "timestamp=" + timestamp + ", " + "lastPosition=" + lastPosition + "]";
	}

}
