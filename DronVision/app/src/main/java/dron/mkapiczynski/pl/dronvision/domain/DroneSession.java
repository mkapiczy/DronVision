package dron.mkapiczynski.pl.dronvision.domain;

import java.util.Date;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.database.DBDrone;

/**
 * Created by Miix on 2016-03-11.
 */
public class DroneSession {
    private Long sessionId;
    private Long droneId;
    private String droneName;
    private Date sessionStarted;
    private Date sessionEnded;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getDroneId() {
        return droneId;
    }

    public void setDroneId(Long droneId) {
        this.droneId = droneId;
    }

    public String getDroneName() {
        return droneName;
    }

    public void setDroneName(String droneName) {
        this.droneName = droneName;
    }

    public Date getSessionStarted() {
        return sessionStarted;
    }

    public void setSessionStarted(Date sessionStarted) {
        this.sessionStarted = sessionStarted;
    }

    public Date getSessionEnded() {
        return sessionEnded;
    }

    public void setSessionEnded(Date sessionEnded) {
        this.sessionEnded = sessionEnded;
    }
}
