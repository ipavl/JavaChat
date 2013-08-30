/*
 * PacketHandler.java
 * Copyright (C) 2013  ipavl <https://www.github.com/ipavl/javachat>
 * 
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server.
 *  
 * When talking from a Java Client to a Java Server, it is a lot easier to pass Java objects
 * as there is no need to count bytes or to wait for a line feed at the end of the frame.
 * 
 * This file is shared between both the client and server.
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

package org.pavlinic.chat;

import java.io.*;

public class PacketHandler implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the client
	public static final int LISTUSERS = 0;  // send a request for the list of connected users
    public static final int MESSAGE = 1;    // send a command or message
    public static final int LOGOUT = 2;     // disconnect from the server
    
	public int type;
	public String message;
	
	// constructor
	public PacketHandler(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	// getters
	public int getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
}