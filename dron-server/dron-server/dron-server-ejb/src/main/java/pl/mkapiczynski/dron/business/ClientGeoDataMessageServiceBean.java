package pl.mkapiczynski.dron.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import pl.mkapiczynski.dron.database.Drone;
import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

@Local
@Stateless(name = "ClientGeoDataMessageService")
public class ClientGeoDataMessageServiceBean implements ClientGeoDataMessageService {
	
	@PersistenceContext(name="dron")
	EntityManager entityManager;

	@Override
	public ClientGeoDataMessage generateClientGeoDataMessage(TrackerGeoDataMessage trackerGeoDataMessage) {
		ClientGeoDataMessage clientGeoDataMessage = new ClientGeoDataMessage();
		clientGeoDataMessage.setDeviceId(trackerGeoDataMessage.getDeviceId().toString());
		clientGeoDataMessage.setDeviceType(trackerGeoDataMessage.getDeviceType());
		clientGeoDataMessage.setLastPosition(trackerGeoDataMessage.getLastPosition());
		clientGeoDataMessage.setTimestamp(new Date());
		clientGeoDataMessage.setSearchedArea(calculateSearchedArea(trackerGeoDataMessage));
		
		String queryStr = "Select d FROM Drone d";
		Query query = entityManager.createQuery(queryStr);
		List<Drone> drones = query.getResultList();
		for(int i=0; i<drones.size();i++){
			System.out.println(drones.get(i).getDroneDescription());
		}
		return clientGeoDataMessage;
	}

	/**
	 * To do własnego Bean'a
	 */
	private List<GeoPoint> calculateSearchedArea(TrackerGeoDataMessage geoMessage) {
		List<GeoPoint> searchedAreaList = GeoPoint.pointsAsCircle(geoMessage.getLastPosition(), 20.0);
		return searchedAreaList;
	}
}
