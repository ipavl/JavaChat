/* 
 * Server.java
 * 
 * This is the main class for the server and contains all of the methods that are
 * needed to allow clients to connect and to manage messages. It cannot be run as
 * a GUI by itself without the aid of ServerGUI.java.
 */

package org.pavlinic.chat.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.pavlinic.chat.PacketHandler;

public class Server {
	static String sVersion = "91";
	static String compileDate = "May 24, 2013";
	
	static int minClientVer = 70;     // the minimum version clients must be running to connect
	
	// a unique ID for each connection
	private static int clientID;
	
	// an ArrayList to keep the list of the Client
	static ArrayList<ClientThread> clientList;
	
	// if I am in a GUI
	private static ServerGUI isGUI;
	
	// to display time
	public static SimpleDateFormat dateFormat;
	
	// the port number to listen for connection
	public static int port;
	
	// the boolean that will be turned off to stop the server
	public static boolean isServerRunning;
	
	// the boolean that will check to see if the chat is mode +m
	public static boolean isRoomModerated = false;
	
	/*
	 * To log server events to file.
	 */
	static void logEvent(String output) {
		try {
			FileWriter fw = new FileWriter ("server.log", true);	// (file, append)
			fw.write(dateFormat.format(new Date()) + " " + output + "\n");	// appends the string to the file + new line
			fw.close();  // close the file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/*
	 * To log chat messages to file.
	 */
	static void logChat(String output) {
		try {
			FileWriter fw = new FileWriter ("server.log", true);	// (file, append)
			fw.write(dateFormat.format(new Date()) + " " + output);	// appends the string to the file
			fw.close();  // close the file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/*
	 *  server constructor that receives the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
		this(port, null);
	}
	
	public Server(int port, ServerGUI isGUI) {
		// GUI or not
		Server.isGUI = isGUI;
		
		// the port
		Server.port = port;
		
		// to display [MMM-dd HH:mm:ss] (24-hr)
		dateFormat = new SimpleDateFormat("[MMM-dd HH:mm:ss]");
		
		// to display [MMM-dd HH:mm:ss a] (12-hr)
		//sdf = new SimpleDateFormat("[MMM-dd hh:mm:ssa]");
		
		// ArrayList for the Client list
		clientList = new ArrayList<ClientThread>();
	}
	
	public void start() {
		isServerRunning = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);
			
			// load permissions
			PermissionsHandler.initPermissions();
			
			// format message saying we are waiting
			display("Server listening on port " + port + ".");
			
			// infinite loop to wait for connections
			while(isServerRunning) 
			{	
				Socket socket = serverSocket.accept();  	     // accept connection
				
				// if I was asked to stop
				if(!isServerRunning)
					break;
				
				ClientThread thread = new ClientThread(socket);  // make a thread of it
				clientList.add(thread);							 // save it in the ArrayList
				thread.start();
			}
			
			// Server was asked to stop
			try {
				display("Server shutting down...");
				serverSocket.close();
				for(int i = 0; i < clientList.size(); ++i) {
					ClientThread tc = clientList.get(i);
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
		
		// something went wrong
		catch (IOException e) {
            String msg = "exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
    /*
     * For the GUI to stop the server
     */
	protected void stop() {
		isServerRunning = false;
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
	static void display(String msg) {
		String event = msg;
		if(isGUI == null)
			System.out.println(event);
		else
			isGUI.appendEvent(event + "\n");
		logEvent(event);	// log to file
	}
	
	/*
	 *  Broadcast a message to all clients
	 */
	public static synchronized void broadcast(String message) {
		String messageLf = message + "\n";
		
		// display message on console or GUI
		if(isGUI == null)
			System.out.print(messageLf);
		else
			isGUI.appendRoom(messageLf);     // append in the room window
		logChat(messageLf);	// log to file
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = clientList.size(); --i >= 0;) {
			ClientThread ct = clientList.get(i);
			// try to write to the Client, if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				clientList.remove(i);
				display("Disconnected client " + ct.username + " removed from list.");
			}
		}
	}

	// for a client who logs off using the LOGOUT message
	synchronized static void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < clientList.size(); ++i) {
			ClientThread ct = clientList.get(i);
			// found it
			if(ct.id == id) {
				clientList.remove(i);
				return;
			}
		}
	}
	
	/*
	 *  To run as a console application: 
	 * > java Server
	 * > java Server [portNumber]
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

	/* One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		
		// my unique id (easier for disconnection)
		int id;
		
		// the username of the Client
		String username;
		
		// the password from the client
		String password;
		
		// the client version
		int cVersion;
		
		// the only type of message a will receive
		PacketHandler packet;
		
		// the connection date
		String logonDate;
		
		// user verification booleans
        boolean isRegistered = false;
		boolean isIdentified = false;
		
		boolean isValidVersion = true;
		boolean isValidUsername = true;
		boolean isNameFree = true;
		
		int authResponse = -1;
		
		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++clientID;
			this.socket = socket;
			
			/* Creating both Data Streams */
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				
				// read the username, password, and version of the client
				username = (String) sInput.readObject();
				password = (String) sInput.readObject();
				//cVersion = (int) sInput.readObject();   // TODO: fix issue here when compiling on JDK 1.6
				
				// Do some preliminary checks
				if(!LoginHandler.isValidVersion(cVersion, minClientVer)) {    // client version check
		            writeMsg("Outdated client! Please update your client and try connecting again.\n");
		            display("Disconnecting user: " + username + " (outdated client: " + cVersion + ")");
		            isValidVersion = false;
				}
				
				if(!LoginHandler.isValidUsername(username)) {   // valid username check
		            writeMsg("Invalid username. Names cannot be longer than 16 characters or contain the word \"Console\" or\n");
		            writeMsg("the following characters: @ + & ~ #\n");
		            writeMsg("Change your username and try again.\n");
		            display("Disconnecting user: " + username + " (invalid username)");
		            isValidUsername = false;
				}
				
				if(!LoginHandler.isNameFree(username)) {    // free username check
				    writeMsg("The username you specified is already in use.\n");
				    isNameFree = false;
				}
	            
	            // Stop if there's a problem.
	            if (!isValidVersion || !isValidUsername || !isNameFree) {
	                return;
	            }

				// set the thread's name equal to the user's (easier to interact with)
				this.setName(username);
				
				// Check if the chosen username is registered, and verify our password if necessary
				writeMsg("Logging in... \n");
				authResponse = LoginHandler.authenticate(username, password);
				
			    if (authResponse == -1) {
			        writeMsg("The login server returned an invalid response.\n");
			        return;
			    } else if (authResponse == 0) {
			        writeMsg("This name is available! Register it with /account register <password>\n");
			        isRegistered = false;
			    } else if (authResponse == 1) {
                    writeMsg("Failed to login successfully. Please check your username and password.\n");
                    writeMsg("If you do not have an account, that name is likely registered already.\n");
			        isRegistered = true;
			        isIdentified = false;
			        return;
			    } else if (authResponse == 2) {
			        writeMsg("Successfully identified as " + username + "\n");
			        isRegistered = true;
			        isIdentified = true;
			    }
			    
			    // Send the MOTD to the client
			    writeMsg(LoginHandler.getMOTD());
				
			    if (isIdentified || !isRegistered) {
					broadcast(username + " connected.");
					if (PermissionsHandler.isBanned(username))
						broadcast(socket.getInetAddress().toString() + " sets mode: +b " + username);
					else if (PermissionsHandler.isOperator(username))
						broadcast(socket.getInetAddress().toString() + " sets mode: +o " + username);
					else if (PermissionsHandler.isVoiced(username))
						broadcast(socket.getInetAddress().toString() + " sets mode: +v " + username);
					else if (PermissionsHandler.isAdministrator(username))
						broadcast(socket.getInetAddress().toString() + " sets mode: +A " + username);
			    }
			}
			catch (IOException e) {
				display("Exception creating new input/output streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
		        // have to catch ClassNotFoundException
	            // but I read a String, I am sure it will work
			}
			
            logonDate = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean isServerRunning = true;
			
			if(isValidVersion && isValidUsername) {
    			if (isIdentified || !isRegistered) {
    				while(isServerRunning) {
    					// read a String (which is an object)
    					try {
    						packet = (PacketHandler) sInput.readObject();
    					}
    					catch (IOException e) {
    						//display(username + " caused exception reading streams: " + e);
    						broadcast(username + " disconnected (" + e + ").");
    						break;
    					}
    					catch(ClassNotFoundException e2) {
    						break;
    					}
    					// the message part of the ChatMessage
    					String message = packet.getMessage();
    	
    					// Switch on the type of message receive
    					switch(packet.getType()) {
    						case PacketHandler.MESSAGE:
        					    if (!PermissionsHandler.isBanned(username)) {		                    // ignore banned users
        					        if (message.equalsIgnoreCase("/version"))
        					            writeMsg("This server is running JChat build " + sVersion + 
        					                    " compiled on " + compileDate + "\n");
        					        else if (message.startsWith("/") && message.length() > 1)	        // command
        					            CommandHandler.processCommand(username, message.substring(1));
        					        else	                                                            // message
        					        {
        					            if (PermissionsHandler.isOperator(username))	                // operator
        					                broadcast("<@" + username + "> " + message);
        					            else if (PermissionsHandler.isVoiced(username))	                // voiced
        					                broadcast("<+" + username + "> " + message);
        					            else if (PermissionsHandler.isAdministrator(username))	        // administrator
        					                broadcast("<&" + username + "> " + message);
        					            else
        					                if (!isRoomModerated)	                                    // user message, room not +m
        					                    broadcast("<" + username + "> " + message);
        					                else
        					                    writeMsg("Cannot send message to channel: channel mode +m\n");
        					        }
        					    }
        					    else {
        					        // TODO: Allow banned users to use /msg to query an operator about their ban (maybe)
        					        writeMsg("Cannot send message to channel: you are banned\n");
        					        display(username + " tried sending message/command while banned: " + message);
        					    }
        					    
        					    break;
    						case PacketHandler.LOGOUT:
        					    broadcast(username + " disconnected (LOGOUT).");
        					    isServerRunning = false;
        					    break;
    						case PacketHandler.LISTUSERS:
        					    writeMsg("List of the users connected at " + dateFormat.format(new Date()) + ":\n");
        					    // scan all the users connected
        					    for(int i = 0; i < clientList.size(); ++i) {
        					        ClientThread ct = clientList.get(i);
        					        writeMsg((i+1) + ") <" + ct.username + "> has been connected since " + ct.logonDate);
        					    }
        					    break;
    					}
    				}
    			}
			}
			// remove myself from the arrayList containing the list of the connected clients
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
		boolean writeMsg(String msg) {
			// if Client is still connected send the message to it, else close its connection
			if(!socket.isConnected()) {
				close();
				return false;
			}
			
			// write the message to the stream
			try {
				sOutput.writeObject(dateFormat.format(new Date()) + " " + msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to user " + username);
				display(e.toString());
			}
			return true;
		}
	}
}