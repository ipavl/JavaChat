/*
 * PermissionHandler.java
 * Copyright (C) 2013  ipavl <https://www.github.com/ipavl/javachat>
 * 
 * This file contains functions that handle user permissions, including reading and
 * writing permissions to and from file, verifying the existence of permission files,
 * and checking a user's permission level.
 * -----------------------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pavlinic.chat.server;

import java.io.*;
import java.util.*;

public class PermissionsHandler {
	static List<String> lstAdmin = new ArrayList<String>();
	static List<String> lstOps = new ArrayList<String>();
	static List<String> lstVoice = new ArrayList<String>();
	static List<String> lstBanned = new ArrayList<String>();
	
	public static void initPermissions() {
		createFiles();	// make sure the files exist, if not, create them
		try {
			Server.display("Loading permissions from file...");
			
			BufferedReader inputFile = null;
			try {
				// Clear arrays
				lstAdmin.clear();
				lstOps.clear();
				lstVoice.clear();
				lstBanned.clear();
				
			    String str;
			    int theCount = 0;	// Ah! Ah! Ah!
				
			    // Administrators
			    lstAdmin.add("Console".toLowerCase());	// console should always be an administrator
				inputFile = new BufferedReader(new FileReader("data" + File.separator + "users-admins.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstAdmin.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " administrators from file.");
	            inputFile.close();
			    theCount = 0;
			    
			    // Operators
				inputFile = new BufferedReader(new FileReader("data" + File.separator + "users-ops.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstOps.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " operators from file.");
			    inputFile.close();
			    theCount = 0;
			    
			    // Voiced users
				inputFile = new BufferedReader(new FileReader("data" + File.separator + "users-voiced.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstVoice.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " voiced users from file.");
	            inputFile.close();
			    theCount = 0;
			    
			    // Banned users
				inputFile = new BufferedReader(new FileReader("data" + File.separator + "users-banned.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstBanned.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " banned users from file.");
	            inputFile.close();
			    theCount = 0;
			} catch (FileNotFoundException e) {
			    e.printStackTrace();
			} catch (IOException e) {
			    e.printStackTrace();
			} finally {
			    if (inputFile != null) {
			        inputFile.close();
			    }
			}
		} catch (Exception ex) {
	    	Server.display("Cannot load permissions lists from file: " + ex.toString());
	    }
	}
	
	public static void addPermission(String permission, String username) {
		BufferedWriter bw = null;
		try {
			if(permission == "admin") {
		    	bw = new BufferedWriter(new FileWriter("data" + File.separator + "users-admins.txt", true));
		    	lstAdmin.add(username);
		    }
			else if(permission == "op") {
		    	bw = new BufferedWriter(new FileWriter("data" + File.separator + "users-ops.txt", true));
		    	lstOps.add(username);
		    }
		    else if(permission == "voice") {
		    	bw = new BufferedWriter(new FileWriter("data" + File.separator + "users-voiced.txt", true));
		    	lstVoice.add(username);
		    }
		    else if(permission == "ban") {
		    	bw = new BufferedWriter(new FileWriter("data" + File.separator + "users-banned.txt", true));
		    	lstBanned.add(username);
		    }
		    bw.write(username);
		    bw.newLine();
		    bw.flush();
		    
		    initPermissions();	// reload permissions
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		} finally { // always close the file
		    if (bw != null) {
		        try {
		            bw.close();
		        } catch (IOException ioe2) {
		            // just ignore it
		        }
		    }
		}
	}

	static void createFiles() {
		try {
			boolean success = new File("data" + File.separator + "logins-db").mkdirs();	// make directory
		    if (success) {
		        Server.display("Created directory: data" + File.separator + "logins-db");
		    } else {
		        // File already exists
		    }
			
			File file0 = new File("data" + File.separator + "logins-db" + File.separator + "console.dat");
		    // Create file if it does not exist
		    success = file0.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file0);
		    } else {
		        // File already exists
		    }
		    File file1 = new File("data" + File.separator + "users-ops.txt");
		    // Create file if it does not exist
		    success = file1.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file1);
		    } else {
		        // File already exists
		    }
		    File file2 = new File("data" + File.separator + "users-voiced.txt");
		    // Create file if it does not exist
		    success = file2.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file2);
		    } else {
		        // File already exists
		    }
		    File file3 = new File("data" + File.separator + "users-banned.txt");
		    // Create file if it does not exist
		    success = file3.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file3);
		    } else {
		        // File already exists
		    }
		    File file4 = new File("data" + File.separator + "users-admins.txt");
		    // Create file if it does not exist
		    success = file4.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file4);
		    } else {
		        // File already exists
		    }
		    
		    File file5 = new File("data" + File.separator + "motd.txt");
	        // Create file if it does not exist
            success = file5.createNewFile();
            if (success) {
                BufferedWriter motdFile = new BufferedWriter(new FileWriter(file5));
                motdFile.write("This is the default Message of the Day.");
                motdFile.newLine();
                motdFile.write("It can be changed by editing data" + File.separator + "motd.txt.");
                motdFile.close();
                Server.display("Created file: " + file5);
            } else {
                // File already exists
            }
		} catch (IOException e) {
			Server.display("Could not create file: " + e);
		}
	}
	
	public static boolean isAdministrator(String username) {
		if (lstAdmin.contains(username.toLowerCase()))
			return true;
		else
			return false;
	}
	
	public static boolean isOperator(String username) {
		if (lstOps.contains(username.toLowerCase()))
			return true;
		else
			return false;
	}
	
	public static boolean isVoiced(String username) {
		if (lstVoice.contains(username.toLowerCase()))
			return true;
		else
			return false;
	}
	
	public static boolean isBanned(String username) {
		if (lstBanned.contains(username.toLowerCase()))
			return true;
		else
			return false;
	}
}