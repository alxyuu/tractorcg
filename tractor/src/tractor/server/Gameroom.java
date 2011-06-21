package tractor.server;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import tractor.lib.Card;
import tractor.lib.GameCommand;
import tractor.lib.MessageFactory;

public class Gameroom extends Chatroom implements Runnable { // do I need a threadgroup? not really?
	private int players;
	private Thread gthread;
	private User host;
	private int state;
	private int TRUMP_SUIT = -1;
	private int TRUMP_NUMBER = -1;
	private int called_cards;
	private boolean firstgame;
	private User caller;
	private User lead;
	private int dipaiSize;
	private List<Card> dipai;
	private User currentUser;
	private Iterator<User> userIterator;
	private User highest;
	private int currentSuit;
	private Trick currentTrick;
	private int currentPoints;
	private int gamePoints;
	private CardComparator<Card> cardComparator;
	private List<User> team1;
	private List<User> team2;
	/** It constructs the game room.
	 * @param players
	 */
	public Gameroom(int players) {
		this.users = new CopyOnWriteArrayList<User>();
		this.players = players;
		this.setName("@"+this.hashCode());
		this.state = GameCommand.WAITING;
		this.firstgame = true;
		this.dipai = Collections.emptyList();
		this.team1 = new ArrayList<User>();
		this.team2 = new ArrayList<User>();
		this.cardComparator = new CardComparator<Card>() {
			/**
			 * gets the suit used for sorting
			 * @return
			 */
			private int getSortingSuit(Card card) {
				if (card.getSuit() == Card.TRUMP)
					return Card.TRUMP+Card.TRUMP+2;
				else if(card.getSuit() == TRUMP_SUIT && card.getNumber() == TRUMP_NUMBER)
					return Card.TRUMP+Card.TRUMP+1;
				else if(card.getNumber() == TRUMP_NUMBER)
					return Card.TRUMP+card.getSuit()+1;
				else if(card.getSuit() == TRUMP_SUIT)
					return Card.TRUMP;
				else
					return card.getSuit();
			}

			/**
			 * gets the value of the card in the current game
			 * @param card
			 * @return
			 */
			private int getGameValue(Card card) {
				if (card.getSuit() == Card.TRUMP)
					if(card.getNumber() == Card.BIG_JOKER)
						return Card.TRUMP+4; // 8
					else
						return Card.TRUMP+3; // 7
				else if(card.getSuit() == TRUMP_SUIT && card.getNumber() == TRUMP_NUMBER)
					return Card.TRUMP+2; // 6
				else if(card.getNumber() == TRUMP_NUMBER)
					return Card.TRUMP+1; // 5
				else if(card.getSuit() == TRUMP_SUIT)
					return Card.TRUMP; // 4
				else
					return card.getSuit();
			}

			/** It gets the sorting value of the card.
			 * @return
			 */
			private int getSortingValue(Card card) {
				//return (card.getNumber() == TRUMP_NUMBER) ? ((card.getSuit() == TRUMP_SUIT) ? Card.SET_TRUMP_NUMBER : Card.SET_TRUMP) : card.getNumber();
				//set trump sorting was taken care of in getSortingSuit, I think
				return card.getNumber();
			}

			public int compare(Card c1, Card c2) {
				if(getSortingSuit(c1) == getSortingSuit(c2)) {
					return getSortingValue(c1) - getSortingValue(c2);
				} else {
					return getSortingSuit(c1) - getSortingSuit(c2);
				}
			}

			public int gameCompare(Card c1, Card c2) {
				int value1 = getGameValue(c1);
				int value2 = getGameValue(c2);
				if(value1 == value2) {
					int toreturn = c1.getNumber() - c2.getNumber();
					if(toreturn > 0 && TRUMP_NUMBER < c1.getNumber() && TRUMP_NUMBER > c2.getNumber())
						return toreturn -1;
					else if(toreturn < 0 && TRUMP_NUMBER > c1.getNumber() && TRUMP_NUMBER < c2.getNumber())
						return toreturn +1;
					else
						return toreturn;
				} else {
					if(value1 >= 4 && value2 >= 4) {
						if(value1 == 4) {
							int decrease = (Card.ACE-1) - c1.getNumber();
							if(TRUMP_NUMBER < c1.getNumber()) {
								decrease++;
							}
							return value1-decrease-value2;
						} else if(value2 == 4) {
							int decrease = (Card.ACE-1) - c2.getNumber();
							if(TRUMP_NUMBER < c2.getNumber()) {
								decrease++;
							}
							return value1+decrease-value2;
						} else {
							return value1-value2;
						}
					} else {
						return 9999;
					}
				}
			}
		};
	}
	
