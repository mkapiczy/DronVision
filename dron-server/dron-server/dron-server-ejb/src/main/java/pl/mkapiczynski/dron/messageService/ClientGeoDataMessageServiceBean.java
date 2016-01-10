package pl.mkapiczynski.dron.messageService;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import pl.mkapiczynski.dron.domain.GeoPoint;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

@Local
@Stateless(name = "ClientGeoDataMessageService")
public class ClientGeoDataMessageServiceBean implements ClientGeoDataMessageService {

	@Override
	public ClientGeoDataMessage generateClientGeoDataMessage(TrackerGeoDataMessage trackerGeoDataMessage) {
		ClientGeoDataMessage clientGeoDataMessage = new ClientGeoDataMessage();
		clientGeoDataMessage.setDeviceId(trackerGeoDataMessage.getDeviceId());
		clientGeoDataMessage.setDeviceType(trackerGeoDataMessage.getDeviceType());
		clientGeoDataMessage.setLastPosition(trackerGeoDataMessage.getLastPosition());
		clientGeoDataMessage.setTimestamp(new Date());
		clientGeoDataMessage.setSearchedArea(calculateSearchedArea(trackerGeoDataMessage));
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
