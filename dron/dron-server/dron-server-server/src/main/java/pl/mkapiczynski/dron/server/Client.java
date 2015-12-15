package pl.mkapiczynski.dron.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Client {
	private static final String HOST = "0.tcp.ngrok.io";
	private static final Integer PORT = 47902;
	private static final String URL = "https://3f4cceb8.ngrok.io";

	public static void main(String[] args) {
//		String name = args[0];
//		try {
//			Socket socket = new Socket(HOST, PORT);
//			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//			while (true) {
//				String readerInput = reader.readLine();
//
//				
//				writer.println(name + ": " + readerInput);
//			}
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		URL url = null;
		try {
			 url = new URL(URL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String urlParameters  = "param1=a&param2=b&param3=c";
			byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
			int    postDataLength = postData.length;
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
			conn.setUseCaches( false );
			DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
			wr.write( postData );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
		
		}
		

	}

}
