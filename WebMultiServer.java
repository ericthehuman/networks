
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.*;
 
public class WebMultiServer {
	
    protected PrintWriter out;

	
	private static List<DataOutputStream> users = new ArrayList<DataOutputStream>(); //a list of sockets (don't really need this)
	private static List<User> onlineUsers = new ArrayList<User>(); //currently online users
	private static List<User> allUsers = new ArrayList<User>(); //all users, made from source file
	
	public static void populateUsers(){
		
		allUsers.add(new User(null, "hans", 0));
		allUsers.add(new User(null, "yoda", 0));
		allUsers.add(new User(null, "vader", 0));
		allUsers.add(new User(null, "r2d2", 0));

	}
	
	
    public static void main(String[] args) throws IOException {
 
    if (args.length != 1) {
        System.err.println("Usage: java KKMultiServer <port number>");
        System.exit(1);
    }
 
        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        populateUsers();
         
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
        	System.out.println("hellooo");
            while (listening) {
                new WebMultiServerThread(serverSocket.accept()).start();       
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
   

private static class WebMultiServerThread extends Thread {
    private Socket socket = null;
    private String user;
//    protected BufferedReader out;
    private DataOutputStream outToClient;
    private User thisUser;
    private boolean lockedOut = false;

   
    
    public Socket getSocket(){
    	return socket;
    }

    public WebMultiServerThread(Socket socket) {
        super("WebMultiServerThread");
        this.socket = socket;
    }
    
    public User findUserByName(User u, List<User> users){
    	
    	User found = null;
    	for (User user : users){
    		if(user.getUsername().equals(u.getUsername())){
    			found = user; 
    		}
    	}
    	return found;
    }
    
    public String joinList(List<String> list){
    	
    	String joined = "";
    	
    	for (String s:list){
    		joined = "" + s + " ";
    	}
    	
    	return joined;
    }
    
    public void run() {

        try 
        		
         {
        	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));	//input stream from client 
        	outToClient = new DataOutputStream(socket.getOutputStream()); //output stream
        	String username = "";
			String outputLine;
			WebProtocol wb = new WebProtocol();
			wb.populateList();
			outputLine = wb.loginProtocol(null);
			outToClient.writeBytes(outputLine);

			String clientSentence;

		while(true){

				while(true){
				
				//for logged in state and all commands are available 
				if(wb.getLoginState()){
					
					//times since last command 
					long start = System.currentTimeMillis();
					long stop = start + 100*1000;  
					
					while(true){
						
						//read in client messages
						//add seconds so user is not logged out every time they enter a command
						clientSentence = inFromClient.readLine();										
						if(clientSentence != null && System.currentTimeMillis() < stop){		

							stop = System.currentTimeMillis() + 100*1000;
							outputLine = wb.processInputc(clientSentence);
							
							//command key word 
							String[] toParse = outputLine.trim().split(" ");
							List<String> parse = new ArrayList<String>(Arrays.asList(toParse));
							String command = parse.get(0);
							System.out.println(parse);
							if(command.equals("BROADCAST")){
								
								  parse.remove(0);
								  String message = joinList(parse);
				                  for (DataOutputStream user : users) {
				                        user.writeBytes("BROADCAST " + message + '\n');
				                    }
							}else if(command.equals("MESSAGE")){
								String userReceiver = parse.get(1);
								parse.remove(0);	//remove "message"
								parse.remove(0);	//remove "user receiver"
								boolean userOnline = false;
								String message = joinList(parse);
								
								//send message online
								String messageToSend = username + ": " + message + '\n';

								for (User user: onlineUsers ){

									System.out.println(user);
									System.out.println(userReceiver);
									if(user.getUsername().equals(userReceiver.toLowerCase())){

										userOnline = true;
										user.getOutputStream().writeBytes(messageToSend);
									}
												
								}
								
								//send message offline
								if(userOnline == false){
									for (User user: allUsers){
										
										if(user.getUsername().equals(userReceiver.toLowerCase())){
											user.addOfflineMessage(messageToSend);
											
										}
									}
								}
								
							}else if(command.equals("WHOELSE")){
								String whoelse = "";
								
								for (User user: onlineUsers){
									if(!user.getOutputStream().equals(outToClient)){
										//add to string
										whoelse = whoelse + user.getUsername() + " ";
									}
								}
								
								outToClient.writeBytes(whoelse + '\n');
							}else if(command.equals("WHOELSESINCE")){
								long currTime = System.currentTimeMillis();
								int secondsSince = Integer.parseInt(parse.get(1));
								
								//whoelesesince command 
								//go through online users ...
								String whoelse = "";
								for(User u:allUsers){
									//check if the time matches up 
									
									if(u.getFirstLoginTime() != 0 && currTime - secondsSince < u.getFirstLoginTime()){
										whoelse =  whoelse + u.getUsername() + " ";
									}else if(u.getLastLogoutTime() == 0 || currTime - secondsSince < u.getLastLogoutTime()){
										whoelse = whoelse + u.getUsername() + " ";
									}
									
								}
								
								if(whoelse.equals("")){
									outToClient.writeBytes("No one has been online in this time frame" + '\n');
								}else{
									outToClient.writeBytes(whoelse + '\n');

								}
								
								
								
							} else if (command.equals("BLOCK")){
								//block command
							}else if (command.equals("UNBLOCK")){
								
							}else if(command.equals("LOGOUT")){
								break;
							}else{
								outToClient.writeBytes("ERROR" + '\n');
							}
							
						}else{
							break;
						}
					}
					System.out.println("timeout");
					outToClient.writeBytes("You have been logged out" + '\n');
					wb.setLoginState(false);
					
					
					User userLoggedOut = findUserByName(thisUser, allUsers);
					userLoggedOut.setLastLogoutTime(System.currentTimeMillis());
					onlineUsers.remove(userLoggedOut);
					users.remove(userLoggedOut.getOutputStream());
					
				//user login protocol
				}else{
					
					//receive user input from client 
					clientSentence = inFromClient.readLine(); 
					if(clientSentence != null && wb.getLockedOut() == false){					
						outputLine = wb.loginProtocol(clientSentence);
						
						//successful login setup 
						if(outputLine.trim().equals("SUCCESSFUL LOGIN")){
							
							users.add(outToClient);
							username = wb.getUsername();
							long loginTime = System.currentTimeMillis();
							User thisUser = new User(outToClient, username, loginTime);
							onlineUsers.add(thisUser);
						
							User userFromAllUsers = findUserByName(thisUser, allUsers);
							userFromAllUsers.setFirstLoginTime(loginTime);
							
							//retrieve messages received while offline
							for(String m : userFromAllUsers.offlineMessages){
								outToClient.writeBytes(m);
							}
						}
						
						// does this do anything though
						outToClient.writeBytes(outputLine);		
					}else{
						outToClient.writeBytes("SORRY YOU ARE LOCKED OUT" + '\n');
					}
				}

			}
		}
            
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	
        	//remove from online users 
            if (outToClient != null) {
                users.remove(outToClient);
            }
            if (thisUser != null){
            	onlineUsers.remove(thisUser);
            }
            
            User userLoggedOut = findUserByName(thisUser, allUsers);
            if(userLoggedOut != null){
				userLoggedOut.setLastLogoutTime(System.currentTimeMillis());
            }
            try {
            	socket.close();
            } catch (IOException e){
            	
            }
        }
    }
}
    
    
    
}

