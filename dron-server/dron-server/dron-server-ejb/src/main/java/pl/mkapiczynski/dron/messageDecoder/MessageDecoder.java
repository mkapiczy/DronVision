package pl.mkapiczynski.dron.messageDecoder;

import java.io.StringReader;
import java.util.Date;

import javax.json.Json;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jsonHelper.JsonDateDeserializer;
import jsonHelper.JsonDateSerializer;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.TrackerGeoDataMessage;
import pl.mkapiczynski.dron.message.TrackerLoginMessage;
import pl.mkapiczynski.dron.message.Message;

public class MessageDecoder implements Decoder.Text<Message>{
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
		if("GeoDataMessage".equals(messageType)){
				return decodeGeoDataMessage(jsonMessage);
		} else if("TrackerLoginMessage".equals(messageType)){
			return decodeTrackerLoginMessage(jsonMessage);
		}
		else if("ClientLoginMessage".equals(messageType)){
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
	      } catch(com.google.gson.JsonSyntaxException ex) { 
	          return false;
	      }
	}
	
	private TrackerGeoDataMessage decodeGeoDataMessage(String jsonMessage){
		TrackerGeoDataMessage geoMessage = new TrackerGeoDataMessage();	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDateSerializer());
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDateDeserializer());
		Gson gson = gsonBuilder.create();
		geoMessage = gson.fromJson(jsonMessage, TrackerGeoDataMessage.class);
		return geoMessage;
	}
	
	private ClientLoginMessage decodeClientLoginMessage(String jsonMessage){
		ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
		clientLoginMessage.setClientId(Json.createReader(new StringReader(jsonMessage)).readObject().getString("clientId"));
		return clientLoginMessage;
	}
	
	private TrackerLoginMessage decodeTrackerLoginMessage(String jsonMessage){
		TrackerLoginMessage trackerLoginMessage = new TrackerLoginMessage();
		Gson gson = new Gson();
		trackerLoginMessage.setDeviceId(Json.createReader(new StringReader(jsonMessage)).readObject().getString("deviceId"));
		return trackerLoginMessage;
	}

}