package tractor.server;

import java.util.Iterator;
import java.util.Set;

import tractor.lib.GameCommand;
import tractor.lib.MessageFactory;

public class Gameroom extends Chatroom implements Runnable { // do I need a threadgroup? not really?
	private int players;
	private Thread gthread;
	private Set<User> users;
	public Gameroom(int players) {
		super();
		this.players = players;
		this.users = super.getUsers();
		this.setName("@"+this.hashCode());
		this.gthread = new Thread(this,this.getName());
		this.gthread.start();
	}
	public boolean join(User user) {
		if(this.getSize() < this.players) {
			return super.join(user);
		}
		return false;
		//error handler
	}
	public void dispose() {
		//cleanup
		this.gthread.interrupt();
	}
	
	public void sendCommand(String message) {
		for(Iterator<User> i=this.users.iterator(); i.hasNext();) {
			User u = i.next();
			//u.getIO().write(this.getName()+"|"+message, MessageFactory.GAMECMD);
			this.sendCommand(message, u);
		}
	}
	
	public void sendCommand(String message, User user) {
		user.getIO().write(this.getName()+"|"+message, MessageFactory.GAMECMD);
	}
	
	public void run() {
		while(!users.isEmpty()) {
			for(Iterator<User> i = users.iterator();i.hasNext();) {
				User user = i.next();
				MessageFactory io = user.getIO();
				while(io.hasNextMessage(MessageFactory.GAMECMD)) {
					String[] message = io.getNextMessage(MessageFactory.GAMECMD).split(" ");
					int primary = GameCommand.get(message[0]);
					switch(primary) {
					/*case GameCommand.NULL:
						//TODO: command not found error
						break;*/
					case GameCommand.JOIN:
						this.sendCommand(GameCommand.UPDATE_STATE + " " + GameCommand.WAITING, user);
						break;
					default:
						//TODO: command not found error
						break;
					}
				}
			}
		}
	}
}

