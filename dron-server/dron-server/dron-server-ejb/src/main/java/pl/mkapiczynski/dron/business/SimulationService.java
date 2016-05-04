package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.message.Message;

/**
 * Interfejs do obsługi symulacji
 * 
 * @author Michal Kapiczynski
 *
 */
public interface SimulationService {
	public void handleSimulationMessage(Message incomingMessage, Session session, Set<Session> clientSessions);

}
