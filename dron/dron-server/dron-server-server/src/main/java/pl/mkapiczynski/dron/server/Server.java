package pl.mkapiczynski.dron.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {
	private static final Logger log = Logger.getLogger(Server.class.toString());
	private static final int PORT = 8080;

	public static void main(String[] args) {
		try {
			new Server().runServer();
		} catch (IOException e) {
			System.out.println("Unable to run Server on port " + PORT);
		}
	}

	public void runServer() throws IOException {
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server up & ready for connections...");
		while (true) {
			Socket socket = serverSocket.accept();
			new ServerThread(socket).start();
		}
	}

}
