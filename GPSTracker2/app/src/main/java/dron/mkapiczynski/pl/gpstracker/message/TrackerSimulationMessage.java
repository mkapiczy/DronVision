package dron.mkapiczynski.pl.gpstracker.message;

/**
 * Created by Miix on 2016-03-06.
 */
public class TrackerSimulationMessage {
    private static final String MESSAGE_TYPE = "TrackerSimulationMessage";

    private final String messageType = MESSAGE_TYPE;
    private Long deviceId;

    public TrackerSimulationMessage(){

    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "TrackerSimulationMessage [messageType=" + messageType + ", deviceId=" + deviceId + "]";
    }
}
