package pl.mkapiczynski.dron.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

public class ServerThread extends Thread{
	Socket socket;
	
	ServerThread(Socket socket){
		this.socket = socket;
	}
	
	public void run() {
		String message = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while((message = reader.readLine()) != null){
				Date date = new Date();
				System.out.println(date.getTime() + " | " + message);
			}
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
