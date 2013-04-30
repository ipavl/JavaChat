package org.pavlinic.chat;

public class ServerBot {
	public static void processCommand(String username, String command) {
		Server.display(username + " issued bot command: " + command);
		Server.broadcast(username + ": !" + command);
		
		if (command.startsWith("help")) {
			// Usage: !help
			// Effect: Outputs help
			Server.broadcast("<ServerBot> " + username + ": !clientthreads, !about, !help");
		}
		else if (command.startsWith("about")) {
			// Usage: !about
			// Effect: Outputs information about the bot
			Server.broadcast("<ServerBot> " + username + ": Hello! I am a server bot to aid users.");
			Server.broadcast("<ServerBot> " + username + ": I provide various commands which you");
			Server.broadcast("<ServerBot> " + username + ": can view by typing !help.");
		}
		else if (command.equalsIgnoreCase("clientthreads")) {
	        Thread[] tList = new Thread[Thread.activeCount()];

	        int numThreads = Thread.enumerate(tList);

	        for (int i = 0; i < numThreads; i++) {
	            Server.broadcast("  - Thread List [" + i + "] = " + tList[i].getName());
	        }
		}
		//else {
			//Server.writeMsg("Unknown command. Type /help for a list of commands.");
		//}
	}
}
