package dron.mkapiczynski.pl.dronvision.message;

import java.util.Date;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.database.DBDrone;
import dron.mkapiczynski.pl.dronvision.domain.DroneSession;

/**
 * Created by Miix on 2016-02-12.
 */
public class GetDroneSessionsMessage {
    private List<DroneSession> droneSessions;

    public List<DroneSession> getDroneSessions() {
        return droneSessions;
    }

    public void setDroneSessions(List<DroneSession> droneSessions) {
        this.droneSessions = droneSessions;
    }
}
