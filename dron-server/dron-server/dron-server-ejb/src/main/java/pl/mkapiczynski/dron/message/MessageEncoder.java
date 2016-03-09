package pl.mkapiczynski.dron.message;

import java.util.Date;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.mkapiczynski.dron.helpers.JsonDateSerializer;
import pl.mkapiczynski.dron.message.ClientGeoDataMessage;
import pl.mkapiczynski.dron.message.Message;

public class MessageEncoder implements Encoder.Text<Message> {

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public String encode(Message message) throws EncodeException {
		String encodedMessage = "";
		if (message instanceof ClientGeoDataMessage) {
			ClientGeoDataMessage clientGeoDataMessage = (ClientGeoDataMessage) message;
			encodedMessage = buildJsonClientGeoDataMessage(clientGeoDataMessage);
		} else if (message instanceof SimulationEndedMessage){
			SimulationEndedMessage simulationEndedMessage = (SimulationEndedMessage) message;
			encodedMessage = buildJsonSimulatioNEndedMessage(simulationEndedMessage);
		}
		return encodedMessage;
	}

	private String buildJsonClientGeoDataMessage(ClientGeoDataMessage clientGeoDataMessage) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDateSerializer());
		Gson gson = gsonBuilder.create();

		return gson.toJson(clientGeoDataMessage);
	}
	
	private String buildJsonSimulatioNEndedMessage(SimulationEndedMessage simulationEndedMessage) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		return gson.toJson(simulationEndedMessage);
	}

}
