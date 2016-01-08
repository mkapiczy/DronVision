package pl.mkapiczynski.dron.serverendpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import domain.GeoPoint;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerLoginMessage;
import pl.mkapiczynski.dron.messageDecoder.MessageDecoder;
import pl.mkapiczynski.dron.messageEncoder.MessageEncoder;

@javax.websocket.server.ServerEndpoint(value = "/server", encoders = { MessageEncoder.class }, decoders = {
		MessageDecoder.class, })
public class ServerEndpoint {
	private static final Logger log = Logger.getLogger(ServerEndpoint.class);
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
		} else if (gpsTrackerDeviceSessions.contains(closingSession)) {
			gpsTrackerDeviceSessions.remove(closingSession);
		}
		allSessions.remove(closingSession);
	}

	@OnError
	public void handleError(Throwable t) {
		log.error("Error has occured : " + t.getMessage().toString());
	}

	private void handleTrackerGeoDataMessage(Message incomingMessage, Session session) {
		if (gpsTrackerDeviceSessions.contains(session)) {
			TrackerGeoDataMessage geoMessage = (TrackerGeoDataMessage) incomingMessage;
			ClientGeoDataMessage clientGeoMessage = new ClientGeoDataMessage();
			clientGeoMessage.setDeviceId(geoMessage.getDeviceId());
			clientGeoMessage.setDeviceType(geoMessage.getDeviceType());
			clientGeoMessage.setLastPosition(geoMessage.getLastPosition());
			clientGeoMessage.setTimestamp(new Date());
			clientGeoMessage.setSearchedArea(GeoPoint.pointsAsCircle(geoMessage.getLastPosition(), 35.0));
			/**
			 * Analiza obszaru przeszukanego Zapis do bazy danych
			 */
			sendGeoDataToAllSessionRegisteredClients(clientGeoMessage);
		} else{
			log.info("Message from not registered tracker device");
		}
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
			session.getUserProperties().put("deviceId", trackerLoginMessage.getDeviceId());
			gpsTrackerDeviceSessions.add(session);
		}
		System.out.println("New trackerDevice : " + trackerLoginMessage.getDeviceId());
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
