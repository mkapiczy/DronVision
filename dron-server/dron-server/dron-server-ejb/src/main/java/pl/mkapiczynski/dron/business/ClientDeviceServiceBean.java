package pl.mkapiczynski.dron.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.ClientUser;
import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.database.SearchedArea;
import pl.mkapiczynski.dron.database.SimulationSession;
import pl.mkapiczynski.dron.domain.Constants;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.Message;

@Local
@Stateless(name = "ClientDeviceService")
public class ClientDeviceServiceBean implements ClientDeviceService {

	private static final Logger log = Logger.getLogger(ClientDeviceServiceBean.class);

	@Inject
	private SearchedAreaService searchedAreaService;

	@Inject
	private DroneService droneService;

	@Inject
	private SimulationSessionService simulationSessionService;

	@Override
	public void handleClientLoginMessage(Message incomingMessage, Session newsSession, Set<Session> clientSessions) {
		ClientLoginMessage clientLoginMessage = (ClientLoginMessage) incomingMessage;
		Long clientId = clientLoginMessage.getClientId();
		Session alreadyRegisteredClientSession = findSessionForClinetId(clientSessions, clientId);
		if (alreadyRegisteredClientSession != null) {
			closePreviousClientSession(clientId, alreadyRegisteredClientSession);
		}
		registerNewClientSession(clientId, newsSession, clientSessions);
	}

	@Override
	public void sendGeoDataToAllSessionRegisteredClients(Drone drone, Set<Session> clientSessions) {
		ClientGeoDataMessage geoMessage = generateClientGeoDataMessage(drone);
		Iterator<Session> iterator = clientSessions.iterator();
		while (iterator.hasNext()) {
			Session currentClient = iterator.next();
			Long clientId = (Long) currentClient.getUserProperties().get("clientId");
			if (clientIsAssignedToDrone(currentClient, drone)) {
				try {
					currentClient.getAsyncRemote().sendObject(geoMessage);
					log.info("Message send to client : " + currentClient.getUserProperties().get("clientId"));
				} catch (IllegalArgumentException e) {
					log.error("Illegal argument exception while sending a message to client: "
							+ currentClient.getUserProperties().get("clientId") + " : " + e);
				}
			}
		}
	}

	private ClientGeoDataMessage generateClientGeoDataMessage(Drone drone) {
		ClientGeoDataMessage clientGeoDataMessage = new ClientGeoDataMessage();
		clientGeoDataMessage.setDeviceId(drone.getDroneId());
		clientGeoDataMessage.setDeviceType(Constants.GPS_TRACKED_DEVICE_TYPE);
		clientGeoDataMessage.setDeviceName(drone.getDroneName());
		clientGeoDataMessage.setLastPosition(new GeoPoint(drone.getLastLocation().getLatitude(),
				drone.getLastLocation().getLongitude(), drone.getLastLocation().getAltitude()));
		clientGeoDataMessage.setTimestamp(new Date());
		DroneSession activeSession = droneService.getActiveDroneSession(drone);
		if (activeSession != null) {
			if (activeSession.getSearchedArea() != null
					&& activeSession.getSearchedArea().getSearchedLocations() != null) {
				List<GeoPoint> searchedArea = convertLocationSearchedAreaToGeoPointSearchedArea(
						activeSession.getSearchedArea().getSearchedLocations());
				clientGeoDataMessage.setSearchedArea(searchedArea);
			}
			if (activeSession.getLastSearchedArea() != null
					&& activeSession.getLastSearchedArea().getSearchedLocations() != null) {
				List<GeoPoint> lastSearchedArea = convertLocationSearchedAreaToGeoPointSearchedArea(
						activeSession.getLastSearchedArea().getSearchedLocations());
				clientGeoDataMessage.setLastSearchedArea(lastSearchedArea);
				List<GeoPoint> lastHoles = new ArrayList<>();
				if (activeSession.getLastSearchedArea().getHolesInSearchedArea() != null) {

					lastHoles.addAll(convertLocationSearchedAreaToGeoPointSearchedArea(
							activeSession.getLastSearchedArea().getHolesInSearchedArea()));

				}
				clientGeoDataMessage.setLastSearchedAreaHoles(lastHoles);
				List<GeoPoint> holes = new ArrayList<>();
				if (activeSession.getSearchedArea().getHolesInSearchedArea() != null) {

					holes.addAll(convertLocationSearchedAreaToGeoPointSearchedArea(
							activeSession.getSearchedArea().getHolesInSearchedArea()));

				}
				clientGeoDataMessage.setSearchedAreaHoles(holes);
			}
		}
		return clientGeoDataMessage;
	}

	private void closePreviousClientSession(Long clientId, Session alreadyRegisteredClientSession) {
		try {
			alreadyRegisteredClientSession.getUserProperties().put("clientId",
					"Previous session for client with id: " + clientId);
			alreadyRegisteredClientSession.close();
		} catch (IOException e) {
			log.error("Exception while closing previous session for client with id " + clientId + " : " + e);
		}
	}

	private void registerNewClientSession(Long clientId, Session newSession, Set<Session> clientSessions) {
		newSession.getUserProperties().put("clientId", clientId);
		clientSessions.add(newSession);
		System.out.println("New clientDevice with id: " + clientId);
	}

	private Session findSessionForClinetId(Set<Session> clientSessions, Long clientId) {
		Iterator<Session> clientIterator = clientSessions.iterator();
		while (clientIterator.hasNext()) {
			Session currentIteratedClientSession = clientIterator.next();
			if (((Long) currentIteratedClientSession.getUserProperties().get("clientId")).equals(clientId)) {
				return currentIteratedClientSession;
			}
		}
		return null;
	}

	private boolean clientIsAssignedToDrone(Session clientSession, Drone drone) {
		for (int i = 0; i < drone.getAssignedUsers().size(); i++) {
			ClientUser userClient = drone.getAssignedUsers().get(i);
			if (userClient.getUserId() == clientSession.getUserProperties().get("clientId")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO Do usunięcia, taka sama metoda w GPSTraxckesServiceBean
	 */
	private static List<GeoPoint> convertLocationSearchedAreaToGeoPointSearchedArea(
			List<Location> locationSearchedArea) {
		List<GeoPoint> geoPointSearchedArea = new ArrayList<>();
		for (int i = 0; i < locationSearchedArea.size(); i++) {
			GeoPoint geoP = new GeoPoint();
			geoP.setLatitude(locationSearchedArea.get(i).getLatitude());
			geoP.setLongitude(locationSearchedArea.get(i).getLongitude());
			geoPointSearchedArea.add(geoP);
		}
		return geoPointSearchedArea;
	};

}
