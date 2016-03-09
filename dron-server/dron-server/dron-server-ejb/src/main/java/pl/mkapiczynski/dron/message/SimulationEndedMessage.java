package pl.mkapiczynski.dron.message;

import java.util.Date;
import java.util.List;

import pl.mkapiczynski.dron.domain.GeoPoint;

public class SimulationEndedMessage implements Message {
	private final String messageType = "SimulationEndedMessage";

	public String getMessageType() {
		return messageType;
	}

}