	public void start() {
		this.gthread = new Thread(this,this.getName());
		this.gthread.start();
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
			Collections.rotate(this.users, 0-index);
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
						sendCommand(GameCommand.DEALING + " " + user.getName() + " " + todeal.getSuit() + " " + todeal.getNumber(), user);
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
					lead.getHand().addCard(card);
				}
				sendCommand(GameCommand.DIPAI + dipai, lead);
				sendUpdateState(GameCommand.DIPAI);
				sendCommand(GameCommand.CLEAR_TABLE+" 0");
			}
		};
		dealing.start();
	}
	
	public void addPoints(List<Card> cards) {
		for(Card card : cards) {
			if(card.getNumber() == Card.KING || card.getNumber() == Card.TEN)
				this.currentPoints += 10;
			else if (card.getNumber() == Card.FIVE)
				this.currentPoints += 5;
		}
	}
	
	public Trick calculateTrick(List<Card> played) {
		Trick trick = new Trick();
		//calculate stuffs
		Iterator<Card> it=played.iterator();
		Card previous=it.next();
		ArrayList<Card> previousCards=new ArrayList<Card>();
		previousCards.add(previous);
		Card twoPrevious=null;
		while(it.hasNext())
		{
			Card current=it.next();
			if(current==previous) //if they are equal add to list for possible pair/triple/tractor
			{
				previousCards.add(current);
				twoPrevious=previous;
				previous=current;
			}
			else
			{
				if( cardComparator.gameCompare(current, previous) == 1 )
				{
					if(previousCards.size()==1) //if only one other card held then its just a single so get rid of the old card and add the new one
					{
						trick.addSingle(previous);
						previousCards.clear();
						previousCards.add(current);
						twoPrevious=null;
						previous=current;
					}
					else if(twoPrevious!=previous) //if the last two cards aren't equal then the there is no tractor possibility so remove previous
					{
						trick.addSingle(previous);
						previousCards.remove(previousCards.size()-1);
						if(previousCards.size()==1) //if there's only one other card then add it as a single
						{
							trick.addSingle(twoPrevious);
							previousCards.clear();
							previousCards.add(current);
							twoPrevious=null;
							previous=current;

						}
						else if(previousCards.size()==2) //if there was two add as pair
						{
							trick.addPair(twoPrevious);
							previousCards.clear();
							previousCards.add(current);
							twoPrevious=null;
							previous=current;
						}
						else if(previousCards.size()==3) //if there are 3 left then its a triple
						{
							trick.addTriple(twoPrevious);
							previousCards.clear();
							previousCards.add(current);
							twoPrevious=null;
							previous=current;
						}
						else //otherwise if theres more its a tractor
						{
							int pairCount=0;
							int tripleCount=0;
							Iterator<Card> it2=previousCards.iterator();
							Card first=it2.next();
							it2.next(); // skip one because we know it's a tractor
							Card before=first;
							Card current2=first;
							while(true)
							{
								current2=it2.next();
								if(before==current2)     //if third card equals first then triple
								{
									tripleCount++;
									if(it2.hasNext())
									{
										before=it2.next();
										it2.next();
										continue;
									} else {
										break;
									}
								}
								else				//third and first different
								{
									pairCount++;
									before=current2;
								}
								it2.next();
								if(!it2.hasNext()) {
									pairCount++;
									break;
								}
							}

							trick.addTractor(new Tractor(pairCount, tripleCount, first.getNumber(), first.getSuit(), Math.abs(cardComparator.gameCompare(first,current2))+1));
							previousCards.clear();
							previousCards.add(current);
							twoPrevious=null;
							previous=current;
						}
					}
					else {
						previousCards.add(current);
						twoPrevious=previous;
						previous = current;
					}

				}
				else //no tractor/pairs/triples
				{
					//why? there could be a pair after
					//trick.addSingle(current);
					if(previousCards.size()==1) //if there's only one other card then add it as a single
					{
						trick.addSingle(previous);
						previousCards.clear();
						previousCards.add(current);
						previous=current;

					}
					else if(previousCards.size()==2) //if there was two add as pair
					{
						trick.addPair(previous);
						previousCards.clear();
						previousCards.add(current);
						previous=current;
					}
					else if(previousCards.size()==3) //if there are 3 left then its a triple
					{
						trick.addTriple(previous);
						previousCards.clear();
						previousCards.add(current);
						previous=current;
					}
					else //otherwise if theres more its a tractor
					{
						int pairCount=0;
						int tripleCount=0;
						Iterator<Card> it2=previousCards.iterator();
						Card first=it2.next();
						it2.next(); // skip one because we know it's a tractor
						Card before=first;
						Card current2=first;
						while(true)
						{
							current2=it2.next();
							if(before==current2)     //if third card equals first then triple
							{
								tripleCount++;
								if(it2.hasNext())
								{
									before=it2.next();
									it2.next();
									continue;
								} else {
									break;
								}
							}
							else				//third and first different
							{
								pairCount++;
								before=current2;
							}
							it2.next();
							if(!it2.hasNext()) {
								pairCount++;
								break;
							}
						}
						trick.addTractor(new Tractor(pairCount, tripleCount, first.getNumber(), first.getSuit(),Math.abs(cardComparator.gameCompare(first,current2))+1));
						previousCards.clear();
						previousCards.add(current);
						twoPrevious=null;
						previous=current;
					}

				}
			}

		}

		//take care of the last set
		if(previousCards.size()==0) {
			System.out.println("some bad shit happened");
		}
		if(previousCards.size()==1) //if only one other card held then its just a single so get rid of the old card and add the new one
		{
			trick.addSingle(previous);
			previousCards.clear();
		}
		else if(twoPrevious!=previous) //if the last two cards aren't equal then the there is no tractor possibility so remove previous
		{
			trick.addSingle(previous);
			previousCards.remove(previousCards.size()-1);
			if(previousCards.size()==1) //if there's only one other card then add it as a single
			{
				trick.addSingle(twoPrevious);
				previousCards.clear();
			}
			else if(previousCards.size()==2) //if there was two add as pair
			{
				trick.addPair(twoPrevious);
				previousCards.clear();
			}
			else if(previousCards.size()==3) //if there are 3 left then its a triple
			{
				trick.addTriple(twoPrevious);
				previousCards.clear();
			}
			else //otherwise if theres more its a tractor
			{
				int pairCount=0;
				int tripleCount=0;
				Iterator<Card> it2=previousCards.iterator();
				Card first=it2.next();
				it2.next(); // skip one because we know it's a tractor
				Card before=first;
				Card current2=first;
				while(true)
				{
					current2=it2.next();
					if(before==current2)     //if third card equals first then triple
					{
						tripleCount++;
						if(it2.hasNext())
						{
							before=it2.next();
							it2.next();
							continue;
						} else {
							break;
						}
					}
					else				//third and first different
					{
						pairCount++;
						before=current2;
					}
					it2.next();
					if(!it2.hasNext()) {
						pairCount++;
						break;
					}
				}

				trick.addTractor(new Tractor(pairCount, tripleCount, first.getNumber(), first.getSuit(), Math.abs(cardComparator.gameCompare(first,current2))+1));
				previousCards.clear();
			}
		}
		else {
			if(previousCards.size()==2) //if there was two add as pair
			{
				trick.addPair(twoPrevious);
				previousCards.clear();
			}
			else if(previousCards.size()==3) //if there are 3 left then its a triple
			{
				trick.addTriple(twoPrevious);
				previousCards.clear();
			}
			else //otherwise if theres more its a tractor
			{
				int pairCount=0;
				int tripleCount=0;
				Iterator<Card> it2=previousCards.iterator();
				Card first=it2.next();
				it2.next(); // skip one because we know it's a tractor
				Card before=first;
				Card current2=first;
				while(true)
				{
					current2=it2.next();
					if(before==current2)     //if third card equals first then triple
					{
						tripleCount++;
						if(it2.hasNext())
						{
							before=it2.next();
							it2.next();
							continue;
						} else {
							break;
						}
					}
					else				//third and first different
					{
						pairCount++;
						before=current2;
					}
					it2.next();
					if(!it2.hasNext()) {
						pairCount++;
						break;
					}
				}

				trick.addTractor(new Tractor(pairCount, tripleCount, first.getNumber(), first.getSuit(), Math.abs(cardComparator.gameCompare(first,current2))+1));
				previousCards.clear();
			}
		}
		
		return trick;
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
					CommandSwitch: switch(primary) {
					/*case GameCommand.NULL:
						//TODO: command not found error
						break;*/
					case GameCommand.JOIN: 
					{
						System.out.println("join");
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
							//System.out.println(Arrays.toString(message));
							for(int k=0; k<this.dipaiSize*2; k+=2) {
								Card dpcard = Card.getCard(message[k+2],message[k+3]);
								this.dipai.add(dpcard);
								user.getHand().removeCard(dpcard);
							}
							//System.out.println(this.dipai);
							
							//sort dem cards
							for(Iterator<User> i2 = users.iterator();i2.hasNext();)
							{
								User temp = i2.next();
								temp.getHand().sort(cardComparator);
								System.out.println(temp.getHand().getCards());
							}
							
							
							//manually set teams for now, modify for find a friend later
							this.team1.clear();
							this.team2.clear();
							this.team1.add(users.get(0));
							this.team1.add(users.get(2));
							this.team2.add(users.get(1));
							this.team2.add(users.get(3));
							users.get(0).setTeam(team1);
							users.get(2).setTeam(team1);
							users.get(1).setTeam(team2);
							users.get(3).setTeam(team2);
							
							this.currentPoints = 0;
							this.gamePoints = 0;
							
							
							this.sendUpdateState(GameCommand.PLAYING);
							this.setLead(lead);
							this.sendCommand(GameCommand.YOUR_TURN+"",lead);
							this.userIterator = this.users.iterator();
							this.currentUser = userIterator.next();
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
							if(played.size() == 0) {
								sendCommand(GameCommand.PLAY_INVALID+" no cards played!",user);
								break;
							}
							System.out.println(user.getHand().getCards());
							if(!user.getHand().contains(played)) {
								sendCommand(GameCommand.PLAY_INVALID+" cheating the system, cards not in your hand!",user);
								break;
							}
							Collections.sort(played,cardComparator);
								



							if(user == this.lead) {
								//the player is the first player, check to make sure the play is high

								System.out.println("LEAD VERIFICATION");
								Iterator<Card> it = played.iterator();
								Card card = it.next();
								int suit = (card.getNumber() == this.TRUMP_NUMBER || card.getSuit() == this.TRUMP_SUIT) ? Card.TRUMP : card.getSuit();
								while(it.hasNext()) {
									card = it.next();
									//if it's not the same suit and both this and the previous suit aren't trump
									if( !( 
											( suit == card.getSuit()  && (suit != Card.TRUMP && card.getNumber() != TRUMP_NUMBER) ) ||
											(card.getSuit() == this.TRUMP_SUIT || card.getSuit() == Card.TRUMP || card.getNumber() == this.TRUMP_NUMBER) && suit == Card.TRUMP
									) ) 
									{
										sendCommand(GameCommand.PLAY_INVALID+" must play same suit",user);
										break CommandSwitch;
									}
									//suit = ( card.getNumber() == this.TRUMP_NUMBER ) ? Card.TRUMP : card.getSuit();
								}

								Trick trick = calculateTrick(played);
								System.out.println(trick);

								//check if high only if there's more than one play
								if(trick.countPlays() > 1) {

									//just check the first in each play, since they're all sorted
									if(trick.countSingles() > 0) {
										Card single = trick.getSingles().get(0);
										if(single.getSuit() != TRUMP_SUIT && single.getNumber() != TRUMP_NUMBER && single.getSuit() != Card.TRUMP) {
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													for(int k=single.getNumber()+1; k<=Card.ACE; k++) {
														if(k != TRUMP_NUMBER && u.getHand().contains(Card.getCard(single.getSuit(),k))) {
															sendCommand(GameCommand.PLAY_INVALID+" not high (normal single found)",user);
															break CommandSwitch;
														}
													}
												}
											}
										} else {
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													List<Card> cards = u.getHand().getCards();
													//just check the highest card since it's sorted
													if( cardComparator.gameCompare(single,cards.get(cards.size()-1)) < 0 ) {
														sendCommand(GameCommand.PLAY_INVALID+" not high (trump single found)",user);
														break CommandSwitch;
													}
												}
											}
										}
									}
									if(trick.countPairs() > 0) {
										Card pair = trick.getPairs().get(0);
										if(pair.getSuit() != TRUMP_SUIT && pair.getNumber() != TRUMP_NUMBER && pair.getSuit() != Card.TRUMP) {
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													for(int k=pair.getNumber()+1; k<=Card.ACE; k++) {
														if(k != TRUMP_NUMBER && u.getHand().frequency(Card.getCard(pair.getSuit(),k)) >= 2) {
															sendCommand(GameCommand.PLAY_INVALID+" not high (normal pair found)",user);
															break CommandSwitch;
														}
													}
												}
											}
										} else {
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													ListIterator<Card> cards = u.getHand().getCards().listIterator(u.getHand().getCards().size());
													//assuming the hand isn't empty
													Card previous = cards.previous();
													cardComparator.gameCompare(pair,previous);
													while( cards.hasPrevious() ) {
														Card current = cards.previous();
														//too lazy to optimize for triples
														if(current == previous) {
															if(cardComparator.gameCompare(pair,previous) < 0) {
																sendCommand(GameCommand.PLAY_INVALID+" not high (trump pair found)",user);
																break CommandSwitch;
															}
															//they're smaller now, just get out.
															break;
														}
														previous = current;
													}
												}
											}
										}
									}
									if(trick.countTriples() > 0) {
										Card triple = trick.getTriples().get(0);
										if(triple.getSuit() != TRUMP_SUIT && triple.getNumber() != TRUMP_NUMBER && triple.getSuit() != Card.TRUMP) {
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													for(int k=triple.getNumber()+1; k<=Card.ACE; k++) {
														if(k != TRUMP_NUMBER && u.getHand().frequency(Card.getCard(triple.getSuit(),k)) >= 3) {
															sendCommand(GameCommand.PLAY_INVALID+" not high (normal triple found)",user);
															break CommandSwitch;
														}
													}
												}
											}
										} else {
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													ListIterator<Card> cards = u.getHand().getCards().listIterator(u.getHand().getCards().size());
													//assuming the hand isn't empty
													Card previous = cards.previous();
													Card twoPrevious = null;
													while( cards.hasPrevious() ) {
														Card current = cards.previous();
														//too lazy to optimize, just move one at a time
														if(current == previous && previous == twoPrevious) {
															if(cardComparator.gameCompare(triple,previous) < 0) {
																sendCommand(GameCommand.PLAY_INVALID+" not high (trump triple found)",user);
																break CommandSwitch;
															}
															//they're smaller now, just get out.
															break;
														}
														twoPrevious = previous;
														previous = current;
													}
												}
											}
										}
									}

								} else if (trick.countPlays() == 0) { // this should never happen...
									System.out.println("some bad shit happened");
									return;
								}


								//the trick is valid so set it
								this.currentTrick = trick;
								//assume lead is always high until beaten by someone else
								this.highest = user;
								//don't set suit until after the play has been verified as high
								this.currentSuit = suit;
							} else {
								System.out.println("FOLLOWER VERIFICATION");
								//not lead, check following suit, playing doubles/tractors/triples/whatever
								//compare to highest user's play
								//make sure the number of cards are correct
								if(played.size() != this.currentTrick.countCards()) {
									sendCommand(GameCommand.PLAY_INVALID+" wrong number of cards",user);
									break CommandSwitch;
								}
								
								
								//LET'S DO RANDOM SHIT INSTEAD OF CALCULATING WHO'S HIGH
								int index = (int)(4*Math.random());
								this.highest = this.users.get(index);


							}
							
							//should only be here if the play was valid
							this.addPoints(played);
							user.getHand().removeAllCards(played);
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
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								if(highest.getTeam() != this.team1) //if team is not the one with dipai
									this.gamePoints += this.currentPoints;
								this.currentPoints = 0;
								for(Iterator<User> i2 = users.iterator(); i.hasNext();) 
									i2.next().getHand().setCurrentPlay(null);
								this.sendCommand(GameCommand.CLEAR_TABLE+" "+gamePoints);
								this.setLead(highest);
								this.userIterator = this.users.iterator();
								
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