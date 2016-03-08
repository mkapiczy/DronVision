package dron.mkapiczynski.pl.dronvision.message;

/**
 * Created by Miix on 2016-03-06.
 */
public class SimulationMessage {
    private static final String MESSAGE_TYPE = "SimulationMessage";

    private final String messageType = MESSAGE_TYPE;
    private Long deviceId;

    public SimulationMessage(){

    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "SimulationMessage [messageType=" + messageType + ", deviceId=" + deviceId + "]";
    }
}
