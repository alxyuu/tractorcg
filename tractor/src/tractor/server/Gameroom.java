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
	private User host;
	private int state;
	private int TRUMP_SUIT;
	private int TRUMP_NUMBER;
	private int called_cards;
	private User caller;
	private User lead;
	public Gameroom(int players) {
		super();
		this.players = players;
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
	
	private void setLead(User user) {
		this.lead = user;
		int index = this.users.indexOf(user);
		if(index > 0) { // -1 = not found ruh roh, 0 = number 1 don't do anyone
			Collections.rotate(this.users, index);
		}
	}
	
	private void deal() {
		this.sendUpdateState(GameCommand.DEALING);
		Thread dealing = new Thread("dealing-"+this.getName()) {
			public void run() {
				int decks = 3;
				ArrayList<Card> cards = new ArrayList<Card>();
				for(int i=0;i<decks; i++)
					cards.addAll(Card.getDeck());
				for(int i=0;i<7;i++) // seven shuffles for fully random guffaw
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
				if(caller != null)
					setLead(caller);
				//TODO: give lead dipai
				sendUpdateState(GameCommand.START);
			}
		};
		dealing.start();
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
					{
						//gtfo if you're not the host
						if(user != this.host)
							break;
						for(Iterator<User> i2 = users.iterator();i2.hasNext();) {
							i2.next().setGameScore(Card.TWO);
						}
						this.TRUMP_NUMBER = Card.TWO;
						this.TRUMP_SUIT  = -1;
						this.called_cards = 0;
						this.setLead(host);
						this.deal();
					}
					break;
					case GameCommand.PLAY_CARD:
					{
						Card played = Card.getCard(message[1],message[2]);
						int call_number = Integer.parseInt(message[3]);
						if(this.state == GameCommand.DEALING) { //card being called
							if(played.getNumber() == this.TRUMP_NUMBER || played.getSuit() == Card.TRUMP) { // make sure the call is valid 
								//card number for jokers might cause first half to return true, shouldn't matter
								if(call_number > this.called_cards || call_number == this.called_cards && played.getSuit() == Card.TRUMP) {
									//TODO: differentiate between big and small jokers
									this.called_cards = call_number;
									this.TRUMP_SUIT = played.getSuit();
									this.caller = user;
								} else {
									System.out.println("illegal call");
								}
							}
						} else if(this.state == GameCommand.START) { //normally played
							
						} else {
							System.out.println("STRANGER DANGER");
						}
					}
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

