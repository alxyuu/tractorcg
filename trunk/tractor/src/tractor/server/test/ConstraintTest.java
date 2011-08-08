package tractor.server.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import tractor.lib.Card;
import tractor.server.CardComparator;
import tractor.server.PlayerHand;
import tractor.server.Tractor;
import tractor.server.Trick;
import tractor.server.test.User;

public class ConstraintTest {
	private User currentUser;
	private List<User> users;
	private User lead;
	private User highest;
	private int currentSuit;
	private int TRUMP_SUIT, TRUMP_NUMBER;
	private Trick currenTrick;
	private Trick currentTrick;
	private CardComparator<Card> cardComparator;
	private Comparator<Tractor> tractorComparator;

	public static void main(String[] args) {
		Card.populateDeck();
		new ConstraintTest().test();
	}

	ConstraintTest() {
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
		
		this.users = new ArrayList<User>();
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.add(new User());
		for(User user : users) {
			user.newHand();
		}
		lead = users.get(0);
		highest = users.get(0);

		/*
		 * MAKE THE PLAYER HANDS HERE
		 */
		PlayerHand hand1 = users.get(0).getHand();
		hand1.addCard(Card.getCard(Card.SPADES, Card.ACE));
		hand1.addCard(Card.getCard(Card.SPADES, Card.ACE));
		hand1.addCard(Card.getCard(Card.SPADES, Card.KING));
		hand1.addCard(Card.getCard(Card.SPADES, Card.KING));
		hand1.sort(cardComparator);
		
		PlayerHand hand2 = users.get(1).getHand();
		//add cards
		hand2.addCard(Card.getCard(Card.TRUMP, Card.SMALL_JOKER));
		hand2.addCard(Card.getCard(Card.TRUMP, Card.SMALL_JOKER));
		hand2.addCard(Card.getCard(Card.SPADES, Card.JACK));
		hand2.addCard(Card.getCard(Card.SPADES, Card.JACK));
		hand2.sort(cardComparator);

		PlayerHand hand3 = users.get(2).getHand();
		//add cards
		hand3.addCard(Card.getCard(Card.SPADES, Card.SIX));
		hand3.addCard(Card.getCard(Card.SPADES, Card.SIX));
		hand3.addCard(Card.getCard(Card.SPADES, Card.TEN));
		hand3.addCard(Card.getCard(Card.SPADES, Card.TEN));
		hand3.sort(cardComparator);

		PlayerHand hand4 = users.get(3).getHand();
		//add cards
		hand4.addCard(Card.getCard(Card.DIAMONDS, Card.THREE));
		hand4.addCard(Card.getCard(Card.SPADES, Card.FOUR));
		hand4.addCard(Card.getCard(Card.SPADES, Card.FIVE));
		hand4.addCard(Card.getCard(Card.SPADES, Card.FIVE));
		hand4.sort(cardComparator);
	}

	public void test() {

		/*
		 * SET TRUMP AND ADD CARDS HERE
		 */
		this.TRUMP_SUIT = Card.HEARTS;
		this.TRUMP_NUMBER = Card.TWO;

		User user = users.get(1);

		ArrayList<Card> played = new ArrayList<Card>();
		played.add(Card.getCard(Card.HEARTS,Card.SEVEN));
		played.add(Card.getCard(Card.HEARTS,Card.SEVEN));
		played.add(Card.getCard(Card.HEARTS,Card.EIGHT));
		played.add(Card.getCard(Card.HEARTS,Card.EIGHT));

		System.out.println(cardComparator.gameCompare(Card.getCard(Card.SPADES,Card.TWO), Card.getCard(Card.HEARTS,Card.QUEEN)));
		
		//add cards





		//Collections.sort(played,cardComparator);

		System.out.println(calculateTrick(played));
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
}