package pl.mkapiczynski.dron.message;

import java.util.Date;

import pl.mkapiczynski.dron.domain.GeoPoint;

/**
 * Wiadomośc geolokalizacyjna (odbierana od aplikacji DronTracker)
 * 
 * @author Michal Kapiczynski
 *
 */
public class TrackerGeoDataMessage implements Message {
	private String messageType;
	private Long deviceId;
	private String deviceType;
	private Date timestamp;
	private GeoPoint lastPosition;

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
		return "GeoDataMessage [messageType=" + messageType + ", deviceId=" + deviceId.toString() + ", " + "deviceType="
				+ deviceType + ", " + "timestamp=" + timestamp + ", " + "lastPosition=" + lastPosition + "]";
	}

}
