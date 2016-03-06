package pl.mkapiczynski.dron.message;

public class TrackerSimulationMessage implements Message{
	private String messageType;
	private Long deviceId;

	public TrackerSimulationMessage() {
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
