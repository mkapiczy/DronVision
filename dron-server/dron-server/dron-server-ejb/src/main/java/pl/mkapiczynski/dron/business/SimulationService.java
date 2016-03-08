package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.message.Message;

public interface SimulationService {
	public void handleTrackerSimulationMessage(Message incomingMessage, final Set<Session> clientSessions);
}
