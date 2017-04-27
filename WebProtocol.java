
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WebProtocol {

	
	 public String processInputc(String input){
		 
		 	if(input == "INITIALISE"){
		 		return "INITIALISED";
		 	}else if(input == "broadcast"){
		 		return "BROADCAST";
		 	}else{
		 		String capitalizedSentence = input.toUpperCase() + '\n';
		 		return capitalizedSentence;
		 	}
	 }


    private int state = WAITING_FOR_USERNAME;
    private static final int INITIAL = 0;
    private static final int WAITING_FOR_USERNAME = 1;
    private static final int WAITING_FOR_PASSWORD = 2;
    private static final int LOGIN_RESPONSE = 3;
    private static final int LOGGED_IN = 4;
    private boolean loginState = false;
    private int login_counter = 3;
    private boolean lockedOut = false;
    
    private String curr_username_attempt;
    private String curr_password_attempt;
    
    
    public void setLockedOut(boolean b){
    	this.lockedOut = b;
    }
    
    public boolean getLockedOut(){
    	return lockedOut;
    }
   
    public void setLoginState(boolean bool){
    	this.loginState = bool;
    }
    public boolean getLoginState(){
    	return loginState;
    }


	private ArrayList<String> usernames = new ArrayList<String>();
	private HashMap<String, String> passwords = new HashMap<String, String>();
	
	public void populateList(){
		this.usernames.add("hans"); // falcon")
		this.passwords.put("hans", "falcon");
		this.usernames.add("yoda"); //wise
		this.passwords.put("yoda", "wise");
		this.usernames.add("vader"); //sithlord
		this.passwords.put("vader", "sithlord");
		this.usernames.add("r2d2"); //socute
		this.passwords.put("r2d2", "socute");

	}
	
	public void printList(){
		if(this.usernames != null){
			for(String s: this.usernames){
				System.out.println(s);
			}
		}
	}
	
	// need to have the credential somewhere
	// compare user input to the credentials 
	// add login state ... 
	public String getUsername(){
		return this.curr_username_attempt;
	}
	public boolean checkDetails(){
		//could probably just use hashmap for this
		String username = this.curr_username_attempt.trim();
		String password = this.curr_password_attempt.trim();
		if(this.usernames.contains(username)){
			
			if(this.passwords.get(username).equals(password)){
				return true;
			}else{
				return false;
			}
			
		}else{
			return false;
		}		
	}
	//protocol after successful login 
	public String process(String input){
		
		
		return null;
	}
	
	
	//protocol for logging in, getting logged in
	public String loginProtocol(String input){
			String output = null;
		
			if (this.state == INITIAL){
			    output = "1";
			    
				this.state = WAITING_FOR_USERNAME;
			}else if (this.state == WAITING_FOR_USERNAME){
				output = "Please input username";
				this.state = WAITING_FOR_PASSWORD;
			} else if (this.state == WAITING_FOR_PASSWORD){
				output = "Please input password";
				this.curr_username_attempt = input; 
				this.state = LOGIN_RESPONSE;
			} else if (this.state == LOGIN_RESPONSE) {
				this.curr_password_attempt = input; 
				// now check both
				if(checkDetails()){
					output = "SUCCESSFUL LOGIN";
					this.state = WAITING_FOR_USERNAME;
					this.loginState = true;
					//need to check if the user is already existing and online
		        }else{
					this.login_counter--; 
					if(login_counter == 0){
						output = "XXX";
						login_counter = 3;
						setLockedOut(true);
					}else{
						output = "PLEASE TRY AGAIN. " + this.login_counter + " ATTEMPTS REMAINING. PRESS ENTER TO CONTINUE" ;
					}
					this.state = WAITING_FOR_USERNAME;


				}
			}
			// need output to have newline so readLine() triggers
			return output + '\n';
		

	}
	
}
