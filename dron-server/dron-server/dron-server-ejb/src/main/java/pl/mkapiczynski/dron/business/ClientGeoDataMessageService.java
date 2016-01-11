package pl.mkapiczynski.dron.business;

import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

public interface ClientGeoDataMessageService {
	public ClientGeoDataMessage generateClientGeoDataMessage(TrackerGeoDataMessage trackerGeoDataMessage);
}
