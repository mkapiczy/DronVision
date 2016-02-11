package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.Session;

import org.jboss.logging.Logger;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerLoginMessage;

@Local
@Stateless(name = "GPSTrackerDeviceService")
public class GPSTrackerDeviceServiceBean implements GPSTrackerDeviceService {
	private static final Logger log = Logger.getLogger(GPSTrackerDeviceServiceBean.class);

	@Inject
	private ClientDeviceService clientDeviceService;
	
	@Inject
	private DroneService droneService;

	@Override
	public void handleTrackerLoginMessage(Message incomingMessage, Session session, Set<Session> gpsTrackerDeviceSessions) {
		TrackerLoginMessage trackerLoginMessage = (TrackerLoginMessage) incomingMessage;
		Long droneId = trackerLoginMessage.getDeviceId();
		if (gpsTrackerDeviceHasNotRegisteredSession(session, gpsTrackerDeviceSessions)) {
			if (droneService.createNewDroneSession(droneId)) {
				session.getUserProperties().put("deviceId", droneId);
				gpsTrackerDeviceSessions.add(session);
				System.out.println("New trackerDevice with id: " + droneId);
			}
		} else {
			log.info("Login message from unregistered tracker device with id: " + droneId);
		}

	}

	@Override
	public void handleTrackerGeoDataMessage(Message incomingMessage, Session session, 
			Set<Session> gpsTrackerDeviceSessions, Set<Session> clientSessions) {
		ClientGeoDataMessage clientGeoMessage = null;
		if (gpsTrackerDeviceSessions.contains(session)) {
			TrackerGeoDataMessage trackerGeoDataMessage = (TrackerGeoDataMessage) incomingMessage;
			Long droneId = trackerGeoDataMessage.getDeviceId();
			GeoPoint lastPosition = trackerGeoDataMessage.getLastPosition();
			if(lastPosition!=null){
				Drone drone = droneService.getDroneById(droneId);
				if(drone!=null){
					Location lastLocation = new Location(lastPosition);
					drone.setLastLocation(lastLocation);
					droneService.updateDroneSearchedArea(drone, lastLocation);
					clientDeviceService.sendGeoDataToAllSessionRegisteredClients(drone, clientSessions);
				} else{
					log.error("Drone can't be NULL!");
				}
			} else{
				log.error("Last position can't be NULL!");
			}
		} else {
			log.info("Message from unregistered tracker device");
		}
	}

	private boolean gpsTrackerDeviceHasNotRegisteredSession(Session session, Set<Session> gpsTrackerDeviceSessions) {
		if (!gpsTrackerDeviceSessions.contains(session)) {
			return true;
		}
		return false;
	}

}
