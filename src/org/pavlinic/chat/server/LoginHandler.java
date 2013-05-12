package org.pavlinic.chat.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.pavlinic.chat.server.Server.ClientThread;

public class LoginHandler {    
    /*
     * This function will check if the client's version is valid.
     */
    public static boolean isValidVersion(int client, int minimum, String username) {
        if(client >= minimum)    // version check
            return true;
        else {
            return false;
        }
    }
    
    /*
     * This function will check to see if a name is valid.
     */
    public static boolean isValidUsername(String username) {
        if (username.equalsIgnoreCase("console") || username.contains("@") || username.contains("+") ||  
                username.contains("&") || username.contains("~") || username.contains("#") || 
                username.length() > 16) {
            
            return false;
        } else {
            return true;
        }
    }
    
    /*
     * This function will return whether or not a name is in use at the moment.
     */
    public static boolean isNameFree(String username) {
        // Check if the desired username is currently in use
        for(int i = 0; i < Server.clientList.size(); ++i) {
            ClientThread currentUser = Server.clientList.get(i);
            if (username.equalsIgnoreCase(currentUser.username)) {
                return false;
            }
        }
        return true;
    }
    
    /*
     * This function will return an integer value based on if the user has successfully authenticated.
     * 
     * Return values:
     *  0 - the account is not registered
     *  1 - the account is registered, but the user failed to authenticate as it
     *  2 - the account is registered, and the user successfully authenticated as it
     * -1 - there was an error
     */
    public static int authenticate(String username, String password) {
        try {
            String userAccount = "data/logins-db/" + username + ".dat";
            boolean exists = (new File(userAccount)).exists();
            if (exists) {   // the chosen username is registered; verify its password
                BufferedReader br = new BufferedReader(new FileReader(userAccount));
                String dbPassword = br.readLine();
                br.close();

                // compare
                if (!password.equals(dbPassword)) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 0;   // the account is not registered
            }   
        } catch (Exception e) {
            return -1;
        }
    }
    
    /*
     * This function will return the MOTD.
     */
    public static String getMOTD() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("data/motd.txt"));
        String line;
        StringBuilder motd = new StringBuilder();
        
        while ((line = br.readLine()) != null) {
            motd.append("[Notice] " + line + "\n" + Server.dateFormat.format(new Date()) + " ").toString();
        }
        br.close();
        
        return motd.toString().trim() + " End of MOTD.\n";
    }
}
