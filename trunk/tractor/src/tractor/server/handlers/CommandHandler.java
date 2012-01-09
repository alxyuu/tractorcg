package tractor.server.handlers;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import tractor.lib.ChatCommand;
import tractor.lib.MessageFactory;
import tractor.server.Chatroom;
import tractor.server.Gameroom;
import tractor.server.Server;
import tractor.server.User;

/**
 * @author 378250
 *
 */
public class CommandHandler extends ServerHandler {
	public void run() {
		System.out.println("listening for commands");
		//Collection<User> users = Collections.synchronizedCollection(Server.getInstance().getUsers().values());
		ConcurrentHashMap<String,User> users = Server.getInstance().getUsers();
		ConcurrentHashMap<String,Chatroom> chatrooms = Server.getInstance().getChatrooms();
		while(true) {
			try {
				for(Iterator<User> i = users.values().iterator(); i.hasNext();) {
					User user = i.next();
					MessageFactory io = user.getIO();
					while(io.hasNextMessage(MessageFactory.CHATCMD)) {
						String cmd = io.getNextMessage(MessageFactory.CHATCMD);
						int index = cmd.indexOf(" ");
						String command;
						if(index == -1) {
							index = cmd.length();
							command = "";
						} else {
							//command = cmd.substring(index+1).trim().toUpperCase();
							command = cmd.substring(index+1).trim();
						}
						switch (ChatCommand.get(cmd.substring(0,index))) {
						case C_JOIN:
						{
							if(command.charAt(0) != '#') { //TODO: valid characters check
								System.out.println(user.getName() + " tried to join invalid room name: "+command);
								io.write("ERR Invalid room name", MessageFactory.CHATCMD);
							} else {
								Chatroom tojoin = chatrooms.get(command.toUpperCase());
								if(tojoin == null) {
									System.out.println(command+" not found, creating");
									tojoin = new Chatroom(command.toUpperCase());
									chatrooms.put(command.toUpperCase(), tojoin);
								}
								if(tojoin.contains(user)) {
									System.out.println(user.getName() + " already in "+command+", ignoring");
									break; 
								}
								tojoin.join(user);
								user.addChatroom(tojoin);
								System.out.println(user.getName() + " has joined "+command);
								io.write("JOIN "+command, MessageFactory.CHATCMD);
								tojoin.sendList(user);
								if(tojoin.getName().equals("#LOBBY")) {
									user.getIO().write("#LOBBY|>MOTD: Thank you for beta testing tractorcg!", MessageFactory.CHAT);
									user.getIO().write("#LOBBY|>New version: the latest version of tractorcg is 0.0.126.", MessageFactory.CHAT); 
									user.getIO().write("#LOBBY|>Download the latest version from: http://code.google.com/p/tractorcg/downloads", MessageFactory.CHAT);
									user.getIO().write("#LOBBY|>If possible, try to use the exe version, as it generates a log file, which can be emailed to me to aid in debugging.", MessageFactory.CHAT);
									user.getIO().write("#LOBBY|>Please send any comments and suggestions to: iu@utexas.edu", MessageFactory.CHAT);
								}
							}
						}
						break;
						case C_PART:
						{
							Chatroom topart = chatrooms.get(command.toUpperCase());
							if(topart != null) {
								topart.part(user);
								user.removeChatroom(topart);
								System.out.println(user.getName() + " has left "+command);
								io.write("PART "+command, MessageFactory.CHATCMD);
							} else {
								//do nothing?
							}
						}
						break;
						case G_CREATE:
						{
							if(user.getCurrentGame() == null) {
								int decks = 3;
								if(!command.equals("")) {
									try {
										decks = Integer.parseInt(command);
										if(decks < 1 || decks > 3) {
											//TODO: error handler
											System.out.println("invalid gcreate argument: "+command);
											break;
										}
									} catch (NumberFormatException e) {
										System.out.println("invalid gcreate argument: "+command);
									}
								}
								Gameroom room = new Gameroom(4,decks);
								chatrooms.put(room.getName(), room);
								room.join(user);
								room.setHost(user);
								user.setCurrentGame(room);
								user.setGamePosition(1);
								user.addChatroom(room);
								room.start();
								io.write("GHOOK " + room.getName() + " 1 " + room.getGameSize(), MessageFactory.CHATCMD);
							} else {
								//TODO: already in game, error handler
							}
						}
						break;
						case G_HOOK:
						{
							User tohook = users.get(command.toUpperCase());
							if(tohook != null) {
								Gameroom tojoin = tohook.getCurrentGame();
								if(tojoin != null) {
									if(tojoin.join(user)) {
										int position = tojoin.getSize();
										user.setCurrentGame(tojoin);
										user.setGamePosition(position);
										user.addChatroom(tojoin);
										io.write("GHOOK " + tojoin.getName() + " " + position + " " + tojoin.getGameSize(), MessageFactory.CHATCMD);
									} else {
										//TODO: game full error
									}

								} else {
									//TODO: game not found error
								}
							} else {
								//TODO: user not found error
							}
						}
						case G_PART:
						{
							Chatroom topart;
							if((topart = chatrooms.get(command)) != null && topart == user.getCurrentGame()) {
								topart.part(user);
								user.setCurrentGame(null);
								user.removeChatroom(topart);
								io.write("GPART "+topart.getName(), MessageFactory.CHATCMD);
								if(topart.getSize() == 0) {
									((Gameroom)topart).dispose();
									chatrooms.remove(topart);
								}
							}
						}
						break;
						default:
							//TODO: some error handler
							io.write("ERR Unsupported command", MessageFactory.CHATCMD);
						}
					}
				}
				//dynamic sleeping lalalaal
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace(System.out);
				break;
			}
		}
	}
}
