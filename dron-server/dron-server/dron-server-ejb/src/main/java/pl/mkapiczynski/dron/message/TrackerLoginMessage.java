package pl.mkapiczynski.dron.message;

public class TrackerLoginMessage implements Message {
	private String messageType;
	private String deviceId;

	public TrackerLoginMessage() {
	}

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

}
