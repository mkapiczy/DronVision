package dron.mkapiczynski.pl.dronvision.message;

/**
 * Created by Miix on 2016-03-09.
 */
public class EndSimulationMessage {
    private static final String MESSAGE_TYPE = "EndSimulationMessage";

    private final String messageType = MESSAGE_TYPE;
    private Long deviceId;

    public EndSimulationMessage(){

    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "EndSimulationMessage [messageType=" + messageType + ", deviceId=" + deviceId + "]";
    }
}
