package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.message.Message;

public interface SimulationService {
	public void handleSimulationMessage(Message incomingMessage, final Session session, final Set<Session> clientSessions);
	public void handleEndSimulationMessage(Message incomingMessage, Session session);
}
