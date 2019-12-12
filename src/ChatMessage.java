

import java.io.*;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class ChatMessage implements Serializable {



	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary message
	// LOGOUT to disconnect from the Server
	public static 	final 	int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, LOGIN=3, INVITE=4, ACCEPT=5, REJECT=6, HIT=7, HELP=8, SHIPS=9;
	private int 	type;
	private String 	message;
	private Login 	loginObject;
	private int 	successCode;
	
	
	public ChatMessage(){} //default constructor
	// constructor - message
	public ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	//ChatMessage object with a login 
	public ChatMessage(int type,String message,  Login lo, int successCode){ //for Login
			
			this.type = type;
			this.message = message;
			//instantiate a new LoginObject from the lo object that got passed from Client
			loginObject = new Login(lo.getLoginUserName(),lo.getLoginPassword()); 
			this.successCode = successCode;
			
			
	}
	
	//ChatMessage with a message and a successCode
	public ChatMessage(int type, String message, int successCode){
			this.type = type;
			this.message = message;
			this.successCode = successCode;
	}
	
	// getters
	public int getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
	
	public Login getLoginObject() {
		return loginObject;
	}
	public int getSuccessCode() {
		return successCode;
	}
	public void setSuccessCode(int successCode) {
		this.successCode = successCode;
	}
	
	
	ChatMessage getFullBookMessageObject(){
		return this;
	}
}
