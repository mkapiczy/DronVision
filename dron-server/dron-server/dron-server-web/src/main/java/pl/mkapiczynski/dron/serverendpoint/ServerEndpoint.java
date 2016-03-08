package pl.mkapiczynski.dron.serverendpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.business.ClientDeviceService;
import pl.mkapiczynski.dron.business.DroneService;
import pl.mkapiczynski.dron.business.GPSTrackerDeviceService;
import pl.mkapiczynski.dron.business.SimulationService;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.MessageDecoder;
import pl.mkapiczynski.dron.message.MessageEncoder;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerLoginMessage;
import pl.mkapiczynski.dron.message.TrackerSimulationMessage;

@javax.websocket.server.ServerEndpoint(value = "/server", encoders = { MessageEncoder.class }, decoders = {
		MessageDecoder.class, })
public class ServerEndpoint {
	private static final Logger log = Logger.getLogger(ServerEndpoint.class);

	@Inject
	private GPSTrackerDeviceService gpsTrackerDeviceService;

	@Inject
	private ClientDeviceService clientDeviceService;

	@Inject
	private DroneService droneService;

	@Inject
	private SimulationService simulationService;

	public static Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());
	public static Set<Session> gpsTrackerDeviceSessions = Collections.synchronizedSet(new HashSet<Session>());
	public static Set<Session> clientDeviceSessions = Collections.synchronizedSet(new HashSet<Session>());

	@OnOpen
	public void handleOpen(Session session) throws IOException, EncodeException {
		allSessions.add(session);
	}

	@OnMessage
	public void handleMessage(Message incomingMessage, Session session) throws IOException, EncodeException {
		if (incomingMessage instanceof TrackerLoginMessage) {
			gpsTrackerDeviceService.handleTrackerLoginMessage(incomingMessage, session, gpsTrackerDeviceSessions);
		} else if (incomingMessage instanceof ClientLoginMessage) {
			clientDeviceService.handleClientLoginMessage(incomingMessage, session, clientDeviceSessions);
		} else if (incomingMessage instanceof TrackerGeoDataMessage) {
			gpsTrackerDeviceService.handleTrackerGeoDataMessage(incomingMessage, session, gpsTrackerDeviceSessions,
					clientDeviceSessions);
		} else if (incomingMessage instanceof TrackerSimulationMessage) {
			simulationService.handleTrackerSimulationMessage(incomingMessage, clientDeviceSessions);
		}
	}

	@OnClose
	public void handleClose(Session closingSession) throws IOException, EncodeException {
		if (clientDeviceSessions.contains(closingSession)) {
			handleCloseClientDeviceSession(closingSession);
		} else if (gpsTrackerDeviceSessions.contains(closingSession)) {
			handleCloseGPSTrackerDeviceSession(closingSession);
		}
		allSessions.remove(closingSession);
	}

	@OnError
	public void handleError(Session sessionOnError, Throwable t) {
		if (sessionOnError.isOpen()) {
			try {
				handleClose(sessionOnError);
				sessionOnError.close();
			} catch (IOException e) {
				log.error("Error while closing session " + e);
			} catch (EncodeException e) {
				log.error("Error while closing session " + e);
			}
		}
		log.error("Websocket connection error has occured : " + t.getMessage().toString());
	}

	private void handleCloseClientDeviceSession(Session closingSession) {
		clientDeviceSessions.remove(closingSession);
		log.info("Client : " + closingSession.getUserProperties().get("clientId") + " disconnected");
	}

	private void handleCloseGPSTrackerDeviceSession(Session closingSession) {
		Long droneId = (long) closingSession.getUserProperties().get("deviceId");
		droneService.closeDroneSession(droneId);
		gpsTrackerDeviceSessions.remove(closingSession);
		log.info("Tracker device : " + closingSession.getUserProperties().get("deviceId") + " disconnected");
	}

}
