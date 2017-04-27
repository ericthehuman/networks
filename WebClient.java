/*
 *
 *  client for TCPClient from Kurose and Ross
 *
 *  * Usage: java TCPClient [server addr] [server port]
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class WebClient {
	
	//keep track of who is online ... 

	public static void main(String[] args) throws Exception {

		// get server address
		String serverName = "localhost";
		if (args.length >= 1)
		    serverName = args[0];
		InetAddress serverIPAddress = InetAddress.getByName(serverName);

		// get server port
		int serverPort = 6789; 
		//change above port number if required
		if (args.length >= 2)
		    serverPort = Integer.parseInt(args[1]);

		// create socket which connects to server
		final Socket clientSocket = new Socket(serverIPAddress, serverPort);

		final Thread outThread = new Thread() {
			
			@Override
			public void run() {
				System.out.println("Started...");
				
				try {
					DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
					BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
					String sentence;
					
					while(true){
						if((sentence = inFromUser.readLine()) != null){
							System.out.println("Client: " + sentence);
							outToServer.writeBytes(sentence + '\n');
						}
						
					}
				} catch (Exception e){
					
				}finally{
					
				}
			}
			
			
		};
		
		outThread.start();
		
		final Thread inThread = new Thread() {
			@Override
			public void run() {
				// Use a Scanner to read from the remote server

				try {
					BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String sentenceFromServer;
					while (true) {
						
						if((sentenceFromServer = inFromServer.readLine()) != null){
							System.out.println("From Server: " + sentenceFromServer);

						}
						
					}
				} catch (Exception e) {
//					e.printStackTrace();
				} finally {
	
				}
			};
		};
		inThread.start();
		
	/*	start comment out
		
		// get input from keyboard
		String sentence;
		BufferedReader inFromUser =
			new BufferedReader(new InputStreamReader(System.in));

		// write to server
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//		outToServer.writeBytes(sentence + '\n');

		// create read stream and receive from server
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String sentenceFromServer;
//		sentenceFromServer = inFromServer.readLine();

		// print output
	//	System.out.println("From Server: " + sentenceFromServer);

		// close client socket

		
		while(true){
			
			if((sentenceFromServer = inFromServer.readLine()) != null){
				System.out.println("From Server: " + sentenceFromServer);
				
				//locks out user for 5 seconds if the client gets the lockout message from server "XXX"
				if(sentenceFromServer.trim().equals("XXX")){
					long start = System.currentTimeMillis();
					long stop = start + 5*1000;
					while(System.currentTimeMillis() < stop){
						//user input during this time is muted (won't echo after lockout elapses)
						if((sentence = inFromUser.readLine()) != null){
							System.out.println("SORRY YOU ARE LOCKED OUT!!!");
						}
					}
				
				}
			}
			
			//sends client message to the server 
			if((sentence = inFromUser.readLine()) != null){
				System.out.println("Client: " + sentence);
				outToServer.writeBytes(sentence + '\n');
			}

			//breaks out of the program 
			if(sentence == "Q"){
				break;
			}
		}
		
		clientSocket.close();
end comment out*/
	} // end of main

} // end of class TCPClient
