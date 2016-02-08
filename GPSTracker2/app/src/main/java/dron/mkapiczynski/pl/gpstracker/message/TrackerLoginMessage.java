package dron.mkapiczynski.pl.gpstracker.message;

/**
 * Created by Miix on 2016-01-08.
 */
public class TrackerLoginMessage {
    private static final String MESSAGE_TYPE = "TrackerLoginMessage";

    private final String messageType = MESSAGE_TYPE;
    private Long deviceId;

    public TrackerLoginMessage(){

    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "TrackerLoginMessage [messageType=" + messageType + ", deviceId=" + deviceId + "]";
    }
}
