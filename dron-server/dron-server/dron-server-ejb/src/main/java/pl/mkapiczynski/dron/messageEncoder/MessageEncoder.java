package pl.mkapiczynski.dron.messageEncoder;

import java.util.Date;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jsonHelper.JsonDateSerializer;
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
			encodedMessage = buildJsonClientGeoDataMessage(clientGeoDataMessage, clientGeoDataMessage.getMessageType());
		}
		return encodedMessage;
	}

	private String buildJsonClientGeoDataMessage(ClientGeoDataMessage geoDataMessage, String messageType) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDateSerializer());
		Gson gson = gsonBuilder.create();

		return gson.toJson(geoDataMessage);
	}

}
