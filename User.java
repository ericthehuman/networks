import java.io.DataOutputStream;
import java.util.ArrayList;

public class User {

	DataOutputStream outputStream; 
	String userName;
	String password;
	long firstLoginTime = 0;
	long lastLogoutTime = 0;
	ArrayList<String> offlineMessages = new ArrayList<String>(); //should be empty unless it fills up ... 
	ArrayList<String> blockedBy = new ArrayList<String>();
	
	public User(DataOutputStream outputStream, String userName, long time){
		this.outputStream = outputStream;
		this.userName = userName;
		this.firstLoginTime = time;
		
	}
	
	public void setFirstLoginTime(long time){
		firstLoginTime = time;
	}
	
	public long getFirstLoginTime(){
		return firstLoginTime;
	}
	
	public long getLastLogoutTime(){
		return lastLogoutTime;
	}

	public void setLastLogoutTime(long time){
		lastLogoutTime = time;
	}
	
	public String getUsername(){
		return userName;
	}
	
	public void addOfflineMessage(String m){
		offlineMessages.add(m);
	}
	
	public DataOutputStream getOutputStream(){
		return outputStream;
	}
}
