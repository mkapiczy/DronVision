package pl.mkapiczynski.dron.serverendpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.business.ClientGeoDataMessageService;
import pl.mkapiczynski.dron.business.GPSTrackerService;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.MessageDecoder;
import pl.mkapiczynski.dron.message.MessageEncoder;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerLoginMessage;

@javax.websocket.server.ServerEndpoint(value = "/server", encoders = { MessageEncoder.class }, decoders = {
		MessageDecoder.class, })
public class ServerEndpoint {
	private static final Logger log = Logger.getLogger(ServerEndpoint.class);

	@Inject
	private ClientGeoDataMessageService clientGeoDataMessageService;
	
	@Inject
	private GPSTrackerService gpsTrackerService;

	public static Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());
	public static Set<Session> gpsTrackerDeviceSessions = Collections.synchronizedSet(new HashSet<Session>());
	public static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<Session>());

	@OnOpen
	public void handleOpen(Session session) throws IOException, EncodeException {
		allSessions.add(session);
	}

	@OnMessage
	public void handleMessage(Message incomingMessage, Session session) throws IOException, EncodeException {
		if (incomingMessage instanceof TrackerLoginMessage) {
			handleTrackerLoginMessage(incomingMessage, session);
		} else if (incomingMessage instanceof ClientLoginMessage) {
			handleClientLoginMessage(incomingMessage, session);
		} else if (incomingMessage instanceof TrackerGeoDataMessage) {
			handleTrackerGeoDataMessage(incomingMessage, session);
		}
	}

	@OnClose
	public void handleClose(Session closingSession) throws IOException, EncodeException {
		if (clientSessions.contains(closingSession)) {
			clientSessions.remove(closingSession);
			log.info("Client : " + closingSession.getUserProperties().get("deviceId") + " disconnected");
		} else if (gpsTrackerDeviceSessions.contains(closingSession)) {
			Long droneId = (long) closingSession.getUserProperties().get("deviceId");
			gpsTrackerService.closeDroneSession(droneId);
			gpsTrackerDeviceSessions.remove(closingSession);
			log.info("Tracker device : " + closingSession.getUserProperties().get("deviceId") + " disconnected");
		}
		allSessions.remove(closingSession);
	}

	@OnError
	public void handleError(Throwable t) {
		log.error("Error has occured : " + t.getMessage().toString());
	}

	private void handleClientLoginMessage(Message incomingMessage, Session session) {
		ClientLoginMessage clientLoginMessage = (ClientLoginMessage) incomingMessage;
		if (clientDeviceHasNotRegisteredSession(session)) {
			session.getUserProperties().put("deviceId", clientLoginMessage.getClientId());
			clientSessions.add(session);
		}
		System.out.println("New clientDevice : " + clientLoginMessage.getClientId());
	}

	private void handleTrackerLoginMessage(Message incomingMessage, Session session) {
		TrackerLoginMessage trackerLoginMessage = (TrackerLoginMessage) incomingMessage;
		if (geoDeviceHasNotRegisteredSession(session)) {
			Long droneId = trackerLoginMessage.getDeviceId();
			if(gpsTrackerService.createNewDroneSession(droneId)){
				session.getUserProperties().put("deviceId", droneId);
				gpsTrackerDeviceSessions.add(session);
			} else{
				log.info("Login message from unregistered tracker device: " + droneId);
			}
			
		}
		System.out.println("New trackerDevice : " + trackerLoginMessage.getDeviceId());
	}

	private void handleTrackerGeoDataMessage(Message incomingMessage, Session session) {
		if (gpsTrackerDeviceSessions.contains(session)) {
			TrackerGeoDataMessage trackerGeoDataMessage = (TrackerGeoDataMessage) incomingMessage;
			Long droneId = trackerGeoDataMessage.getDeviceId();
			Location newSearchedLocation = new Location();
			newSearchedLocation.setLatitude(trackerGeoDataMessage.getLastPosition().getLatitude());
			newSearchedLocation.setLongitude(trackerGeoDataMessage.getLastPosition().getLongitude());
			newSearchedLocation.setAltitude(trackerGeoDataMessage.getLastPosition().getAltitude());
			gpsTrackerService.updateDroneSearchedArea(droneId, newSearchedLocation);
			
			ClientGeoDataMessage clientGeoMessage = clientGeoDataMessageService
					.generateClientGeoDataMessage(trackerGeoDataMessage);
			sendGeoDataToAllSessionRegisteredClients(clientGeoMessage);
		} else {
			log.info("Message from unregistered tracker device");
		}
	}

	private boolean geoDeviceHasNotRegisteredSession(Session session) {
		if (!gpsTrackerDeviceSessions.contains(session)) {
			return true;
		}
		return false;
	}

	private boolean clientDeviceHasNotRegisteredSession(Session session) {
		if (!clientSessions.contains(session)) {
			return true;
		} else {
			return false;
		}
	}

	private void sendGeoDataToAllSessionRegisteredClients(ClientGeoDataMessage geoMessage) {
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

}
