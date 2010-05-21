package tractor.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import tractor.lib.Card;
import tractor.lib.GameCommand;
import tractor.lib.MessageFactory;

public class Gameroom extends Chatroom implements Runnable { // do I need a threadgroup? not really?
	private int players;
	private Thread gthread;
	private Set<User> users;
	private User host;
	private int state;
	public Gameroom(int players) {
		super();
		this.players = players;
		this.users = super.getUsers();
		this.setName("@"+this.hashCode());
		this.gthread = new Thread(this,this.getName());
		this.gthread.start();
		this.state = GameCommand.WAITING;
	}
	public void setHost(User user) {
		this.host = user;
	}
	public int getGameSize() {
		return this.players;
	}
	public boolean join(User user) {
		if(this.getSize() < this.players) {
			return super.join(user);
		}
		return false;
		//error handler
	}
	public void part(User user) {
		super.part(user);
		this.sendCommand(GameCommand.PART + " " + user.getName() + " " + user.getGamePosition());
		this.sendUpdateState(GameCommand.WAITING);
	}
	public void dispose() {
		//cleanup
		this.gthread.interrupt();
	}
	
	private void sendUpdateState(int state) {
		this.sendCommand(GameCommand.UPDATE_STATE + " " + state);
		this.state = state;
	}
	private void sendUpdateState(int state, User user) {
		this.sendCommand(GameCommand.UPDATE_STATE + " " + state, user);
		this.state = state;
	}
	private void sendCommand(String message) {
		for(Iterator<User> i=this.users.iterator(); i.hasNext();) {
			User u = i.next();
			//u.getIO().write(this.getName()+"|"+message, MessageFactory.GAMECMD);
			this.sendCommand(message, u);
		}
	}
	
	private void sendCommand(String message, User user) {
		user.getIO().write(this.getName()+"|"+message, MessageFactory.GAMECMD);
	}
	
	private void sendCommandExclude(String message, User user) {
		for(Iterator<User> i=this.users.iterator(); i.hasNext();) {
			User u = i.next();
			if(u != user) 
				this.sendCommand(message, u);
		}
	}
	
	public void run() {
		while(!users.isEmpty()) {
			for(Iterator<User> i = users.iterator();i.hasNext();) {
				User user = i.next();
				MessageFactory io = user.getIO();
				while(io.hasNextMessage(MessageFactory.GAMECMD)) {
					String[] message = io.getNextMessage(MessageFactory.GAMECMD).split(" ");
					System.out.println(Arrays.toString(message));
					//int primary = GameCommand.get(message[0]);
					int primary = Integer.parseInt(message[0]);
					System.out.println(primary);
					switch(primary) {
					/*case GameCommand.NULL:
						//TODO: command not found error
						break;*/
					case GameCommand.JOIN:
						this.sendCommandExclude(GameCommand.JOIN + " " + user.getGamePosition() + " " + user.getName(), user);
						for(Iterator<User> i2 = users.iterator();i2.hasNext();) {
							User u = i2.next();
							if(u!=user)
								this.sendCommand(GameCommand.JOIN + " " + u.getGamePosition() + " " + u.getName(), user);
						}
						if(this.getSize() == this.getGameSize()) {
							this.sendUpdateState(GameCommand.READY);
						} else {
							this.sendUpdateState(GameCommand.WAITING, user);
						}
						break;
					case GameCommand.START:
						this.sendUpdateState(GameCommand.DEALING);
						Thread dealing = new Thread("dealing-"+this.getName()) {
							public void run() {
								int decks = 3;
								ArrayList<Card> cards = new ArrayList<Card>();
								for(int i=0;i<decks; i++)
									cards.addAll(Card.getDeck());
								Collections.shuffle(cards);
								System.out.println("something");
								while(cards.size() > 4+users.size()) {
									System.out.println("something else");
									for(Iterator<User> i = users.iterator();i.hasNext();) {
										User user = i.next();
										Card todeal = cards.remove(0);
										sendCommand(GameCommand.DEALING + " " + user.getName() + " " + todeal, user);
										sendCommandExclude(GameCommand.DEALING + " " + user.getName(), user);
										try {
											Thread.sleep(50);
										} catch (InterruptedException e) {
											e.printStackTrace();
											return;
										}
									}
								}
								sendUpdateState(GameCommand.START);
							}
						};
						dealing.start();
						break;
					default:
						//TODO: command not found error
						break;
					}
				}
			}
			try {
				//dynamic sleeping?
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

