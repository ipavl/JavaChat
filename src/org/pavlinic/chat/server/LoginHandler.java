/*
 * LoginHandler.java
 * Copyright (C) 2013  ipavl <https://www.github.com/ipavl/javachat>
 *
 * This file contains functions that handle user login and authentication.
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
    public static boolean isValidVersion(int client, int minimum) {
        if(client >= minimum)    // version check
            return true;
        else
            return false;
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
            String userAccount = "data" + File.separator + "logins-db" + File.separator + username + ".dat";
            boolean exists = (new File(userAccount)).exists();
            if (exists) {   // the chosen username is registered; verify its password
                BufferedReader br = new BufferedReader(new FileReader(userAccount));
                String dbPassword = br.readLine();
                br.close();

                // compare
                if (!password.equals(dbPassword)) {
                    return 1;	// unsuccessful authentication for a registered name
                } else {
                    return 2;	// successful authentication
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
        BufferedReader br = new BufferedReader(new FileReader("data" + File.separator + "motd.txt"));
        String line;
        StringBuilder motd = new StringBuilder();

        while ((line = br.readLine()) != null) {
            motd.append("[Notice] " + line + "\n" + Server.dateFormat.format(new Date()) + " ").toString();
        }
        br.close();

        return motd.toString().trim() + " End of MOTD.\n";
    }
}
