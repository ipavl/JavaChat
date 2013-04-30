SERVER README
=============

To start the server in console mode use one of the following commands at the command
prompt/terminal, or launch one of the included batch scripts:

	> java Server [portNumber]

	If the portNumber is not specified 1500 is used.
 
In console mode, if an error occurs the program simply stops; when a GUI id used, the
GUI is informed of the disconnection.

The Server GUI can be started using the included script, or by launching it at the command
prompt using:

	> java ServerGUI
	
=======
LOGGING
=======

The server will log all chat messages and events/errors to a file called server.log. This file
is generally found in the same directory you launched the server from (i.e. where the Start script
is located, or where you typed 'java Server' from at the command prompt).

====================
SERVER-SIDE COMMANDS
====================

The server itself does not currently include any commands. User management commands such
as /kick and /ban are planned, but are not included at the current moment.

The following are commands that can be executed via a client, provided the user has appropriate
priveleges:

	- /me <action>