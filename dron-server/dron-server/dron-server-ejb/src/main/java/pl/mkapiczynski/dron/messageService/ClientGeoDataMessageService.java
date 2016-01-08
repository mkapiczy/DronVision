package pl.mkapiczynski.dron.messageService;

import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;

public interface ClientGeoDataMessageService {
	public ClientGeoDataMessage generateClientGeoDataMessage(TrackerGeoDataMessage trackerGeoDataMessage);
}
