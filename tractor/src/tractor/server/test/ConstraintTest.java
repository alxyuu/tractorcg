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
	private CardComparator cardComparator;
	private Comparator<Tractor> tractorComparator;

	public static void main(String[] args) {
		Card.populateDeck();
		new ConstraintTest().test();
	}

	ConstraintTest() {
		this.cardComparator = new CardComparator(0,0);
	
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
		this.cardComparator.setTrump(this.TRUMP_SUIT, this.TRUMP_NUMBER);

		User user = users.get(1);

		ArrayList<Card> played = new ArrayList<Card>();
		played.add(Card.getCard(Card.SPADES,Card.THREE));
		played.add(Card.getCard(Card.SPADES,Card.SIX));
		played.add(Card.getCard(Card.SPADES,Card.SIX));
		played.add(Card.getCard(Card.SPADES,Card.SEVEN));
		played.add(Card.getCard(Card.SPADES,Card.SEVEN));
		played.add(Card.getCard(Card.SPADES,Card.SEVEN));
		played.add(Card.getCard(Card.SPADES,Card.EIGHT));
		played.add(Card.getCard(Card.SPADES,Card.EIGHT));
		played.add(Card.getCard(Card.SPADES,Card.EIGHT));
		played.add(Card.getCard(Card.SPADES,Card.NINE));
		played.add(Card.getCard(Card.SPADES,Card.TEN));
		played.add(Card.getCard(Card.SPADES,Card.QUEEN));
		played.add(Card.getCard(Card.SPADES,Card.KING));
		played.add(Card.getCard(Card.CLUBS,Card.FOUR));
		played.add(Card.getCard(Card.CLUBS,Card.SIX));
		played.add(Card.getCard(Card.CLUBS,Card.SEVEN));
		played.add(Card.getCard(Card.CLUBS,Card.JACK));
		played.add(Card.getCard(Card.CLUBS,Card.ACE));
		played.add(Card.getCard(Card.DIAMONDS,Card.THREE));
		played.add(Card.getCard(Card.DIAMONDS,Card.FOUR));
		played.add(Card.getCard(Card.DIAMONDS,Card.EIGHT));
		played.add(Card.getCard(Card.DIAMONDS,Card.TEN));
		played.add(Card.getCard(Card.DIAMONDS,Card.JACK));
		played.add(Card.getCard(Card.DIAMONDS,Card.QUEEN));
		played.add(Card.getCard(Card.DIAMONDS,Card.KING));
		played.add(Card.getCard(Card.DIAMONDS,Card.KING));
		played.add(Card.getCard(Card.HEARTS,Card.SIX));
		played.add(Card.getCard(Card.HEARTS,Card.SEVEN));
		played.add(Card.getCard(Card.HEARTS,Card.SEVEN));
		played.add(Card.getCard(Card.HEARTS,Card.NINE));
		played.add(Card.getCard(Card.HEARTS,Card.NINE));
		played.add(Card.getCard(Card.HEARTS,Card.ACE));
		played.add(Card.getCard(Card.SPADES,Card.TWO));
		played.add(Card.getCard(Card.SPADES,Card.TWO));
		played.add(Card.getCard(Card.CLUBS,Card.TWO));
		played.add(Card.getCard(Card.CLUBS,Card.TWO));
		played.add(Card.getCard(Card.DIAMONDS,Card.TWO));
		played.add(Card.getCard(Card.TRUMP,Card.SMALL_JOKER));
		played.add(Card.getCard(Card.TRUMP,Card.SMALL_JOKER));


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