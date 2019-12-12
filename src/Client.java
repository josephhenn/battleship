import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	// if I use a GUI or not
	private ClientGUI cg;
	
	// the server, the port and the username
	private String server, username;
	private int port;
	
	
	Boolean loggedIn=false;

	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
	Client(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this(server, port, username, null);
	}

	/*
	 * Constructor call when used from a GUI
	 * in console mode the ClienGUI parameter is null
	 */
	Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		// save if we are in GUI mode or not
		this.cg = cg;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(cg != null)
			cg.connectionFailed();
			
	}
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Anonymous 1500 localhost 
	 * are eqquivalent
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI id used, the GUI is informed of the disconnection
	 */
	public static void main(String[] args) {
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client username portNumber serverAddr
			case 3:
				serverAddress = args[2];
			// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if(!client.start())
			return;
		
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		// loop forever for message from the user
		while(true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				// break to do the disconnect
				break;
			}
			else if(msg.equalsIgnoreCase("INVITE")){
				System.out.println("Who do you want to invite?");
				String invitee = scan.nextLine();
				client.sendMessage(new ChatMessage(ChatMessage.INVITE, invitee));
			}
			else if(msg.equalsIgnoreCase("ACCEPT")){
				client.sendMessage(new ChatMessage(ChatMessage.ACCEPT, "Accept"));
			}
			else if(msg.equalsIgnoreCase("REJECT")){
				client.sendMessage(new ChatMessage(ChatMessage.REJECT, "Reject"));
			}
			else if(msg.contains("hit")){
				client.sendMessage(new ChatMessage(ChatMessage.HIT, msg));
			}
			else if(msg.equalsIgnoreCase("HELP")){
				client.sendMessage(new ChatMessage(ChatMessage.HELP, ""));
			}
			else if(msg.equalsIgnoreCase("SHIPS")){
				client.sendMessage(new ChatMessage(ChatMessage.SHIPS, ""));
			}
			// message WhoIsIn
			else if(msg.equalsIgnoreCase("WHOISIN")) {
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			}
			else if (msg.equalsIgnoreCase("LOGIN")){
				
				if (!client.loggedIn){
					if (client.username.equals("Anonymous")){
						System.out.println("Username:");
						String inputUserName = scan.nextLine();
						System.out.println("Password:");
						String inputUserPassword = scan.nextLine();
						Login LoginObject = new Login(inputUserName,inputUserPassword);
						client.sendMessage(new ChatMessage(ChatMessage.LOGIN,"login",LoginObject,0)); 
						//we don't really need the success code in this direction - so we will set it to zero
					}
					
				}
				else{
					System.out.println("You are already logged in...");
				}
				
				
				
			}
			else {				// default to ordinary message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		// done disconnect
		client.disconnect();	
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {  

		public void run() {
			while(true) {
				try {
					ChatMessage cm = null; //= new ChatMessage();
					Object obj = sInput.readObject();
					String msg = null;
					if (obj instanceof ChatMessage){
						 msg = ((ChatMessage) obj).getMessage();
						 cm = new ChatMessage();
						 cm = (ChatMessage) obj;
						
					}
					else if (obj instanceof String){
						msg = obj.toString();
						System.out.println(msg);
					}
					// if console mode print the message and add back the prompt
					if(cg == null) {
						if (obj instanceof ChatMessage){
							System.out.println(msg);
							
							
							if (((ChatMessage) obj).getType() == 3){
								if(((ChatMessage) obj).getSuccessCode()==1){
									loggedIn = true;
									username = ((ChatMessage) obj).getLoginObject().getLoginUserName();
									System.out.println(username + " is now logged in");
									
								}
								else{
									loggedIn=false;
									System.out.println("user is still logged in as " + username );
									
								}
								
							}
							
							
							
						 }
						System.out.print("> ");
					}
					else {		//This is for the GUI we will use later
						//GUI action
					}
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					if(cg != null) 
						cg.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
