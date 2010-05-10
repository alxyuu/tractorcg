package tractor.server.handlers;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import tractor.lib.ChatCommand;
import tractor.lib.MessageFactory;
import tractor.server.Chatroom;
import tractor.server.Server;
import tractor.server.User;

public class CommandHandler extends ServerHandler {
	public void run() {
		System.out.println("listening for commands");
		Collection<User> users = Collections.synchronizedCollection(Server.getInstance().getUsers().values());
		ConcurrentHashMap<String,Chatroom> chatrooms = Server.getInstance().getChatrooms();
		while(true) {
			try {
				for(Iterator<User> i = users.iterator(); i.hasNext();) {
					User user = i.next();
					MessageFactory io = user.getIO();
					while(io.hasNextMessage(MessageFactory.CHATCMD)) {
						String cmd = io.getNextMessage(MessageFactory.CHATCMD).toUpperCase();
						int index = cmd.indexOf(" ");
						String command;
						if(index == -1) {
							index = cmd.length();
							command = "";
						} else {
							command = cmd.substring(index+1).trim();
						}
						switch (ChatCommand.get(cmd.substring(0,index))) {
						case C_JOIN:
							if(command.charAt(0) != '#') { //TODO: valid characters check
								System.out.println(user.getName() + " tried to join invalid room name: "+command);
								io.write("ERR Invalid room name", MessageFactory.CHATCMD);
							} else {
								Chatroom tojoin = chatrooms.get(command);
								if(tojoin == null) {
									System.out.println(command+" not found, creating");
									tojoin = new Chatroom(command);
									chatrooms.put(command, tojoin);
								}
								tojoin.join(user);
								user.addChatroom(tojoin);
								System.out.println(user.getName() + " has joined "+command);
								io.write("JOIN "+command, MessageFactory.CHATCMD);
							}
							break;
						case C_PART:
							Chatroom topart = chatrooms.get(command);
							if(topart != null) {
								topart.part(user);
								user.removeChatroom(topart);
								System.out.println(user.getName() + " has left "+command);
								io.write("PART "+command, MessageFactory.CHATCMD);
							} else {
								//do nothing?
							}
							break;
						default:
							//some error handler
							io.write("ERR Unsupported command", MessageFactory.CHATCMD);
						}
					}
				}
				//dynamic sleeping lalalaal
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
