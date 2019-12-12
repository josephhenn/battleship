import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	private ClientThread player1 = null;
	private ClientThread player2 = null;
	private ArrayList<Ship> p1 = new ArrayList<Ship>();
	private ArrayList<Ship> p2 = new ArrayList<Ship>();
	
	private CopyOnWriteArrayList<User> userData;

	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
		this(port, null);
	}
	
	public Server(int port, ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		
		//Read all users from the uses file
		
		userData = Globals.readUsersFromFile(Globals.USERS_INPUT_FILE); //the function and the file name are all in Globals.
				
	}
	
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	/*
	 *  to broadcast a message to all Clients
	 */
	private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// display message on console or GUI
		if(sg == null)
			System.out.print(messageLf);
		else
			sg.appendRoom(messageLf);     // append in the room window
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;
		boolean inGame;

		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			inGame = false;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
				
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the message part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.INVITE:
					player1 = this;
					boolean found = false;
					for(int i = 0; i < al.size(); i++) {
						if(al.get(i).username.equalsIgnoreCase(message)){
							if(this.username.equals(al.get(i).username)){
								found = true;
								writeMsg("You cannot invite yourself.");
								break;
							}
							if(al.get(i).inGame){
								found = true;
								writeMsg("That player is already in a game.");
								break;
							}
							player2 = al.get(i);
							writeMsg("Sent invite to " + message + ".");
							found = true;
							al.get(i).writeMsg("Do you want to accept the game invite from " + username + "? (ACCEPT/REJECT)");
							break;
						}
					}
					if(!found){
						writeMsg("There are no players with that name.");
					}
					break;
				case ChatMessage.ACCEPT:
					if(username.equals(player2.username)){
						player1.writeMsg(player2.username + " accepted the invite!");
						player1.inGame=true;
						player2.inGame=true;
						startGame();
					}
					else{
						broadcast(username + ": " + message);
					}
					break;
				case ChatMessage.REJECT:
					if(username.equals(player2.username)){
						player1.writeMsg(player2.username + " rejected the invite.");
						player1 = null;
						player2 = null;
					}
					else{
						broadcast(username + ": " + message);
					}
					break;
				case ChatMessage.HIT:
					String coord="";
					if(this.equals(player1) || this.equals(player2)){
						if(message.length()==8){
							coord = message.substring(5,7);}
						else if(message.length()==9){
							coord = message.substring(5,8);}
						else{
							writeMsg("Invalid Coordinates");
							break;}
					broadcast(username + " tried " + coord);
					playTurn(coord, this);
					}
					else{
						writeMsg("You are not playing a game.");
					}
					break;
				case ChatMessage.HELP:
					writeMsg("To make a move type 'hit (A#)'\nTo see a list of users type 'WHOISIN'\nTo see your remaining pieces type 'Ships'\nTo logout type 'logout'");
					break;
				case ChatMessage.SHIPS:
					if(this.equals(player1))
						printShips(p1, player1);
					else if(this.equals(player2))
						printShips(p2, player2);
					else
						writeMsg("You do not have any ships");
					break;	
				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				case ChatMessage.LOGIN:
					
					try {
						display("Server received Login request by user " + cm.getLoginObject().getLoginUserName());
						String sendMsg = "Server: received Your Login request..Please wait.";
						sOutput.writeObject(sendMsg);
						//check if the user is already logged in
						//You should do this yourself - will leave for the student to do
						
						//call function to authenticate user and pass the user list (userData) and the clinet 
						//message object which would have a login object in this case
						User userLoggingIn = Globals.authenticateUser(userData,cm.getLoginObject()); 
						ChatMessage cmLoginResult=null;
						if (!(userLoggingIn==null)){
							//did not use "equals" so that we don't get a nullpointerexception in the case when the function
							//returns a null meaning it could not find a valid username and password.
							username = cm.getLoginObject().getLoginUserName();
							sendMsg = "Server:Login Successfull...";
							cmLoginResult = new ChatMessage(3,sendMsg,cm.getLoginObject(),1); //1 for successful login
							sOutput.writeObject(cmLoginResult);
							
							//and the server will send the checkoutlist to the client when the user signs on if there is any
							display("Server logged in user " + username); 
							//make a list of checked out books for the client who requested it only
							
						}
						else{
							sendMsg = "invalid user or password";
							cmLoginResult = new ChatMessage(3,sendMsg,0); //1 for successful login
							sOutput.writeObject(cmLoginResult);
						}
							
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
	private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
	
	private void startGame(){
		Game game = new Game();
		broadcast("\nWelcome to the Battleship Game!\nType 'HELP' for help!");
		broadcast("There are " + game.numPieces() + " pieces on the board.");
		broadcast("The board size is " + game.boardSize() + " by " + game.boardSize() + ".");
		game.setup();
		p1 = game.player1List();
		p2 = game.player2List();
		printShips(p1, player1);
		printShips(p2, player2);
		Random rand = new Random();
		Integer first = rand.nextInt(2);
		if(first == 0){
			broadcast(player1.username + " goes first.");
			player1.writeMsg("It is your Turn.");}
		else{
			broadcast(player2.username + " goes first.");
			player2.writeMsg("It is your Turn.");}
		
	}
	
	private void printShips(ArrayList<Ship> list, ClientThread p){
		p.writeMsg("Your ships are located at:");
		for(Ship l:list){
			ArrayList<String> loc = l.getLocation();
			for(String s:loc){
				p.writeMsg(s);
			}
		}
	}
	
	private void playTurn(String guess, ClientThread p){
		String result="miss";
		if(p.equals(player1)){	
			guess = guess.toUpperCase();
			for(int i=0; i<p2.size(); i++){
				result = p2.get(i).checkGuess(guess);
				if(result.equals("Sunk")){
					broadcast(p.username + " sunk opponent's ship!");
					p2.remove(i);
					break;
				}
				else if(result.equals("Hit")){
					break;
				}
			}
			if(result != "Sunk"){broadcast(result);}		
		}
		else{
			guess = guess.toUpperCase();
			for(int i=0; i<p1.size(); i++){
				result = p1.get(i).checkGuess(guess);
				if(result.equals("Sunk")){
					broadcast(p.username + " sunk opponent's ship!");
					p1.remove(i);
					break;
				}
				else if(result.equals("Hit")){
					break;
				}
			}
			if(result != "Sunk"){broadcast(result);}
			
		}
		if(p2.isEmpty()){
			broadcast(player1.username + " wins!");
			player1.inGame=false;
			player2.inGame=false;
			endGame();}
		else if(p1.isEmpty()){
			broadcast(player2.username + " wins!");
			player1.inGame=false;
			player2.inGame=false;
			endGame();}
		else if(p.equals(player1))
			player2.writeMsg("It is your turn.");
		else if(p.equals(player2))
			player1.writeMsg("It is your turn.");
	}
	
	private void endGame(){
		p1.clear();
		p2.clear();
	}
}

