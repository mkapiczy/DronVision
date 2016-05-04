package pl.mkapiczynski.dron.business;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.database.SimulationSession;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerLoginMessage;

/**
 * Klasa biznesowa do obsługi kontaktu z instancjami aplikacji DronTracker
 * @author Michal Kapiczynski
 *
 */
@Local
@Stateless(name = "GPSTrackerDeviceService")
public class GPSTrackerDeviceServiceBean implements GPSTrackerDeviceService {
	private static final Logger log = Logger.getLogger(GPSTrackerDeviceServiceBean.class);

	@PersistenceContext(name = "dron")
	private EntityManager entityManager;

	@Inject
	private ClientDeviceService clientDeviceService;

	@Inject
	private DroneService droneService;

	@Inject
	private SimulationSessionService simulationSessionService;

	/**
	 * Metoda do obsługi wiadomości logującej od DronTrackera
	 */
	@Override
	public void handleTrackerLoginMessage(Message incomingMessage, Session session,
			Set<Session> gpsTrackerDeviceSessions) {
		TrackerLoginMessage trackerLoginMessage = (TrackerLoginMessage) incomingMessage;
		Long droneId = trackerLoginMessage.getDeviceId();
		if (!gpsTrackerDeviceHasRegisteredSession(session, gpsTrackerDeviceSessions)) {
			if (droneService.createNewDroneSession(droneId)) {
				session.getUserProperties().put("deviceId", droneId);
				gpsTrackerDeviceSessions.add(session);
				System.out.println("New trackerDevice with id: " + droneId);
			}
		} else {
			log.info("Login message from  already registered tracker device with id: " + droneId);
		}

	}
	/**
	 * Metoda do obsługi wiadomości o zmianie położenia od aplikacji DronTracker
	 */
	@Override
	public void handleTrackerGeoDataMessage(Message incomingMessage, Session session,
			Set<Session> gpsTrackerDeviceSessions, Set<Session> clientSessions) {
		if (gpsTrackerDeviceHasRegisteredSession(session, gpsTrackerDeviceSessions)) {
			TrackerGeoDataMessage trackerGeoDataMessage = (TrackerGeoDataMessage) incomingMessage;
			Long droneId = trackerGeoDataMessage.getDeviceId();
			GeoPoint lastPosition = trackerGeoDataMessage.getLastPosition();
			calculateSearchedAreaAndSendMessageToClients(droneId, lastPosition, clientSessions);
		} else {
			log.info("Message from unregistered session tracker device");
		}
	}
	/**
	 * Metoda pomocnicza dla symulacji
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void simulate(GeoPoint locationToSimulate, final Set<Session> clientSessions) {
		final Long droneId = 4l;
		calculateSearchedAreaAndSendMessageToClients(droneId, locationToSimulate, clientSessions);

	}
	
	private void calculateSearchedAreaAndSendMessageToClients(Long droneId, GeoPoint lastPosition,
			Set<Session> clientSessions) {
		log.info("Method calculateSearchedAreaAndSendMessageToClients started: " + new Date());
		if (droneId != null && lastPosition != null) {
			Drone drone = droneService.getDroneById(droneId);
			if (drone != null) {
				drone.setLastLocation(new Location(lastPosition));
				droneService.updateDroneSearchedArea(drone);
				simulationSessionService.updateSimulationData(drone, clientSessions);
				clientDeviceService.sendGeoDataToAllSessionRegisteredClients(drone, clientSessions);
			} else {
				log.error("No drone with id: " + droneId);
			}
		} else {
			log.error("Last position and droneId can not be NULL!");
		}
		log.info("Method calculateSearchedAreaAndSendMessageToClients ended: " + new Date());
	}

	private boolean gpsTrackerDeviceHasRegisteredSession(Session session, Set<Session> gpsTrackerDeviceSessions) {
		if (gpsTrackerDeviceSessions.contains(session)) {
			return true;
		}
		return false;
	}
}
