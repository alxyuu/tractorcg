package tractor.server;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

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
	private boolean firstgame;
	private User caller;
	private User lead;
	private int dipaiSize;
	private List<Card> dipai;
	private User currentUser;
	private Iterator<User> userIterator;
	private User highest;
	/** It constructs the game room.
	 * @param players
	 */
	public Gameroom(int players) {
		super();
		this.players = players;
		this.setName("@"+this.hashCode());
		this.gthread = new Thread(this,this.getName());
		this.gthread.start();
		this.state = GameCommand.WAITING;
		this.firstgame = true;
		this.dipai = Collections.emptyList();
	}
	/** It sets the host of the gameroom.
	 * @param user
	 */
	public void setHost(User user) {
		this.host = user;
		sendCommand(GameCommand.SET_HOST + " " + user.getName(),user);
	}
	/** It gets the number of players in the game.
	 * @return
	 */
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
	/** It destroys the game room.
	 * 
	 */
	public void dispose() {
		//cleanup
		this.gthread.interrupt();
	}

	/** It sends the updated state of the game room.
	 * @param state
	 */
	private void sendUpdateState(int state) {
		this.sendCommand(GameCommand.UPDATE_STATE + " " + state);
		this.state = state;
	}
	/** It sends the updated stated.
	 * @param state
	 * @param user
	 */
	private void sendUpdateState(int state, User user) {
		this.sendCommand(GameCommand.UPDATE_STATE + " " + state, user);
		this.state = state;
	}
	/** It sends the message.
	 * @param message
	 */
	private void sendCommand(String message) {
		for(Iterator<User> i=this.users.iterator(); i.hasNext();) {
			User u = i.next();
			//u.getIO().write(this.getName()+"|"+message, MessageFactory.GAMECMD);
			this.sendCommand(message, u);
		}
	}

	/** It sends the message to a particular user.
	 * @param message
	 * @param user
	 */
	private void sendCommand(String message, User user) {
		user.getIO().write(this.getName()+"|"+message, MessageFactory.GAMECMD);
	}

	/** It sends a message excluding a particular user.
	 * @param message
	 * @param user
	 */
	private void sendCommandExclude(String message, User user) {
		for(Iterator<User> i=this.users.iterator(); i.hasNext();) {
			User u = i.next();
			if(u != user) 
				this.sendCommand(message, u);
		}
	}

	/** It sets who has lead in the game room.
	 * @param user
	 */
	private void setLead(User user) {
		this.lead = user;
		int index = this.users.indexOf(user);
		if(index > 0) { // -1 = not found ruh roh, 0 = number 1 don't do anyone
			Collections.rotate(this.users, index);
		}
	}

	/** It updates the stats of the game room.
	 * 
	 */
	private void updateStats() {
		String stats = GameCommand.SET_STATS + " " + this.TRUMP_NUMBER + " " + 4;
		for(Iterator<User> i = users.iterator();i.hasNext();) {
			User user = i.next();
			stats += " " + user.getName() + " " + user.getGameScore();
		}
		sendCommand(stats);
	}

	/** It deals the cards in the game room.
	 * 
	 */
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
				for(Iterator<User> i = users.iterator();i.hasNext();) {
					i.next().newHand();
				}
				while(cards.size() > 4+users.size()) {
					for(Iterator<User> i = users.iterator();i.hasNext();) {
						User user = i.next();
						Card todeal = cards.remove(0);
						user.getHand().addCard(todeal);
						sendCommand(GameCommand.DEALING + " " + user.getName() + " " + todeal, user);
						sendCommandExclude(GameCommand.DEALING + " " + user.getName(), user);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
					}
				}
				try {
					Thread.sleep(1000);	
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}

				if(firstgame && caller != null) {
					setLead(caller);
					firstgame = false;
				}

				dipaiSize = cards.size();
				//TODO: flip dipai if no one calls
				String dipai = " "+cards.size();
				for(Card card : cards) {
					dipai += " "+card.getSuit()+" "+card.getNumber();
				}
				sendCommand(GameCommand.DIPAI + dipai, lead);
				sendUpdateState(GameCommand.DIPAI);
				sendCommand(GameCommand.CLEAR_TABLE+"");
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
					//int primary = GameCommand.get(message[0]);
					int primary = Integer.parseInt(message[0]);
					switch(primary) {
					/*case GameCommand.NULL:
						//TODO: command not found error
						break;*/
					case GameCommand.JOIN: 
					{
						this.sendCommandExclude(GameCommand.JOIN + " " + user.getGamePosition() + " " + user.getName(), user);
						for(Iterator<User> i2 = users.iterator();i2.hasNext();) {
							User u = i2.next();
							if(u!=user)
								this.sendCommand(GameCommand.JOIN + " " + u.getGamePosition() + " " + u.getName(), user);
						}
						if(this.getSize() == this.getGameSize()) {
							this.sendUpdateState(GameCommand.READY, host); // only need to send to host right?
						} else {
							this.sendUpdateState(GameCommand.WAITING, user);
						}
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
						this.updateStats();
						this.deal();
					}
					break;
					case GameCommand.PLAY_CARD:
					{

						if(this.state == GameCommand.DEALING) { //card being called
							Card played = Card.getCard(message[1],message[2]);
							int call_number = Integer.parseInt(message[3]);
							if(played.getNumber() == this.TRUMP_NUMBER || played.getSuit() == Card.TRUMP) { // make sure the call is valid 
								//card number for jokers might cause first half to return true, shouldn't matter
								if( user.getHand().frequency(played) >= call_number && (call_number > this.called_cards || call_number == this.called_cards && played.getSuit() == Card.TRUMP) ) {
									//TODO: differentiate between big and small jokers
									this.called_cards = call_number;
									this.TRUMP_SUIT = played.getSuit();
									this.caller = user;
									this.sendCommand(GameCommand.PLAY_CARD + " " + user.getName() + " " + played.getSuit() + " " + call_number);
								} else {
									System.out.println("illegal call");
								}
							}
						} else if(this.state == GameCommand.DIPAI) { // laying down dipai
							if(user != this.lead) {
								this.sendCommand(GameCommand.PLAY_INVALID + " calling out of turn", user);
								break;
							}
							if(Integer.parseInt(message[1]) != this.dipaiSize) {
								this.sendCommand(GameCommand.PLAY_INVALID + " not enough cards", user);
								break;
							}
							this.dipai = Collections.synchronizedList(new ArrayList<Card>());
							for(int k=0; k<this.dipaiSize*2; k+=2) {
								this.dipai.add(Card.getCard(message[k+2],message[k+3]));
							}
							this.sendUpdateState(GameCommand.PLAYING);
							this.setLead(lead);
							this.sendCommand(GameCommand.YOUR_TURN+"",lead);
							this.userIterator = this.users.iterator();
							this.currentUser = userIterator.next();
							System.out.println("lead is current user? "+(currentUser==lead));
							this.highest = currentUser;

						} else if(this.state == GameCommand.PLAYING) { //normally played
							if(user != currentUser) {
								this.sendCommand(GameCommand.PLAY_INVALID + " playing out of turn",user);
								break;
							}
							int numPlayed = Integer.parseInt(message[1]);
							ArrayList<Card> played = new ArrayList<Card>();
							for(int k=0;k<numPlayed*2; k+=2) {
								played.add(Card.getCard(message[k+2],message[k+3]));
							}




							if(user == this.lead) {
								//the player is the first player, check to make sure the play is high
								boolean isHigh=true;
								int suits=0;
								int length=2;
								List<Card> Tractor2=user.getHand().Tractors(0,length,suits);
								List<Card> Pairs=user.getHand().Pairs(0,suits);
								Card Singles=user.getHand().Singles(0,suits);
								for(Iterator<User> i2 = users.iterator();i2.hasNext();)
								{
									User u = i2.next();
									if(u != user)
									{
										PlayerHand friedchicken = u.getHand();
										List<Card> seven = friedchicken.getCards();

									}
								}
								
								
								
								//do this if invalid
								//sendCommand(GameCommand.PLAY_INVALID+" "+"error message",user);
								//break;
							} else {
								//not lead, check following suit, playing doubles/tractors/triples/whatever
								//compare to highest user's play
								//make sure the number of cards are correct
								
								
								

								//send this if invalid
								//sendCommand(GameCommand.PLAY_INVALID+" "+"error message",user);
								//break;
							}
							
							//TODO: sort the cards some time before here...
							//should only be here if the play was valid
							sendCommand(GameCommand.PLAY_SUCCESS+"",user);
							String tosend = GameCommand.PLAY_CARD + " " + user.getName() + " " + played.size();
							for(Card card : played) {
								tosend += " " + card.getSuit() + " " + card.getNumber();
							}
							sendCommandExclude(tosend,user);



							if(!userIterator.hasNext()) { 
								//add points etc
								if(currentUser.getHand().getCards().size() == 0) {
									//game over, update score
									break;
								}
								this.setLead(highest);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								sendCommand(GameCommand.CLEAR_TABLE+"");
								for(Iterator<User> i2 = users.iterator(); i.hasNext();) 
									i2.next().getHand().setCurrentPlay(null);
								userIterator = this.users.iterator();
							}
							this.currentUser = userIterator.next();
							this.sendCommand(GameCommand.YOUR_TURN+"",currentUser);
						} else {
							System.out.println("STRANGER DANGER");
						}
					}
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