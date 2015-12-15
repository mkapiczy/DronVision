package pl.mkapiczynski.dron.encoder;

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

import pl.mkapiczynski.dron.message.ChatMessage;
import pl.mkapiczynski.dron.message.Message;
import pl.mkapiczynski.dron.message.UsersMessage;

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
		if (message instanceof ChatMessage) {
			ChatMessage chatMessage = (ChatMessage) message;
			encodedMessage = buildJsonChatMessageData(chatMessage, chatMessage.getClass().getSimpleName());
		} else if (message instanceof UsersMessage) {
			UsersMessage usersMessage = (UsersMessage) message;
			encodedMessage = buildJsonUsersData(usersMessage.getUsers(), usersMessage.getClass().getSimpleName());
		}
		return encodedMessage;
	}

	private String buildJsonUsersData(Set<String> usersSet, String messageType) {
		Iterator<String> iterator = usersSet.iterator();
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		while (iterator.hasNext()) {
			jsonArrayBuilder.add(iterator.next());
		}
		return Json.createObjectBuilder().add("messageType", messageType).add("users", jsonArrayBuilder).build()
				.toString();
	}

	private String buildJsonChatMessageData(ChatMessage chatMessage, String messageType) {
		JsonObject jsonObject = Json.createObjectBuilder().add("messageType", messageType)
				.add("name", chatMessage.getName()).add("location", chatMessage.getLocation())
				.add("message", chatMessage.getMessage()).build();
		StringWriter stringWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
			jsonWriter.write(jsonObject);
		}
		return stringWriter.toString();
	}

}
