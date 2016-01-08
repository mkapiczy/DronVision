package domain;

public class TrackerDevice {
	private Long loid;
	private String deviceId;
	private String deviceType;
	private String deviceName;
	private Boolean isOnline;

	public Long getLoid() {
		return loid;
	}

	public void setLoid(Long loid) {
		this.loid = loid;
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

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Boolean getLoggedIn() {
		return isOnline;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.isOnline = loggedIn;
	}

}
