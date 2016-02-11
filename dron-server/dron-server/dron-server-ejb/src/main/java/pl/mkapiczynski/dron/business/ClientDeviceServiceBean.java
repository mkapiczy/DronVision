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
import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.DroneSession;
import pl.mkapiczynski.dron.database.Location;
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
	public void sendGeoDataToAllSessionRegisteredClients(Drone drone, Set<Session> clientSessions) {
		ClientGeoDataMessage geoMessage = generateClientGeoDataMessage(drone);
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
	
	private ClientGeoDataMessage generateClientGeoDataMessage(Drone drone) {
		ClientGeoDataMessage clientGeoDataMessage = new ClientGeoDataMessage();
		clientGeoDataMessage.setDeviceId(drone.getDroneId());
		clientGeoDataMessage.setDeviceType("GPSTracker");
		clientGeoDataMessage.setLastPosition(new GeoPoint(drone.getLastLocation().getLatitude(), drone.getLastLocation().getLongitude(), drone.getLastLocation().getAltitude()));
		clientGeoDataMessage.setTimestamp(new Date());
		DroneSession activeSession = droneService.getActiveDroneSession(drone);
		if(activeSession!=null && activeSession.getSearchedArea()!=null){
			List<GeoPoint> searchedArea = convertLocationSearchedAreaToLocationSearchedArea(activeSession.getSearchedArea().getSearchedLocations());
			clientGeoDataMessage.setSearchedArea(searchedArea);
		}
		return clientGeoDataMessage;
	}


	
	private boolean clientDeviceHasNotRegisteredSession(Session session, Set<Session> clientSessions) {
		if (!clientSessions.contains(session)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * TODO
	 * Do usunięcia, taka sama metoda w GPSTraxckesServiceBean
	 */
	private static List<GeoPoint> convertLocationSearchedAreaToLocationSearchedArea(List<Location> locationSearchedArea) {
		List<GeoPoint> geoPointSearchedArea = new ArrayList<>();
		for(int i=0; i<locationSearchedArea.size();i++){
			GeoPoint geoP = new GeoPoint(locationSearchedArea.get(i));
			geoPointSearchedArea.add(geoP);
		}
		return geoPointSearchedArea;
	};
	

}
