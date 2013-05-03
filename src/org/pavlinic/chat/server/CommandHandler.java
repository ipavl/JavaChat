/*
 * CommandHandler.java
 * 
 * This file handles commands sent by users and works with PermissionsHandler.java for
 * permission handling.
 */

package org.pavlinic.chat.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.Socket;
import java.util.Date;

import org.pavlinic.chat.AeSimpleSHA1;
import org.pavlinic.chat.server.Server.ClientThread;

public class CommandHandler {
	
	static void sendMessage(String msg) {
		((ClientThread) Server.ClientThread.currentThread()).writeMsg(msg + "\n");
	}
	
	public static void processCommand(String username, String command) {
		try {
			Server.display(username + " issued command: " + command);

			int userRights = 0;
			if (PermissionsHandler.isBanned(username))	             // banned
				userRights = -1;
			else if (username.equalsIgnoreCase("console"))           // console
				userRights = 4;
			else if (PermissionsHandler.isAdministrator(username))	 // administrator
				userRights = 3;
			else if (PermissionsHandler.isOperator(username))        // operator
				userRights = 2;
			else if (PermissionsHandler.isVoiced(username))	         // voiced
				userRights = 1;
			else	                                                 // regular user
				userRights = 0;
			
			if (command.startsWith("me")) {
				// Usage: /me <action>
				// Effect: Outputs given message as " * <Name> says hello"
				Server.broadcast("* " + username + " " + command.substring(3));
			}
			else if (command.startsWith("nick") && !username.equalsIgnoreCase("console")) {
				// Usage: /nick <name>
				// Effect: Changes user's name
				Server.ClientThread.currentThread().setName(command.substring(5));	// rename thread
				((ClientThread) Server.ClientThread.currentThread()).username = command.substring(5);
				Server.broadcast(username + " is now known as " + command.substring(5));
			}
			else if (command.startsWith("account")) {
				// Usage: /account <parameter> [options]
				// Effect: Manage your account
				String service = command.substring(8);
				if (service.startsWith("register")) {
					// Usage: /account register <PASSWORD>
					// Effect: Registers the account if it's not taken already.
					String userAccount = "data/logins-db/" + username + ".dat";
					boolean exists = (new File(userAccount)).exists();
			    	if (!exists) {
						BufferedWriter bw = new BufferedWriter(new FileWriter(userAccount, false));
					    bw.write(AeSimpleSHA1.SHA1(service.substring(9)));	// hash the password
					    bw.flush();
					    sendMessage("Registered account successfully!");
					    bw.close();
			    	}
			    	else {
			    		sendMessage("That username is already registered.");
			    		sendMessage("If you own this account and want to change its password,");
			    		sendMessage("please use /account password <newpassword>");
			    	}
				}
			}
			else if (command.startsWith("say") && userRights == 4) {
				// Usage: /say <message>
				// Effect: Says a message (console use only)
				Server.broadcast("<&" + username + "> " + command.substring(4));
			}
			else if (command.startsWith("userlist") && userRights == 4) {
				// Usage: /userlist
				// Effect: Displays list of connected users (console only)
				Server.display("List of the users connected at " + Server.dateFormat.format(new Date()) + ":\n");
				// scan all the users connected
				for(int i = 0; i < Server.clientList.size(); ++i) {
					ClientThread ct = Server.clientList.get(i);
					Server.display((i+1) + ") <" + ct.username + "> has been connected since " + ct.logonDate);
				}
			}
			else if (command.startsWith("mode")) {
				// Usage: /mode +/-<mode> [parameters]
				// Effect: Changes a channel or user mode

				String mode = command.substring(5);			
				
				if(userRights > 1) {	// operator only modes
					if(mode.equals("+m"))	// put channel in moderation mode
						Server.isRoomModerated = true;
					else if(mode.equals("-m"))	// remove moderation mode
						Server.isRoomModerated = false;
					else if(mode.startsWith("+o"))	// temporary op
						PermissionsHandler.lstOps.add(mode.substring(3).toLowerCase());
					else if(mode.startsWith("-o"))	// remove op
						PermissionsHandler.lstOps.remove(mode.substring(3).toLowerCase());
					else if(mode.startsWith("+v"))	// temporary voice
						PermissionsHandler.lstVoice.add(mode.substring(3).toLowerCase());
					else if(mode.startsWith("-v"))	// remove voice
						PermissionsHandler.lstVoice.remove(mode.substring(3).toLowerCase());
					else if(mode.startsWith("+b"))	// ban (permanent)
						PermissionsHandler.addPermission("ban", mode.substring(3).toLowerCase());
					else if(mode.startsWith("-b"))	// unban (temporary)
						PermissionsHandler.lstBanned.remove(mode.substring(3).toLowerCase());
					
					if(userRights > 2) {	// administrator modes
						if(mode.startsWith("+O"))	// permaop
							PermissionsHandler.addPermission("op", mode.substring(3));
						else if(mode.startsWith("+V"))	// permavoice
							PermissionsHandler.addPermission("voice", mode.substring(3));
					}
					
					if(userRights == 4)	// console-only modes
						if(mode.startsWith("+A"))	// make a user an administrator
							PermissionsHandler.addPermission("admin", mode.substring(3));
					
					Server.broadcast(username + " sets mode: " + mode);
				}
			}
			else if (command.equalsIgnoreCase("reload") && userRights > 2) {
				// Usage: /reload
				// Effect: Reloads server permissions
				PermissionsHandler.initPermissions();
			}
			/*else if (command.startsWith("mute") && ServerPermissionsHandler.isOperator(username) || ServerPermissionsHandler.isAdministrator(username)) {
				// Usage: /mute <name>
				// Effect: Silences a user
				//int id = Integer.parseInt(command.substring(5));
			    Thread[] tList = new Thread[Thread.activeCount()];

			    int numThreads = Thread.enumerate(tList);

			    for (int i = 0; i < numThreads; i++) {
			    	if(tList[i].getName().equals(command.substring(5))) {
				    	tList[i].suspend();	// "mute" the user by suspending their thread
						//Server.broadcast(command.substring(5) + " has been muted by " + username);
						Server.broadcast(username + " sets mode: +q " + command.substring(5));	// IRC version
				    	break;
				    }
			    }
			}
			else if (command.startsWith("kick") && ServerPermissionsHandler.isOperator(username) || ServerPermissionsHandler.isAdministrator(username)) {
				// Usage: /kick <name>
				// Effect: Disconnects a user
			    Thread[] tList = new Thread[Thread.activeCount()];

			    int numThreads = Thread.enumerate(tList);

			    for (int i = 0; i < numThreads; i++) {
			    	if(tList[i].getName().equals(command.substring(5))) {
				    	int id = (int) tList[i].getId();	// get user id
				    	tList[i].suspend();	// "mute" the user by suspending their thread
				    	Server.remove(id);	// "deafen" the user by removing them from the client list
						Server.broadcast(command.substring(5) + " has been kicked from the chat by " + username);
				        break;
				    }
			    }
			}*/
			else if (command.equalsIgnoreCase("stop") && userRights > 2) {
				// Usage: /stop
				// Effect: Stops the server
				Server.broadcast(" - ALERT - Server received stop command by " + username);
				Server.broadcast(" - ALERT - The server will now shutdown.");
				Server.isServerRunning = false;
				// connect to myself as Client to exit statement 
				try {
					new Socket("localhost", Server.port);
				}
				catch(Exception e) {
					// nothing I can really do
				}
			}
			else {
			    // TODO: Make this a single function (i.e. make sendMessage interpret rank, and have this listed as one operation)
			    if (userRights == 4)
			        Server.display("Unknown command. Type /help for a list of commands.");
			    else
			        sendMessage("Unknown command. Type /help for a list of commands.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}