package main.core;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DataServer {

	static int datacenterID = -1;
	String element = "";// datacenter data element
	int[] version;// version : timestamp, datacenterId
	static Map<String, String> dependency_list = new HashMap();// key = element, client >> value = dependent element,
																// version
	static Map dataset = new HashMap<String, String[]>();// dataset with actual key value and datacentreID where this
															// key was generated(to track the version in dependency
															// list)
	static Stack<String[]> queue = new Stack();// queue to maintain the pending writes

	// serverList
	static List<String[]> serverList = new ArrayList<String[]>();
	// server local time
	static int lamport_time = 0;

	// make them synchronized later maybe
	public static void setTime(int time) {

		lamport_time = time;
	}

	public static int getTime(int time) {

		return lamport_time;
	}

	public static void set_Dependency(String client, String element, String dependency) {
		if (dependency_list.get(client) == null)
			dependency_list.put(client, dependency);
		else
			dependency_list.computeIfPresent(client, (k, v) -> v.concat(dependency));
	}

	public static void initiate_Dependency(String client) {
		dependency_list.put(client, null);

	}

	public static String get_Dependency(String key) {
		String dep = dependency_list.get(key);
		return dep;

	}

	public static String[] getData(String key) {
		return (String[]) dataset.get(key);
	}

	public static void clear_dependency(String clientId) {
		dependency_list.replace(clientId, null);
	}

}
