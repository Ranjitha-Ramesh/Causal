package test.causal;

import org.junit.BeforeClass;
//import org.junit.Ignore;
import org.junit.Test;

import main.core.Client;
import main.core.Server;
//import main.core.SplitFile;
import main.core.ServerClientThread;
//import main.core.MergeFile;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class SocketTest {

	private static int port = 8585;

//	@BeforeClass
	public static void start() throws InterruptedException, IOException {

		/*
		 * ServerSocket s = new ServerSocket(0); port = s.getLocalPort(); s.close();
		 */
		List servers = new ArrayList();
		servers.add(5454);
//		Executors.newSingleThreadExecutor().submit(() -> new Server().start(8888));
		//setServers(servers);
		Thread.sleep(500);
	}
	
   //@Test
	/*public void givenClient1_whenServerResponds_thenCorrect() {
		Client client = new Client();
		client.startConnection("127.0.0.1",port);
//		String msg1 = client.sendMessage("Hello");
//		String msg2 = client.sendMessage("its me client 1");
//		String terminate = client.sendMessage(".");
		try {
			String msg1 = client.sendServer("write", "x lost");
			String msg2 = client.sendServer("read","x");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		assertEquals(msg1, "Hello");
//		assertEquals(msg2, "its me client 1");
//		assertEquals(terminate, "C ya again");
		client.stopConnection();

	}*/

	/*
	 * public void givenClient2_whenServerResponds_thenCorrect() { Client client =
	 * new Client(); client.startConnection("127.0.0.1", port); String msg1 =
	 * client.sendMessage("Hello"); String msg2 =
	 * client.sendMessage("its me client 2"); String terminate =
	 * client.sendMessage(".");
	 * 
	 * assertEquals(msg1, "Hello"); assertEquals(msg2, "its me client 2");
	 * assertEquals(terminate, "C ya again"); client.stopConnection(); }
	 * 
	 * //@Test public void givenClient3_whenServerResponds_thenCorrect() { Client
	 * client = new Client(); client.startConnection("127.0.0.1", port); String msg1
	 * = client.sendMessage("Hello"); String msg2 =
	 * client.sendMessage("its me client 3"); String terminate =
	 * client.sendMessage(".");
	 * 
	 * assertEquals(msg1, "Hello"); assertEquals(msg2, "its me client 3");
	 * assertEquals(terminate, "C ya again"); client.stopConnection(); }
	 * 
	 * //These files ae automatically split //@Test public void RegisterwithFiles()
	 * { Client c1 = new Client(); Client c2 = new Client();
	 * 
	 * c1.startConnection("127.0.0.1", 5555); c2.startConnection("127.0.0.1", 5555);
	 * 
	 * String msg1 = c1.register("127.0.0.1", "5001", "test.pdf", "50851"); String
	 * msg2 = c2.register("127.0.0.1", "5011", "best.pdf", "151552");
	 * System.out.println("msg"+msg1); System.out.println(msg2); assertEquals(msg1,
	 * "[test.pdf0.splitPart 127.0.0.1 5001, test.pdf1.splitPart 127.0.0.1 5001, test.pdf2.splitPart 127.0.0.1 5001]"
	 * ); assertEquals(msg2,
	 * "[best.pdf0.splitPart 127.0.0.1 5011, best.pdf1.splitPart 127.0.0.1 5011, best.pdf2.splitPart 127.0.0.1 5011]"
	 * );
	 * 
	 * c1.stopConnection(); c2.stopConnection(); }
	 * 
	 * //Testing fileLocationRequest(String) after registration //@Test public void
	 * RequestFilesList() { Client c1 = new Client(); Client c2 = new Client();
	 * Client c3 = new Client(); c1.startConnection("127.0.0.1", 5555);
	 * c2.startConnection("127.0.0.1", 5555); c3.startConnection("127.0.0.1", 5555);
	 * 
	 * c1.register("127.0.0.1", "5001", "test.pdf", "50851");
	 * c2.register("127.0.0.1", "5011", "best.pdf", "151552"); List<String> msg1 =
	 * c3.fileListRequest();
	 * 
	 * String file = msg1.toString(); System.out.println(file); assertEquals(file,
	 * "[test.pdf:50851, best.pdf:151552]"); //assertEquals(file[1],
	 * "best.pdf:151552");
	 * 
	 * c1.stopConnection(); c2.stopConnection(); c3.stopConnection(); }
	 * 
	 * //Testing File Locations //@Test public void RequestFileLocation() { Client
	 * c1 = new Client(); Client c2 = new Client(); Client c3 = new Client(); Client
	 * c4 = new Client();
	 * 
	 * c1.startConnection("127.0.0.1", 5555); c2.startConnection("127.0.0.1", 5555);
	 * c3.startConnection("127.0.0.1", 5555); c4.startConnection("127.0.0.1", 5555);
	 * c1.register("127.0.0.1", "5001", "test.pdf", "50851");
	 * c2.register("127.0.0.1", "5011", "best.pdf", "151552"); c3.fileListRequest();
	 * 
	 * //c4.requestFile("best.pdf"); //[test.pdf0.splitPart 127.0.0.1 5001,
	 * test.pdf1.splitPart 127.0.0.1 5001, test.pdf2.splitPart 127.0.0.1 5001]
	 * List<String> msg1 = c4.fileLocationRequest("best.pdf"); //String file =
	 * msg1.toString(); for(String s:msg1) System.out.println(s); assertEquals(msg1,
	 * "[test.pdf0.splitPart 127.0.0.1 5001, test.pdf1.splitPart 127.0.0.1 5001, test.pdf2.splitPart 127.0.0.1 5001]"
	 * ); //assertEquals(file[1], "best.pdf:151552");
	 * 
	 * c1.stopConnection(); c2.stopConnection(); c3.stopConnection();
	 * c4.stopConnection(); }
	 * 
	 * 
	 * //Download the chunks mentioned by the fileLocationsRequest
	 * 
	 * @Test public void RequestFileChunkDownloads() { Client c1 = new Client();
	 * Client c2 = new Client(); Client c3 = new Client(); Client c4 = new Client();
	 * Client c5 = new Client(), c6 = new Client(); c1.startConnection("127.0.0.1",
	 * 5555); c2.startConnection("127.0.0.1", 5555); c3.startConnection("127.0.0.1",
	 * 5555); c4.startConnection("127.0.0.1", 5555); c5.startConnection("127.0.0.1",
	 * 5555); //c6.startConnection("127.0.0.1", 5555);; c1.register("127.0.0.1",
	 * "5001", "test.pdf", "50851"); c2.register("127.0.0.1", "5011", "best.pdf",
	 * "151552"); c3.fileListRequest();
	 * 
	 * //c4.requestFile("best.pdf"); //[test.pdf0.splitPart 127.0.0.1 5001,
	 * test.pdf1.splitPart 127.0.0.1 5001, test.pdf2.splitPart 127.0.0.1 5001]
	 * List<String> msg1 = c4.fileLocationRequest("test.pdf"); // c6.start(5001);
	 * for(String s:msg1) { System.out.println(s); String[] chunkDetails =
	 * s.split(" ");
	 * 
	 * // c5.requestFileChunk(chunkDetails); } MergeFile.mergeFile();
	 * //assertEquals(file, "[test.pdf:50851, best.pdf:151552]");
	 * //assertEquals(file[1], "best.pdf:151552");
	 * 
	 * c1.stopConnection(); c2.stopConnection(); c3.stopConnection();
	 * c4.stopConnection(); c5.stopConnection();
	 * 
	 * }
	 * 
	 * //Test files //@Test
	 * 
	 * public void splitfiles() {
	 * 
	 * //File f =new File(); String s=
	 * "C:\\Users\\ranji\\eclipse-workspace\\p2p\\src\\main\\socket\\test.pdf";
	 * //SplitFile splitf = new SplitFile(); List<String> news; try { //news =
	 * SplitFile.splitFile(s); for(String l :news) System.out.println(l); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * 
	 * //@Test public void MergeFiles() {
	 * 
	 * MergeFile.mergeFile(); }
	 * 
	 * //@Test public void fileLocationRequest() { Client c1 = new Client();
	 * 
	 * 
	 * List<String> lf = c1.fileLocationRequest("best.pdf");
	 * 
	 * }
	 */
}
