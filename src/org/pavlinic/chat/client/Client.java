package org.pavlinic.chat.client;

import java.net.*;
import java.io.*;
import java.util.*;

import org.pavlinic.chat.PacketHandler;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	// if I use a GUI or not
	private ClientGUI isGUI;
	
	// the server, the port and the username
	private String server, username;
	private int port;
	private boolean validUsername;	// verifies that the chosen username is okay

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
	 * in console mode the ClientGUI parameter is null
	 */
	Client(String server, int port, String username, ClientGUI isGUI) {
		// save if we are in GUI mode or not
		this.isGUI = isGUI;
		this.server = server;
		this.port = port;
		// List of disallowed characters
		//String[] illegalChars = {"@", "%", "+","#"};
		
	    //for(int i = 0; i < illegalChars.length; i++)
	    //{
	        /*if(username.contains(illegalChars[i]))	// faulty code at the moment; will null server
	        {
	        	// invalid username
	        	display("Invalid username. Username cannot contain character(s): " + illegalChars[i].toString());
	            return;
	        }*/
	        if (username.equalsIgnoreCase("console") || username.contains("@") || username.contains("+") ||	 
	        		username.contains("&") || username.contains("~") || username.contains("#") || 
	        		username.length() > 16) {	// ugly method
	        	display("Invalid username. Names cannot be longer than 16 characters or contain the ");
	        	display("word \"Console\" or the following characters: @ + & ~ #");
	        	display("Change your username and try again.");
	        	validUsername = false;
	        	return;
	        }
		    else
		    {
		    	validUsername = true;
				this.username = username;
		    }
	    //}
		// TODO: Implement password
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		if(validUsername) {	// don't even bother connecting if the name isn't valid
			// try to connect to the server
			display("Connecting to server...");
			try {
				socket = new Socket(server, port);
			} 
			// if it failed not much I can so
			catch(Exception ec) {
				display("Error connecting to server: " + ec);
				return false;
			}
			
			String msg = "Connection accepted " + socket.getInetAddress() + ": " + socket.getPort();
			display(msg);
			// TODO: Get MOTD from server
			
			if(isGUI == null)	// this means the text won't show up in GUI mode
				System.out.println("Should you wish to disconnect, you can type /logout at anytime.");
		
			/* Creating both Data Stream */
			try
			{
				sInput  = new ObjectInputStream(socket.getInputStream());
				sOutput = new ObjectOutputStream(socket.getOutputStream());
			}
			catch (IOException eIO) {
				display("Exception creating new input/output Streams: " + eIO);
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
				display("Exception establishing connection to server: " + eIO);
				disconnect();
				return false;
			}
			// success we inform the caller that it worked
			return true;
		}
		else
			return false;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if(isGUI == null)
			System.out.println(msg);        // println in console mode
		else
			isGUI.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(PacketHandler msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception sending message to server: " + e);
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
		if(isGUI != null)
			isGUI.connectionFailed();
			
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
	 * If the username is not specified "Guest" + random number is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Guest### 1500 localhost 
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI id used, the GUI is informed of the disconnection
	 */
	public static void main(String[] args) {
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		// generate a random number for the guest's name
		Random r = new Random();
		int randint = r.nextInt(50);
		String userName = "Guest" + randint;

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
		while (true) {
			//System.out.print("> ");	// what the line appears like to the user in no-gui mode
			// read message from user
			String msg = scan.nextLine();
			
			// These act as client-sided commands for the text-only client
			if (msg.equalsIgnoreCase("/commands")) {
				listCommands();
			}
			else if (msg.equalsIgnoreCase("/logout")) {	// logout
				client.sendMessage(new PacketHandler(PacketHandler.LOGOUT, ""));
				// break out of loop to do the disconnect
				break;
			}
			else if (msg.equalsIgnoreCase("/whoisin") || msg.equalsIgnoreCase("/userlist")) {	// list users
				client.sendMessage(new PacketHandler(PacketHandler.LISTUSERS, ""));				
			}
			else if (msg.equalsIgnoreCase("/quit")) {
				System.exit(0);
			}
			else if (msg.trim().equalsIgnoreCase("")) {
				// we don't want the client to be able to send blank messages so do nothing
			}
			else {	// default to ordinary message
				client.sendMessage(new PacketHandler(PacketHandler.MESSAGE, msg));
			}
		}
		// disconnect
		client.disconnect();
		System.out.println("Disconnected from server. Type /connect to reconnect or /quit to exit.");
		// allow user to reconnect via this command ONLY if they aren't connected
		String msg = scan.nextLine();
		if (msg.startsWith("/connect")) {
			main(args);
		}
		else if (msg.equalsIgnoreCase("/commands")) {
			listCommands();
		}
		else if (msg.equalsIgnoreCase("/quit")) {
			System.exit(0);
		}
	}

	static void listCommands() {
		System.out.println("== Client-sided commands ==");
		System.out.println("The following commands affect your client only:");
		System.out.println("	- /connect ~ connects to the server (must be disconnected)");
		System.out.println("	- /commands ~ lists client-side commands (this command)");
		System.out.println("	- /logout ~ disconnect from the server");
		System.out.println("	- /userlist ~ list connected users");
		System.out.println("For server commands, try typing /help instead.");
	}
	
	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					// if console mode print the message and add back the prompt
					if(isGUI == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						isGUI.append(msg);
					}
				}
				catch(IOException e) {
					display("Server has closed the connection: " + e);
					if(isGUI != null) 
						isGUI.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}

