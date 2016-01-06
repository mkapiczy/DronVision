package pl.mkapiczynski.dron.messageEncoder;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import pl.mkapiczynski.dron.message.GeoDataMessage;
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
		 if(message instanceof GeoDataMessage){
			GeoDataMessage geoDataMessage = (GeoDataMessage) message;
			encodedMessage = buildJsonGeoDataMessage(geoDataMessage, geoDataMessage.getClass().getSimpleName());
		}
		return encodedMessage;
	}

	private String buildJsonGeoDataMessage(GeoDataMessage geoDataMessage, String messageType) {
		JsonObject jsonObject = Json.createObjectBuilder().add("messageType", messageType)
				.add("deviceId", geoDataMessage.getDeviceId())
				.add("deviceType", geoDataMessage.getDeviceType())
				.add("timestamp", geoDataMessage.getTimestamp())
				.add("latitude", geoDataMessage.getLatitude())
				.add("longitude", geoDataMessage.getLongitude())
				.add("altitude", geoDataMessage.getAltitude()).build();
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.write(jsonObject);
		}
		return stringWriter.toString();
	}
	
	/**
	 * TODO Do skasowania. Zostaje jaki wzór
	 * 
	 */
	/*
	private String buildJsonUsersData(Set<String> usersSet, String messageType) {
		Iterator<String> iterator = usersSet.iterator();
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		while (iterator.hasNext()) {
			jsonArrayBuilder.add(iterator.next());
		}
		return Json.createObjectBuilder().add("messageType", messageType).add("users", jsonArrayBuilder).build()
				.toString();
	}*/

}
