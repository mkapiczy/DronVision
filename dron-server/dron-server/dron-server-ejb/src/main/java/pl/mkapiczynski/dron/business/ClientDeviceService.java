package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

public interface ClientDeviceService {
	public void handleClientLoginMessage(Message incomingMessage, Session session, Set<Session> clientSessions); 
	public void sendGeoDataToAllSessionRegisteredClients(ClientGeoDataMessage geoMessage, Set<Session> clientSessions);
	public ClientGeoDataMessage generateClientGeoDataMessage(TrackerGeoDataMessage trackerGeoDataMessage);
}
