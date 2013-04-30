package org.pavlinic.chat.server;

import java.io.*;
import java.util.*;

public class ServerPermissionsHandler {
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
				inputFile = new BufferedReader(new FileReader("data/users-admins.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstAdmin.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " administrators from file.");
			    theCount = 0;
			    // Operators
				inputFile = new BufferedReader(new FileReader("data/users-ops.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstOps.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " operators from file.");
			    theCount = 0;
			    // Voiced users
				inputFile = new BufferedReader(new FileReader("data/users-voiced.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstVoice.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " voiced users from file.");
			    theCount = 0;
			    // Banned users
				inputFile = new BufferedReader(new FileReader("data/users-banned.txt"));
			    while ((str = inputFile.readLine()) != null) {
			        lstBanned.add(str.toLowerCase());
			        theCount++;
			    }
			    Server.display("  - Loaded " + theCount + " banned users from file.");
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
		    	bw = new BufferedWriter(new FileWriter("data/users-admins.txt", true));
		    	lstAdmin.add(username);
		    }
			else if(permission == "op") {
		    	bw = new BufferedWriter(new FileWriter("data/users-ops.txt", true));
		    	lstOps.add(username);
		    }
		    else if(permission == "voice") {
		    	bw = new BufferedWriter(new FileWriter("data/users-voiced.txt", true));
		    	lstVoice.add(username);
		    }
		    else if(permission == "ban") {
		    	bw = new BufferedWriter(new FileWriter("data/users-banned.txt", true));
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
			boolean success = new File("data/logins-db/").mkdirs();	// make directory
		    if (success) {
		        Server.display("Created directory: data/logins-db/");
		    } else {
		        // File already exists
		    }
			
			File file0 = new File("data/logins-db/console.dat");
		    // Create file if it does not exist
		    success = file0.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file0);
		    } else {
		        // File already exists
		    }
		    File file1 = new File("data/users-ops.txt");
		    // Create file if it does not exist
		    success = file1.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file1);
		    } else {
		        // File already exists
		    }
		    File file2 = new File("data/users-voiced.txt");
		    // Create file if it does not exist
		    success = file2.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file2);
		    } else {
		        // File already exists
		    }
		    File file3 = new File("data/users-banned.txt");
		    // Create file if it does not exist
		    success = file3.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file3);
		    } else {
		        // File already exists
		    }
		    File file4 = new File("data/users-admins.txt");
		    // Create file if it does not exist
		    success = file4.createNewFile();
		    if (success) {
		        Server.display("Created file: " + file4);
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