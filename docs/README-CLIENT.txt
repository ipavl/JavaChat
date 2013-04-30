CLIENT README
=============

To start the Client in console mode use one of the following commands at the command
prompt/terminal, or launch one of the included batch scripts:

	> java Client
	> java Client username
	> java Client username portNumber
	> java Client username portNumber serverAddress

	- If the portNumber is not specified 1500 is used.
	- If the serverAddress is not specified "localHost" is used
	- If the username is not specified "Guest" + random number is used
	> java Client 
	is equivalent to
	> java Client Guest<random number> 1500 localhost 
	 
In console mode, if an error occurs the program simply stops. When a GUI id used, the
GUI is informed of the disconnection.

The Client GUI can be started using the included script, or by launching it at the command
prompt using:

	> java ClientGUI
	
====================
CLIENT-SIDE COMMANDS (console mode only)
====================

These commands will only affect your client:

	* /connect (connects you to the server; you must be disconnected to use this)
	* /commands (lists commands)
	* /logout (disconnect from server)
	* /userlist (list users connected to the server)
	* /quit (exit client)
	
The GUI client has buttons for connect, disconnect, and userlist.