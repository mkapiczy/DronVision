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

	@Override
	public void handleTrackerGeoDataMessage(Message incomingMessage, Session session, 
			Set<Session> gpsTrackerDeviceSessions, Set<Session> clientSessions) {
		if (gpsTrackerDeviceHasRegisteredSession(session, gpsTrackerDeviceSessions)) {
			TrackerGeoDataMessage trackerGeoDataMessage = (TrackerGeoDataMessage) incomingMessage;
			Long droneId = trackerGeoDataMessage.getDeviceId();
			GeoPoint lastPosition = trackerGeoDataMessage.getLastPosition();
			if(droneId!=null && lastPosition!=null){
				Drone drone = droneService.getDroneById(droneId);
				if(drone!=null){
					drone.setLastLocation(new Location(lastPosition));
					droneService.updateDroneSearchedArea(drone);
					clientDeviceService.sendGeoDataToAllSessionRegisteredClients(drone, clientSessions);
				} else{
					log.error("No drone with id: " + droneId);
				}
			} else{
				log.error("Last position and droneId can not be NULL!");
			}
		} else {
			log.info("Message from unregistered session tracker device");
		}
	}
	

	@Override
	public void handleTrackerSimulationMessage(Message incomingMessage, Set<Session> clientSessions) {
		log.info("Simulation message came");
	}

	private boolean gpsTrackerDeviceHasRegisteredSession(Session session, Set<Session> gpsTrackerDeviceSessions) {
		if (gpsTrackerDeviceSessions.contains(session)) {
			return true;
		}
		return false;
	}


}
