package tractor.server;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;
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
	private boolean trumped;
	private CardComparator<Card> cardComparator;
	private Comparator<Tractor> tractorComparator;
	private Team team1;
	private Team team2;
	private Team defending;
	private Team attacking;
	private int decks;
	/** It constructs the game room.
	 * @param players
	 */
	public Gameroom(int players, int decks) {
		if(decks < 1 || decks > 3 || players < 1) {
			throw new IllegalArgumentException("shit");
		}
		
		this.decks = decks;
		this.users = new CopyOnWriteArrayList<User>();
		this.players = players;
		this.setName("@"+this.hashCode());
		this.state = GameCommand.WAITING;
		this.firstgame = true;
		this.dipai = Collections.emptyList();
		this.team1 = new Team();
		this.team2 = new Team();
		
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
				try {
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
				} catch (NullPointerException e) {
					return 9999;
				}
			}
		};
	
		this.tractorComparator = new Comparator<Tractor>() {
			public int compare(Tractor t1, Tractor t2) {
				if(t1.getType() > t2.getType() && t1.getLength() >= t2.getLength() || t1.getType() >= t2.getType() && t1.getLength() > t2.getLength()) {
					return 1;
				} else if (t1.getType() < t2.getType() || t1.getLength() < t2.getLength()) {
					return -1;
				} else {
					return cardComparator.compare(t1.getStartingCard(), t2.getStartingCard());
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
	
	private void sendUpdateState(int state, String message) {
		this.sendCommand(GameCommand.UPDATE_STATE + " " + state + " " + message);
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
		this.trumped = false;
		this.gamePoints = 0;
		this.called_cards = 0;
		this.caller = null;
		this.updateStats();
		this.sendUpdateState(GameCommand.DEALING);
		Thread dealing = new Thread("dealing-"+this.getName()) {
			public void run() {
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
				
				//TODO: sleep for a few seconds after a card is called for chance to overturn
				//while loop will be removed when dipai flipping is implemented
				while( caller == null ) {
					try {
						Thread.sleep(1000);	
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}

				if(firstgame) {
					setLead(caller);
					firstgame = false;
				}

				dipaiSize = cards.size();
				//TODO: flip dipai if no one calls
				String dipai = ""+cards.size();
				for(Card card : cards) {
					dipai += " "+card.getSuit()+" "+card.getNumber();
					lead.getHand().addCard(card);
				}
				sendCommand(GameCommand.DIPAI + " " + lead.getName() + " " + dipai, lead);
				
				sendCommandExclude(GameCommand.DIPAI + " " + lead.getName(), lead);
				
				sendUpdateState(GameCommand.DIPAI);
				sendCommand(GameCommand.CLEAR_TABLE+" 0");
			}
		};
		dealing.start();
	}
	
	public void addPoints(List<Card> cards) {
		this.addPoints(cards,1);
	}
	public void addPoints(List<Card> cards, int multiplier) {
		for(Card card : cards) {
			if(card.getNumber() == Card.KING || card.getNumber() == Card.TEN)
				this.currentPoints += 10*multiplier;
			else if (card.getNumber() == Card.FIVE)
				this.currentPoints += 5*multiplier;
		}
	}
	
	private void addCardsToTrick(List<Card> cardlist, Trick trick, int size) {
		if(cardlist.size() >= 2) {
			trick.addTractor(new Tractor(size, cardlist));
		} else {
			for(Card c : cardlist) {
				switch(size) {
					case 1:
						trick.addSingle(c); //shouldn't ever happen though...
						break;
					case 2:
						trick.addPair(c);
						break;
					case 3:
						trick.addTriple(c);
						break;
					default:
						System.out.println("something went terribly terribly wrong");
				}
			}
		}
	}

	public Trick calculateTrick(List<Card> played) {
		played.add(null); //trick the while loop into going one more round...
		Trick trick = new Trick(this.cardComparator, this.tractorComparator);
		//calculate stuffs
		Iterator<Card> it=played.iterator();
		Card previous=it.next();
		ArrayList<Card> cardlist = new ArrayList<Card>();
		cardlist.add(previous);
		int currentsize = 1;
		int maxsize = 1;
		while(it.hasNext())
		{
			Card current=it.next();
			if(current==previous) //if they are equal add to list for possible pair/triple/tractor
			{
				currentsize++;
			}
			else
			{
				if(maxsize == 1)
					maxsize = currentsize;
					
				if(maxsize != currentsize) 
				{
					Card temp = cardlist.remove(cardlist.size()-1);
					this.addCardsToTrick(cardlist, trick, maxsize);
					cardlist.clear();
					maxsize = currentsize;
					if(currentsize == 1) {
						trick.addSingle(temp);
					} else {
						cardlist.add(temp);
					}
				}
				else
				{
					
					if( cardComparator.gameCompare(current, previous) == 1 )
					{
						if(currentsize == 1) 
						{
							trick.addSingle(previous);
							cardlist.clear();
						}
					} 
					else //skipped a card, current card can't be part of tractor
					{ 
						if(  cardlist.size() < 2 || cardComparator.gameCompare(cardlist.get(cardlist.size()-1), cardlist.get(cardlist.size()-2)) == 1 ) {
							this.addCardsToTrick(cardlist, trick, maxsize);
							cardlist.clear();
							maxsize = 1;
						} else {
							Card temp = cardlist.remove(cardlist.size()-1);							
							this.addCardsToTrick(cardlist, trick, maxsize);
							cardlist.clear();
							maxsize = currentsize;
							if(currentsize == 1) {
								trick.addSingle(temp);
							} else {
								cardlist.add(temp);
							}
						}
					}

					
					
				}
				if(current != null) {
					cardlist.add(current);
					currentsize = 1;
				} else {
					this.addCardsToTrick(cardlist, trick, maxsize);
					break;
				}
			}
			previous=current;
		}
		
		played.remove(played.size()-1); //remove the null we put in
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
						//System.out.println("join");
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
						this.setLead(host);
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
								if( user.getHand().frequency(played) >= call_number && 
									(call_number > this.called_cards || 
										call_number >= 2 && 
										call_number >= this.called_cards && 
										played.getSuit() == Card.TRUMP) &&
									(user != this.caller ||
										played.getSuit() == this.TRUMP_SUIT)
									) {
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
								break CommandSwitch;
							}
							if(Integer.parseInt(message[1]) != this.dipaiSize) {
								this.sendCommand(GameCommand.PLAY_INVALID + " not enough cards", user);
								break CommandSwitch;
							}
							this.dipai = Collections.synchronizedList(new ArrayList<Card>());
							//System.out.println(Arrays.toString(message));
							for(int k=0; k<this.dipaiSize*2; k+=2) {
								Card dpcard = Card.getCard(message[k+2],message[k+3]);
								this.dipai.add(dpcard);
								user.getHand().getCards().remove(dpcard);
							}
							//System.out.println(this.dipai);
							
							//sort dem cards
							for(Iterator<User> i2 = users.iterator();i2.hasNext();)
							{
								User temp = i2.next();
								temp.getHand().sort(cardComparator);
								//System.out.println(temp.getHand().getCards());
							}
							
							//calculate sets in hand
							for(User u : users) {
								Trick trick = calculateTrick(u.getHand().getCards());
								TreeSet<Tractor> tractors = trick.getTractors();
								TreeSet<Card> pairs = trick.getPairsPlusTractors();
								TreeSet<Card> triples = trick.getTriplesPlusTractors();
								TreeSet<Card> combined = new TreeSet<Card>(cardComparator);
								TreeSet<Tractor> mixed = new TreeSet<Tractor>(tractorComparator);
								combined.addAll(pairs);
								combined.addAll(triples);
								mixed.addAll(tractors);
								
								Iterator<Card> it = combined.iterator();
								Card current = it.next();
								
								while(it.hasNext()) {
									
									//TODO: what if the person has 2 non trump suit trump numbers...?
									Card previous;
									List<Card> tractorcards = new LinkedList<Card>();
									do {
										tractorcards.add(current);
										previous = current;
										current = it.next();
									} while ( it.hasNext() && cardComparator.gameCompare(current, previous) == 1 );
									if(cardComparator.gameCompare(current, previous) == 1)
										tractorcards.add(current);
									
									if(tractorcards.size() >= 2) {
										Tractor t = new Tractor(2, tractorcards);
										Tractor t3 = new Tractor(3, tractorcards);
										if(!tractors.contains(t) && !tractors.contains(t3)) {
											//pairs.removeAll(tractorcards);
											//don't need to remove, no duplicates in a set
											//pairs.addAll(tractorcards);
											mixed.add(t);
										}
									}
									tractorcards.clear();
								}
								
								//remove any triples from the pairs
								
								
								//TODO: 
								u.getHand().init(pairs, triples, tractors, mixed);
								System.out.println(u.getName() + "'s Hand: \n " + u.getHand());
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
							
							int index = users.indexOf(lead)-1;
							if(index < 0) //can't possibly be not found...
								index += users.size();
							if(lead.getTeam() == team1) {
								team1.setCurrentUser(lead);
								team2.setCurrentUser(users.get(index));
								this.defending = team1;
								this.attacking = team2;
							} else {
								team2.setCurrentUser(lead);
								team1.setCurrentUser(users.get(index));
								this.defending = team2;
								this.attacking = team1;
							}
							
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
								break CommandSwitch;
							}
							int numPlayed = Integer.parseInt(message[1]);
							LinkedList<Card> played = new LinkedList<Card>();
							for(int k=0;k<numPlayed*2; k+=2) {
								played.add(Card.getCard(message[k+2],message[k+3]));
							}
							if(played.size() == 0) {
								sendCommand(GameCommand.PLAY_INVALID+" no cards played!",user);
								break CommandSwitch;
							}
							//System.out.println(user.getHand().getCards());
							if(!user.getHand().contains(played)) {
								sendCommand(GameCommand.PLAY_INVALID+" cheating the system, cards not in your hand!",user);
								break CommandSwitch;
							}
							Collections.sort(played,cardComparator);
								



							if(user == this.lead) {
								//the player is the first player, check to make sure the play is high

								System.out.println("LEAD VERIFICATION FOR: "+user.getName());
								Iterator<Card> it = played.iterator();
								Card card = it.next();
								int suit = (card.getNumber() == this.TRUMP_NUMBER || card.getSuit() == this.TRUMP_SUIT) ? Card.TRUMP : card.getSuit();
								this.trumped = false;
								
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
								CheckPlay: while(trick.countPlays() > 1) {
									
									//can't just throw trump
									if( suit == Card.TRUMP ) {
										sendCommand(GameCommand.PLAY_INVALID+" can not throw trump",user);
										break CommandSwitch;
									}
									
									//just check the first in each play, since they're all sorted
									if(trick.countSingles() > 0) {
										Card single = trick.getSingles().get(0);
										for(Iterator<User> i2 = users.iterator();i2.hasNext();)
										{
											User u = i2.next();
											if(u != user)
											{
												for(int k=single.getNumber()+1; k<=Card.ACE; k++) {
													if(k != TRUMP_NUMBER && u.getHand().contains(Card.getCard(single.getSuit(),k))) {
														//sendCommand(GameCommand.PLAY_INVALID+" not high (normal single found)",user);
														//break CommandSwitch;
														played.clear();
														played.add(single);
														trick = new Trick(this.cardComparator, this.tractorComparator);
														trick.addSingle(single);
														break CheckPlay;
													}
												}
											}
										}
									}
									if(trick.countPairs() > 0) {
										Card pair = trick.getPairs().first();
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													for(int k=pair.getNumber()+1; k<=Card.ACE; k++) {
														if(k != TRUMP_NUMBER && u.getHand().frequency(Card.getCard(pair.getSuit(),k)) >= 2) {
															//sendCommand(GameCommand.PLAY_INVALID+" not high (normal pair found)",user);
															//break CommandSwitch;
															played.clear();
															played.add(pair);
															played.add(pair);
															trick = new Trick(this.cardComparator, this.tractorComparator);
															trick.addPair(pair);
															break CheckPlay;
														}
													}
												}
											}
									}
									if(trick.countTriples() > 0) {
										Card triple = trick.getTriples().first();
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													for(int k=triple.getNumber()+1; k<=Card.ACE; k++) {
														if(k != TRUMP_NUMBER && u.getHand().frequency(Card.getCard(triple.getSuit(),k)) >= 3) {
															//sendCommand(GameCommand.PLAY_INVALID+" not high (normal triple found)",user);
															//break CommandSwitch;
															played.clear();
															played.add(triple);
															played.add(triple);
															played.add(triple);
															trick = new Trick(this.cardComparator, this.tractorComparator);
															trick.addTriple(triple);
															break CheckPlay;
														}
													}
												}
											}
									}
									if(trick.countTractors() > 0) {
										// O(n^2)....
										for ( Tractor tractor : trick.getTractors() ) {
											for(Iterator<User> i2 = users.iterator();i2.hasNext();)
											{
												User u = i2.next();
												if(u != user)
												{
													for(Iterator<Tractor> it2 = u.getHand().getMixedTractors().iterator(); it.hasNext();) {
															Tractor comp = it2.next();
															if( comp.getStartingCard().getGameSuit(this.TRUMP_SUIT,this.TRUMP_NUMBER) == tractor.getStartingCard().getGameSuit(this.TRUMP_SUIT,this.TRUMP_NUMBER) && comp.getLength() >= tractor.getLength() && cardComparator.gameCompare(comp.getStartingCard(), tractor.getStartingCard()) >= 1 ) {
																//sendCommand(GameCommand.PLAY_INVALID+" not high (normal tractor found)",user);
																//break CommandSwitch;
																played.clear();
																for(Card c : tractor.getCards()) {
																	played.add(c);
																}
																trick = new Trick(this.cardComparator, this.tractorComparator);
																trick.addTractor(tractor);
																break CheckPlay;
															}
													}
												}
											}
										}
									}
									break CheckPlay;
								}
								
								if (trick.countPlays() == 0) { // this should never happen...
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
								System.out.println("FOLLOWER VERIFICATION FOR: "+user.getName());
								//not lead, check following suit, playing doubles/tractors/triples/whatever
								//compare to highest user's play
								//make sure the number of cards are correct
								if(played.size() != this.currentTrick.countCards()) {
									sendCommand(GameCommand.PLAY_INVALID+" wrong number of cards",user);
									break CommandSwitch;
								}
								
								//make sure following suit
								int cards_following_suit = 0;
								for(Card card : played) {
									if( card.getNumber() != TRUMP_NUMBER && card.getSuit() == this.currentSuit || (this.currentSuit == Card.TRUMP && (card.getNumber() == this.TRUMP_NUMBER || card.getSuit() == this.TRUMP_SUIT || card.getSuit() == Card.TRUMP))) {
										cards_following_suit++;
									}
								}
								boolean following_suit = cards_following_suit == this.currentTrick.countCards();
								System.out.println("following suit: "+following_suit);
								
								//check if void
								boolean void_in_suit = false;
								if(!following_suit) {
									if(this.currentSuit == Card.TRUMP && user.getHand().getNumTrump(TRUMP_SUIT, TRUMP_NUMBER) > cards_following_suit || this.currentSuit != Card.TRUMP && user.getHand().getNumSuit(this.currentSuit, this.TRUMP_NUMBER) > cards_following_suit) {
										sendCommand(GameCommand.PLAY_INVALID+" not following suit",user);
										break CommandSwitch;
									} else {
										void_in_suit = true;
									}
								}
								System.out.println("void in suit: "+void_in_suit);
								
								//check if all trump but only if void and current play is not trump
								boolean all_trump = false;
								if(void_in_suit && this.currentSuit != Card.TRUMP) {
									all_trump = true;
									for( Card card : played ) {
										if( !( card.getNumber() == TRUMP_NUMBER || card.getSuit() == Card.TRUMP || card.getSuit() == TRUMP_SUIT ) ) {
											all_trump = false;
											break;
										}
									}
								}
								System.out.println("all trump: "+all_trump);
								
								Trick trick = calculateTrick(played);
								System.out.println(trick);
								
								CheckPlay: while ( following_suit || all_trump ) { //dirty...dirty hack
									
									boolean skipTractorCheck = false;
									boolean following_play = true;
									
									//convert extra tractors to triples and pairs
									if(trick.countTractors() > this.currentTrick.countTractors()) {
										
										TreeSet<Tractor> tricktractors = trick.getTractors();
										List<Tractor> temp = new LinkedList<Tractor>();
										
										for(Tractor tractor : this.currentTrick.getTractors()) {
											skipTractorCheck = false;
											for(Iterator<Tractor> it = tricktractors.iterator(); it.hasNext();) {
												Tractor temp1 = it.next();
												if(tractorComparator.compare(temp1,tractor) > 0) {
													skipTractorCheck = true;
													it.remove();
													temp.add(temp1);
													break;
												}
											}
											if(!skipTractorCheck) {
												following_play = false;
												break;
											}
										}
										
										for(Iterator<Tractor> it = tricktractors.iterator(); it.hasNext();) {
											Tractor temp1 = it.next();
											if(temp1.getType() == 3) {
												for(Card card : temp1.getCards()) {
													trick.addTriple(card,false);
												}
											} else {
												for(Card card : temp1.getCards()) {
													trick.addPair(card,false);
												}
											}
											it.remove();
										}
										
										tricktractors.addAll(temp);
										
									}
									
									
									
									//System.out.println("played triples: " + trick.countTriplesPlusTractors());
									if(trick.countTriplesPlusTractors() < this.currentTrick.countTriplesPlusTractors()) {
										if( following_suit ) {
											int triples = 0;
											for( Card triple : user.getHand().getTriples() ) {
												if(triple.getGameSuit(this.TRUMP_SUIT, this.TRUMP_NUMBER) == this.currentSuit) {
													System.out.println("Triple in hand: "+triple);
													triples++;
												}
											}
											//System.out.println("triples: "+triples);
											if(triples > trick.countTriplesPlusTractors()) {
												sendCommand(GameCommand.PLAY_INVALID+" must play triples",user);
												break CommandSwitch;
											}
											if(trick.countPairsPlusTractors() + trick.countTriplesPlusTractors() < this.currentTrick.countTriplesPlusTractors() + this.currentTrick.countPairsPlusTractors()) {
												int pairs = 0;
												for( Card pair : user.getHand().getPairs() ) {
													if(pair.getGameSuit(this.TRUMP_SUIT, this.TRUMP_NUMBER) == this.currentSuit) {
														System.out.println("Pair in hand: "+pair);
														pairs++;
													}
												}
												if( pairs > trick.countPairsPlusTractors() ) {
													sendCommand(GameCommand.PLAY_INVALID+" must play pairs",user);
													break CommandSwitch;
												}
											}
										}
										following_play = false;
									} else if ( trick.countTriples() > this.currentTrick.countTriples() ) {
										//convert extra triples to pairs and singles
										do {
											trick.tripleToPair(trick.getTriples().first());
										} while(trick.countTriples() > this.currentTrick.countTriples());
									}
									
									if(trick.countPairsPlusTractors() < this.currentTrick.countPairsPlusTractors()) {
										if( following_suit ) {
											int pairs = 0;
											for( Card pair : user.getHand().getPairs() ) {
												if(pair.getGameSuit(this.TRUMP_SUIT, this.TRUMP_NUMBER) == this.currentSuit) {
													System.out.println("Pair in hand: "+pair);
													pairs++;
												}
											}
											if(pairs > trick.countPairsPlusTractors()) {
												sendCommand(GameCommand.PLAY_INVALID+" must play pairs",user);
												break CommandSwitch;
											}
										}
										following_play = false;
									} else if ( trick.countPairs() > this.currentTrick.countPairs() ) {
										//convert extra pairs to singles
										do {
											trick.pairToSingle(trick.getPairs().first());
										} while(trick.countPairs() > this.currentTrick.countPairs());
										Collections.sort(trick.getSingles(),cardComparator);
									}
									
									//check tractors
									if (!skipTractorCheck) {
										TreeSet<Tractor> ctrick = this.currentTrick.getTractors();
										TreeSet<Tractor> ttrick = trick.getTractors();
										Iterator<Tractor> ci = ctrick.iterator();
										Iterator<Tractor> ti = ttrick.iterator();
										//for(int k = 0; k < ctrick.size(); k++) {
										while(ci.hasNext()) {
											Tractor ct = ci.next();
											Tractor tt;
											try {
												tt = ti.next();
											} catch (NoSuchElementException e) {
												tt = null;
											}
											if(tt == null) {
												for(Tractor tractor : user.getHand().getTractors()) {
													if(tractor.getStartingCard().getGameSuit(TRUMP_SUIT, TRUMP_NUMBER) == this.currentSuit && tractor.getType() == ct.getType() && !ttrick.contains(tractor) ) {
														System.out.println("tractor in hand: "+tractor);
														sendCommand(GameCommand.PLAY_INVALID+" must play tractors",user);
														break CommandSwitch;
													}
												}
												following_play = false;
											} else if( tt.getType() < ct.getType() || tt.getCards().size() < ct.getCards().size() ) {
												for(Tractor tractor : user.getHand().getTractors()) {
													if(tractor.getStartingCard().getGameSuit(TRUMP_SUIT, TRUMP_NUMBER) == this.currentSuit && tractor.getLength() >= ct.getLength() && tractor.getType() == ct.getType() && !ttrick.contains(tractor) ) {
														System.out.println("tractor in hand: "+tractor);
														sendCommand(GameCommand.PLAY_INVALID+" must play tractors",user);
														break CommandSwitch;
													}
												}
												following_play = false;
											} else {
												if (tt.getType() > ct.getType()) {
													tt.tripleToPair();
													//TODO: tractors might be out of order if there's a pair tractor with cards larger than the triple tractor
													List<Card> cards = tt.getCards();
													for(Card card : cards) {
														trick.tripleToPair(card);
													}
												}
												if (tt.getLength() > ct.getLength()) {
													if(tt.getType() == 3) {
														do {
															Card card = tt.getCards().remove(0); 
															trick.getTriplesPlusTractors().remove(card);
															trick.addTriple(card);
														} while (tt.getLength() > ct.getLength());
														if ( trick.countTriples() > this.currentTrick.countTriples() ) {
															//convert extra triples to pairs and singles
															do {
																trick.tripleToPair(trick.getTriples().first());
															} while(trick.countTriples() > this.currentTrick.countTriples());
														}
														if ( trick.countPairs() > this.currentTrick.countPairs() ) {
															//convert extra pairs to singles
															do {
																trick.pairToSingle(trick.getPairs().first());
															} while(trick.countPairs() > this.currentTrick.countPairs());
															Collections.sort(trick.getSingles(),cardComparator);
														}
													} else if(tt.getType() == 2) {
														do {
															Card card = tt.getCards().remove(0); 
															trick.getPairsPlusTractors().remove(card);
															trick.addPair(card);
														} while (tt.getLength() > ct.getLength());
														if ( trick.countPairs() > this.currentTrick.countPairs() ) {
															//convert extra pairs to singles
															do {
																trick.pairToSingle(trick.getPairs().first());
															} while(trick.countPairs() > this.currentTrick.countPairs());
															Collections.sort(trick.getSingles(),cardComparator);
														}
													}
												}
											}
										}
									}
									
									
									if(following_play) {
										if(all_trump && !this.trumped) {
											this.trumped = true; 
										} else {
											if(this.trumped && !all_trump)
												break CheckPlay;
											
											//check to see who's high now
											//shouldn't have an issue with comparator returning 9999 since anything that's not the same suit will have been eliminated
											if(this.currentTrick.countTractors() > 0) {
												if(tractorComparator.compare(trick.getTractors().last(),this.currentTrick.getTractors().last()) <= 0 )
													break CheckPlay;
											} else if (this.currentTrick.countTriples() > 0) {
												if( cardComparator.gameCompare(trick.getTriples().last(), this.currentTrick.getTriples().last()) <= 0 ) {
													break CheckPlay;
												}
											} else if ( this.currentTrick.countPairs() > 0 ) {
												if( cardComparator.gameCompare(trick.getPairs().last(), this.currentTrick.getPairs().last()) <= 0 ) {
													break CheckPlay;
												}
											} else {
												if( cardComparator.gameCompare(trick.getSingles().get(trick.countSingles()-1), this.currentTrick.getSingles().get(this.currentTrick.countSingles()-1)) <= 0 ) {
													break CheckPlay;
												}
											}
										}
										
										this.highest = user;
										this.currentTrick = trick;
									}
									break;
								}


							}
							
							//should only be here if the play was valid
							this.addPoints(played);
							user.getHand().removeAllCards(played);
							//System.out.println("removing cards from "+user.getName()+"'s hand: "+played);
							String success = " "+played.size();
							for(Card c : played) {
								success += " " + c.getSuit() + " " + c.getNumber();
							}
							sendCommand(GameCommand.PLAY_SUCCESS+success,user);
							String tosend = GameCommand.PLAY_CARD + " " + user.getName() + " " + played.size();
							for(Card card : played) {
								tosend += " " + card.getSuit() + " " + card.getNumber();
							}
							sendCommandExclude(tosend,user);

							if(!userIterator.hasNext()) { 

								if(highest.getTeam() != this.defending) //if team is not the one with dipai
									this.gamePoints += this.currentPoints;
								this.currentPoints = 0;
								
								if(currentUser.getHand().getCards().size() == 0) {
									System.out.println("GAME ENDING");
									
									//TODO: show dipai
									if(highest.getTeam() != this.defending) {
										this.addPoints(dipai,played.size()*2);
										this.gamePoints += this.currentPoints;
									}
									
									if(gamePoints == 0) {
										this.defending.goUp(3);
										this.setLead(this.defending.next());
									} else if ( gamePoints < decks*20 ) {
										this.defending.goUp(2);
										this.setLead(this.defending.next());
									} else if ( gamePoints < decks*40 ) {
										this.defending.goUp(1);
										this.setLead(this.defending.next());
									} else {
										this.setLead(this.attacking.next());
										if(/*this.lead.getGameScore() != Card.TWO && */gamePoints >= decks*60 ) {
											if ( gamePoints < decks*80 ) {
												this.attacking.goUp(1);
											} else if ( gamePoints < decks*100 ) {
												this.attacking.goUp(2);
											} else {
												this.attacking.goUp(3);
											}
										}
									}
									
									this.TRUMP_NUMBER = this.lead.getGameScore();
									
									if(this.TRUMP_NUMBER > Card.ACE) {
										//TODO: this.lead.getTeam() wins
									}
									
									this.sendCommand(GameCommand.CLEAR_TABLE+" "+gamePoints);
									String dps = "" + this.dipai.size();
									for(Card card : this.dipai) {
										dps += " " + card.getSuit() + " " + card.getNumber();
									}
									this.sendUpdateState(GameCommand.FINISHED, dps);
									this.deal();
									
									break CommandSwitch;
								}
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
								for(Iterator<User> i2 = users.iterator(); i.hasNext();) //what does this do?
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