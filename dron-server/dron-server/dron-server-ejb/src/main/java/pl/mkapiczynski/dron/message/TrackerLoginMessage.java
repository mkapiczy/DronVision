package pl.mkapiczynski.dron.message;

/**
 * Wiadomośc logująca aplikację DronTracker (odbierana od aplikacji DronTracker(
 * @author Michal Kapiczynski
 *
 */
public class TrackerLoginMessage implements Message {
	private String messageType;
	private Long deviceId;

	public TrackerLoginMessage() {
	}

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

}
