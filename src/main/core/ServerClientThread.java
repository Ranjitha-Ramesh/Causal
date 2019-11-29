package main.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class ServerClientThread extends Thread {
	Socket serverClient;
	String clientId;
	int squre;

	ServerClientThread(Socket inSocket, int counter) {
		serverClient = inSocket;
		clientId = String.valueOf(counter);
	}

	public void run() {
		try {
			DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
			DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
			String clientMessage = "", serverMessage = "";
			// invoking connect method to update time and id
			String result = connect(String.valueOf(clientId));
			serverMessage = "From Server to Client:" + clientId + " Result of " + clientMessage + " is " + result;

			while (!clientMessage.equals("Terminate")) {
				clientMessage = inStream.readUTF();
				String[] msgs = clientMessage.split(" ");
				System.out.println("From Client-" + clientId + ": Call to :" + clientMessage);

				/**
				 * Use reflections later to fetch the available methods and call them to make it
				 * more scalable and maintainable
				 */
				if (msgs[0].equalsIgnoreCase("read")) {
					String res = read(msgs[1]);
					serverMessage = "From Server to Client:" + clientId + " Result of " + clientMessage + " is " + res;
				}
				if (msgs[0].equalsIgnoreCase("write")) {

					String res = write(msgs[1], msgs[2]);
					serverMessage = "From Server to Client:" + clientId + " Result of " + clientMessage + " is " + res;
				}
				if (msgs[0].equalsIgnoreCase("serverwriteconnect")) {
					String key = msgs[1];
					String dep = msgs[2];
					String newkey = msgs[3];
					String val = msgs[4];

					System.out.println("dep in repl-write is: " + dep);
					verifydep(dep, newkey, val);
					//setTime()
					serverMessage = "From Server to Client:" + clientId + " Result of " + clientMessage + " is "
							+ newkey;
				}
				outStream.writeUTF(serverMessage);
				outStream.flush();
			}
			inStream.close();
			outStream.close();
			serverClient.close();
		} catch (Exception ex) {
			System.out.println(ex);
		} finally {
			System.out.println("Client :" + clientId + " exit!! ");
		}
	}

	/**
	 * create the client object and his dependency to the map
	 */
	private String connect(String clientName) {

		if (DataServer.lamport_time < 0)
			DataServer.lamport_time = 0;
		DataServer.initiate_Dependency(clientName);

		// TODO Auto-generated method stub
		return "You are Connected";
	}

	private synchronized String read(String key) {
		String[] keytocheck = { clientId, key };
		String res;
		if (DataServer.dataset.containsKey(key)) {
			String[] datasetVals = DataServer.getData(key);
			String dependency = key.concat(String.valueOf(DataServer.lamport_time)).concat(datasetVals[1]);
			System.out.println("Client " + clientId + "dependency is " + dependency);
			DataServer.set_Dependency(clientId, key, dependency);
			return "Your Read value is: " + datasetVals[0];
		} else
			return "Key Not Found";
	}

	private synchronized String write(String key, String val) {
		String dcid = String.valueOf(Server.datacentreID);
		String[] vals = { val, dcid };
		String[] newpair = { key, val };
		writeReplicate(key, DataServer.get_Dependency(clientId), newpair);
		DataServer.dataset.put(key, vals);
		DataServer.clear_dependency(clientId);
		String newdep = key.concat(String.valueOf(DataServer.lamport_time)).concat(dcid);
		DataServer.set_Dependency(clientId, key, newdep);
		return "Your Write of " + val + " is committed";

	}

	private synchronized String writeReplicate(String key, String deps, String[] newpair) {
		System.out.println(Server.serverList.size());
		for (String s : Server.serverList)
			connectsend(s, key, deps, newpair);
		return "Your value is WriteReplicated ";
	}

	private void verifydep(String dep, String newkey, String val) {
		boolean ok = false;
		if (dependency_check(dep)) {
			System.out.println("dependency check passed for " + newkey);
			String[] vals = { val, String.valueOf(Server.datacentreID) };

			// to sleep before the msg u is written to third client
			// to demeonstrate the causal consistency behavior
			if (Server.datacentreID == 3 && newkey.equalsIgnoreCase("u"))
				try {
					System.out.println("Sleeping before the write of y on server 3");
					Thread.sleep(120000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			int c = DataServer.queue.size();
			DataServer.dataset.put(newkey, vals);
			System.out.println("Inserted key: " + newkey + " val: " + val);
			while (c>0  && !DataServer.queue.isEmpty() ) {
				System.out.println("Checking from queue: ");
				String[] pendlist = DataServer.queue.pop();
				verifydep(pendlist[0], pendlist[1], pendlist[2]);
				c--;
			}
		} else {
			System.out.println("Writing to queue: " + newkey);
			queue(dep, newkey, val);
		}
	}

	private boolean dependency_check(String dep) {
		System.out.println("dependency in the received repl-write: " + dep);

		boolean satisfy = true;
		if (!dep.equalsIgnoreCase("null")) {
			String[] elem_of_dep = dep.split("");
			for (int i = 0; i < elem_of_dep.length; i += 3) {
				System.out.println("Printing elements in the dataset of server: "
						+ Arrays.deepToString(DataServer.dataset.keySet().toArray()));
				if (!DataServer.dataset.containsKey(elem_of_dep[i]))
					satisfy = false;
			}
		}
		return satisfy;
	}

	private void queue(String dep, String key, String val) {
		String[] waitlist = { dep, key, val };

		DataServer.queue.add(waitlist);

	}

	/**
	 * Using an inner class to spawn another thread within, to connect to other
	 * datacenter and propagate the write
	 */
	private void connectsend(String serverList, String key, String dep, String[] newpair) {

		Thread connectDatacenter = new Thread() {
			public void run() {
				try {
					Socket socket = new Socket("127.0.0.1", Integer.valueOf(serverList));
					DataInputStream inStream = new DataInputStream(socket.getInputStream());
					DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());

					String serverthreadMessage = "serverwriteconnect", serverMessage = "";

					String writeInputs = serverthreadMessage + " " + key + " " + dep + " " + newpair[0] + " "
							+ newpair[1];
					outStream.writeUTF(writeInputs);
					outStream.flush();
					serverMessage = inStream.readUTF();
					System.out.println(serverMessage);

					outStream.close();
					outStream.close();
					socket.close();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		};
		connectDatacenter.start();
	}

}
