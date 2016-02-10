package pl.mkapiczynski.dron.business;

import java.util.Set;

import javax.websocket.Session;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.database.Location;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.Message;

public interface GPSTrackerDeviceService {
	public void handleTrackerLoginMessage(Message incomingMessage, Session session, Set<Session> gpsTrackerDeviceSessions);
	public void handleTrackerGeoDataMessage(Message incomingMessage, Session session, Set<Session> gpsTrackerDeviceSessions, Set<Session> clientSessions);
	

}
