package pl.mkapiczynski.dron.response;

import java.util.List;

import pl.mkapiczynski.dron.domain.NDBDrone;
import pl.mkapiczynski.dron.domain.NDBDroneSession;

public class GetDroneSessionsResponse {
	private List<NDBDroneSession> droneSessions;

	public List<NDBDroneSession> getDroneSessions() {
		return droneSessions;
	}

	public void setDroneSessions(List<NDBDroneSession> droneSessions) {
		this.droneSessions = droneSessions;
	}

}
