package pl.mkapiczynski.dron.clientendpoint;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class ChatroomClientEndpoint {
	private static final String SERVER_URL = "ws://localhost:8080//dron-server-web/chatroom";
	private Session session;
	
	public ChatroomClientEndpoint() throws IOException, DeploymentException{
		try {
			URI uri = new URI(SERVER_URL);
			ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@OnOpen
	public void processOpen(Session session){
		this.session = session;
	}
	
	@OnMessage
	public void processMessage(String message){
		System.out.println(Json.createReader(new StringReader(message)).readObject().getString("message"));
	}
	
	public void sendMessage(String message) throws IOException{
		session.getBasicRemote().sendText(message);
	}
	

}
