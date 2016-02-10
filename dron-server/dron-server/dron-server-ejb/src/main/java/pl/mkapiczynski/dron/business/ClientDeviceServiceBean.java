package pl.mkapiczynski.dron.business;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

@Local
@Stateless(name = "ClientDeviceService")
public class ClientDeviceServiceBean implements ClientDeviceService {
	
	private static final Logger log = Logger.getLogger(ClientDeviceServiceBean.class);
	
	@Inject
	SearchedAreaService searchedAreaService;

	@Override
	public void handleClientLoginMessage(Message incomingMessage, Session session, Set<Session> clientSessions) {
		ClientLoginMessage clientLoginMessage = (ClientLoginMessage) incomingMessage;
		String deviceId = clientLoginMessage.getClientId();
		if (clientDeviceHasNotRegisteredSession(session, clientSessions)) {
			session.getUserProperties().put("deviceId", deviceId);
			clientSessions.add(session);
			System.out.println("New clientDevice with id: " + deviceId);
		} else{
			System.out.println("Client device with id: " + deviceId + " already registered");
		}
	}
	
	@Override
	public ClientGeoDataMessage generateClientGeoDataMessage(TrackerGeoDataMessage trackerGeoDataMessage) {
		ClientGeoDataMessage clientGeoDataMessage = new ClientGeoDataMessage();
		clientGeoDataMessage.setDeviceId(trackerGeoDataMessage.getDeviceId());
		clientGeoDataMessage.setDeviceType(trackerGeoDataMessage.getDeviceType());
		clientGeoDataMessage.setLastPosition(trackerGeoDataMessage.getLastPosition());
		clientGeoDataMessage.setTimestamp(new Date());
		clientGeoDataMessage.setSearchedArea(searchedAreaService.calculateSearchedArea(trackerGeoDataMessage.getLastPosition()));
		return clientGeoDataMessage;
	}

	@Override
	public void sendGeoDataToAllSessionRegisteredClients(ClientGeoDataMessage geoMessage, Set<Session> clientSessions) {
		Iterator<Session> iterator = clientSessions.iterator();
		while (iterator.hasNext()) {
			Session currentClient = iterator.next();
			try {
				currentClient.getBasicRemote().sendObject(geoMessage);
				log.info("Message send to client : " + currentClient.getUserProperties().get("deviceId"));
			} catch (IOException | EncodeException e) {
				log.error("Error occured while sendig message to client : "
						+ currentClient.getUserProperties().get("clientId") + " Error message : " + e);
			}
		}
	}


	
	private boolean clientDeviceHasNotRegisteredSession(Session session, Set<Session> clientSessions) {
		if (!clientSessions.contains(session)) {
			return true;
		} else {
			return false;
		}
	}

}
