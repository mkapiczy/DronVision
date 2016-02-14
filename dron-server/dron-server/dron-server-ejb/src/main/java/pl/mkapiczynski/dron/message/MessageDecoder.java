package pl.mkapiczynski.dron.message;

import java.io.StringReader;
import java.util.Date;

import javax.json.Json;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pl.mkapiczynski.dron.domain.Constants;
import pl.mkapiczynski.dron.helpers.JsonDateDeserializer;
import pl.mkapiczynski.dron.helpers.JsonDateSerializer;

public class MessageDecoder implements Decoder.Text<Message> {
	private String messageType;

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public Message decode(String jsonMessage) throws DecodeException {
		messageType = Json.createReader(new StringReader(jsonMessage)).readObject().getString("messageType");
		if (Constants.GEO_DATA_MESSAGE_TYPE.equals(messageType)) {
			return decodeGeoDataMessage(jsonMessage);
		} else if (Constants.TRACKER_LOGIN_MESSAGE_TYPE.equals(messageType)) {
			return decodeTrackerLoginMessage(jsonMessage);
		} else if (Constants.CLIENT_LOGIN_MESSAGE_TYPE.equals(messageType)) {
			return decodeClientLoginMessage(jsonMessage);
		} 
		return null;
	}

	@Override
	public boolean willDecode(String jsonMessage) {
		try {
			Gson gson = new Gson();
			gson.fromJson(jsonMessage, Object.class);
			return true;
		} catch (com.google.gson.JsonSyntaxException ex) {
			return false;
		}
	}


	private ClientLoginMessage decodeClientLoginMessage(String jsonMessage) {
		Gson gson = new Gson();
		return gson.fromJson(jsonMessage, ClientLoginMessage.class);
	}

	private TrackerLoginMessage decodeTrackerLoginMessage(String jsonMessage) {
		Gson gson = new Gson();
		return gson.fromJson(jsonMessage, TrackerLoginMessage.class);

	}
	
	private TrackerGeoDataMessage decodeGeoDataMessage(String jsonMessage) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDateSerializer());
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
		Gson gson = gsonBuilder.create();
		return gson.fromJson(jsonMessage, TrackerGeoDataMessage.class);
	}
	
	

}
