package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.Message;

/**
 * Interfejs do obsługi kontaktu z instancjami DronTracker
 * @author Michal Kapiczynski
 *
 */
public interface GPSTrackerDeviceService {
	public void handleTrackerLoginMessage(Message incomingMessage, Session session,
			Set<Session> gpsTrackerDeviceSessions);

	public void handleTrackerGeoDataMessage(Message incomingMessage, Session session,
			Set<Session> gpsTrackerDeviceSessions, Set<Session> clientSessions);

	public void simulate(GeoPoint locationToSimulate, Set<Session> clientSessions);

}
