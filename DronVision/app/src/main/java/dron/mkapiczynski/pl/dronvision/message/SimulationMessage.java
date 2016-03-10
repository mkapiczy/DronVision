package dron.mkapiczynski.pl.dronvision.message;

/**
 * Created by Miix on 2016-03-06.
 */
public class SimulationMessage {
    private static final String MESSAGE_TYPE = "SimulationMessage";

    private final String messageType = MESSAGE_TYPE;
    private Long deviceId;
    private String task;

    public SimulationMessage(){

    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "SimulationMessage [messageType=" + messageType + ", deviceId=" + deviceId +", task=" +task + "]";
    }
}
