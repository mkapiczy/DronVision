package pl.mkapiczynski.dron.serverendpoint;

import java.io.IOException;
import java.util.Collections;
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

import pl.mkapiczynski.dron.decoder.MessageDecoder;
import pl.mkapiczynski.dron.encoder.MessageEncoder;
import pl.mkapiczynski.dron.message.ChatMessage;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.GeoDataMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.UsersMessage;

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
		if (incomingMessage instanceof GeoDataMessage){
			handleGeoDataMessage(incomingMessage, session);
		} else if(incomingMessage instanceof ClientLoginMessage){
			handleClientLoginMessage(incomingMessage, session);
		}
	}

	@OnClose
	public void handleClose(Session closingSession) throws IOException, EncodeException {
		if(clientSessions.contains(closingSession)){
			clientSessions.remove(closingSession);
		} else if(gpsTrackerDeviceSessions.contains(closingSession)){
			gpsTrackerDeviceSessions.remove(closingSession);
		}
		allSessions.remove(closingSession);
	}

	@OnError
	public void handleError(Throwable t) {
		log.error("Error has occured : " + t.getMessage().toString());
	}
	
	private void handleGeoDataMessage(Message incomingMessage, Session session){
		GeoDataMessage geoMessage = (GeoDataMessage) incomingMessage;
		if(geoDeviceHasNotRegisteredSession(session)){
			session.getUserProperties().put("deviceId", geoMessage.getDeviceId());
			gpsTrackerDeviceSessions.add(session);
		}
		sendGeoDataToAllSessionRegisteredClients(geoMessage);
	}
	
	private void handleClientLoginMessage(Message incomingMessage, Session session){
		ClientLoginMessage clientLoginMessage = (ClientLoginMessage) incomingMessage;
		if(clientDeviceHasNotRegisteredSession(session)){
			session.getUserProperties().put("deviceId", clientLoginMessage.getClientId());
			clientSessions.add(session);
		}
		System.out.println("New clientDevice : " + clientLoginMessage.getClientId());
	}
	
	private boolean geoDeviceHasNotRegisteredSession(Session session){
		if (!gpsTrackerDeviceSessions.contains(session)) {
			return true;
		}
		return false;
	}
	
	private boolean clientDeviceHasNotRegisteredSession(Session session){
		if(!clientSessions.contains(session)){
			return true;
		} else{
			return false;
		}
	}
	
	private void sendGeoDataToAllSessionRegisteredClients(GeoDataMessage geoMessage){
		Iterator<Session> iterator = clientSessions.iterator();
		while(iterator.hasNext()){
			Session currentClient = iterator.next();
			try {
				currentClient.getBasicRemote().sendObject(geoMessage);
			} catch (IOException | EncodeException e) {
				log.error("Error occured while sendig message to client : " + currentClient.getUserProperties().get("clientId") + " Error message : " + e );
			}
		}
	}

}
