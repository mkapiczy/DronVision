package pl.mkapiczynski.dron.decoder;

import java.io.StringReader;

import javax.json.Json;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import pl.mkapiczynski.dron.message.ChatMessage;
import pl.mkapiczynski.dron.message.ClientLoginMessage;
import pl.mkapiczynski.dron.message.GeoDataMessage;
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
				GeoDataMessage geoMessage = new GeoDataMessage();	
				geoMessage.setDeviceId((Json.createReader(new StringReader(jsonMessage)).readObject().getString("deviceId")));
				geoMessage.setDeviceType((Json.createReader(new StringReader(jsonMessage)).readObject().getString("deviceType")));
				geoMessage.setTimestamp((Json.createReader(new StringReader(jsonMessage)).readObject().getString("timestamp")));
				geoMessage.setLatitude((Json.createReader(new StringReader(jsonMessage)).readObject().getString("latitude")));
				geoMessage.setLongitude((Json.createReader(new StringReader(jsonMessage)).readObject().getString("longitude")));
				geoMessage.setAltitude((Json.createReader(new StringReader(jsonMessage)).readObject().getString("altitude")));
				return geoMessage;
		} else if("ClientLoginMessage".equals(messageType)){
			ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
			clientLoginMessage.setClientId(Json.createReader(new StringReader(jsonMessage)).readObject().getString("clientId"));
			return clientLoginMessage;
		}
		return null;
	}

	@Override
	public boolean willDecode(String jsonMessage) {
		try {
			Json.createReader(new StringReader(jsonMessage)).readObject();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
