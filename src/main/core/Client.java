package main.core;

import java.net.*;
import java.io.*;

public class Client {
	public static void main(String[] args) throws Exception {

		startConnection("127.0.0.1", Integer.valueOf(args[0]));
	}

	public static void startConnection(String host, int port) {
		try {
			Socket socket = new Socket("127.0.0.1", port);
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String clientMessage = "", serverMessage = "";
			while (!clientMessage.equals("terminate")) {
				System.out.println("Enter action :");
				clientMessage = br.readLine();
				outStream.writeUTF(clientMessage);
				outStream.flush();

				serverMessage = inStream.readUTF();
				System.out.println(serverMessage);

			}
			outStream.close();
			outStream.close();
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
