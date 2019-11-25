package main.core;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

public class Server {
	private ServerSocket server;
	static List<String> serverList = new ArrayList();
	public static int datacentreID;

	/*
	 * Assuming the servers start with the information about each ther server in the
	 * loop. all the servers run on same machine, hence server will use localhost
	 * for ipaddress Inputs: args[0] - port args[1] - datacenterid args[2] -
	 * datecenterList
	 */
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);

		int port = Integer.valueOf(args[0]);
		datacentreID = Integer.valueOf(args[1]);
		System.out.println("Enter the number of servers");
		int n = sc.nextInt();
		System.out.println("Enter the server List");
		while (n != 0) {
			serverList.add(sc.next());
			n--;
		}
		Server s = new Server();
		s.start(port);

	}

	public void start(int port) {
		try {
			server = new ServerSocket(port);
			int counter = 0;
			System.out.println("Server Started ....on port: " + port);
			while (true) {
				counter++;
				Socket serverClient = server.accept(); // server accept the client connection request
				System.out.println(" >> " + "Client No:" + counter + " Connected!!!");

				ServerClientThread sct = new ServerClientThread(serverClient, counter); // send the request to a
																						// separate thread
				sct.start();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// to set values without console
	public void setServers(List list) {

		serverList = list;
	}

	public void stop() {
		try {
			server.close();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
}
