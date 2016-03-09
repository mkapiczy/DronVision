package pl.mkapiczynski.dron.message;
import pl.mkapiczynski.dron.message.Message;

public class EndSimulationMessage implements Message{
	private String messageType;
	private Long deviceId;

	public EndSimulationMessage() {
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
