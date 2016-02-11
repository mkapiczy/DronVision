package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.Message;

public interface ClientDeviceService {
	public void handleClientLoginMessage(Message incomingMessage, Session session, Set<Session> clientSessions); 
	public void sendGeoDataToAllSessionRegisteredClients(Drone drone, Set<Session> clientSessions);

}
