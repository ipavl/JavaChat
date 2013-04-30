package org.pavlinic.chat;

import java.io.*;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server it is a lot easier to pass Java objects,
 * as there is no need to count bytes or to wait for a line feed at the end of the frame.
 * This file is shared between both the client and server.
 */

public class PacketHandler implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	//	- LISTUSERS to receive the list of the users connected
	// 	- MESSAGE an ordinary message
	// 	- LOGOUT to disconnect from the Server
	public static final int LISTUSERS = 0;
    public static final int MESSAGE = 1;
    public static final int LOGOUT = 2;
    
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