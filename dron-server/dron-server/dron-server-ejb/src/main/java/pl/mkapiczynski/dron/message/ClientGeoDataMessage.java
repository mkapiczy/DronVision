package pl.mkapiczynski.dron.message;

import java.util.Date;
import java.util.List;

import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.domain.NDBHoleInSearchedArea;

/**
 * Wiadomość wysyłana do aplikacji DronVision informująca o zmianie położenia i nowym obszarze przeszukanym
 * @author Michal Kapiczynski
 *
 */
public class ClientGeoDataMessage implements Message {
	private final String messageType = "ClientGeoDataMessage";
	private Long deviceId;
	private String deviceType;
	private String deviceName;
	private Date timestamp;
	private GeoPoint lastPosition;
	private List<GeoPoint> searchedArea;
	private List<GeoPoint> lastSearchedArea;
	private List<NDBHoleInSearchedArea> searchedAreaHoles;
	private List<NDBHoleInSearchedArea> lastSearchedAreaHoles;

	public String getMessageType() {
		return messageType;
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

	public List<NDBHoleInSearchedArea> getSearchedAreaHoles() {
		return searchedAreaHoles;
	}

	public void setSearchedAreaHoles(List<NDBHoleInSearchedArea> searchedAreaHoles) {
		this.searchedAreaHoles = searchedAreaHoles;
	}

	public List<NDBHoleInSearchedArea> getLastSearchedAreaHoles() {
		return lastSearchedAreaHoles;
	}

	public void setLastSearchedAreaHoles(List<NDBHoleInSearchedArea> lastSearchedAreaHoles) {
		this.lastSearchedAreaHoles = lastSearchedAreaHoles;
	}

	@Override
	public String toString() {
		return "GeoDataMessage [messageType=" + messageType + ", deviceId=" + deviceId + ", " + "deviceType="
				+ deviceType + ", " + "timestamp=" + timestamp + ", " + "lastPosition=" + lastPosition
				+ ", searchedArea=" + searchedArea + "]";
	}

}
