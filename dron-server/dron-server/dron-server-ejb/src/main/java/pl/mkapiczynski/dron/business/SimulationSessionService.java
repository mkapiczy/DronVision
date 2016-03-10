package pl.mkapiczynski.dron.business;

import java.util.Iterator;
import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.database.SimulationSession;

public interface SimulationSessionService {
	public void setLastSimulationId(Long lastSimulationId, Long clientId);
	public SimulationSession createNewSimulationSession(Long clientId);
	public void deleteSimulationSession(Long clientId);
	public void deleteAllSimulationSessionsForClient(Long clientId);
	public SimulationSession getSimulationSessionForClient(Long clientId);
	public void updateSimulationData(Drone drone, Set<Session> clientSessions);
}
