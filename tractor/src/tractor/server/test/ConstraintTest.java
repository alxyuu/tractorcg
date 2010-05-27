package tractor.server.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import tractor.lib.Card;
import tractor.server.PlayerHand;
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
					return Card.TRUMP+Card.TRUMP+1;
				else if(card.getSuit() == TRUMP_SUIT && card.getNumber() == TRUMP_NUMBER)
					return Card.TRUMP+Card.TRUMP;
				else if(card.getNumber() == TRUMP_NUMBER)
					return Card.TRUMP+card.getSuit();
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
		this.TRUMP_SUIT = Card.DIAMONDS;
		this.TRUMP_NUMBER = Card.TWO;

		User user = users.get(1);

		ArrayList<Card> played = new ArrayList<Card>();
		played.add(Card.getCard(Card.SPADES, Card.KING));
		played.add(Card.getCard(Card.SPADES, Card.KING));
		played.add(Card.getCard(Card.SPADES, Card.ACE));
		//add cards





		Collections.sort(played,cardComparator);

		if(user == this.lead) {
			//the player is the first player, check to make sure the play is high

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
					System.out.println("must play same suit");
					return;
				}
				//suit = ( card.getNumber() == this.TRUMP_NUMBER ) ? Card.TRUMP : card.getSuit();
			}

			Trick trick = calculateTrick(played);
			System.out.println(trick);

			//check if high only if there's more than one play
			if(trick.countPlays() >= 1) {

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
										System.out.println("not high (normal single found)");
										return;
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
									System.out.println("not high (trump single found)");
									return;
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
										System.out.println("not high (normal pair found)");
										return;
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
											System.out.println("not high (trump pair found)");
											return;
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
										System.out.println("not high (normal triple found)");
										return;
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
											System.out.println("not high (trump triple found)");
											return;
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
			//not lead, check following suit, playing doubles/tractors/triples/whatever
			//compare to highest user's play
			//make sure the number of cards are correct




		}

		System.out.println("congratulations all tests passed");
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
}

class Trick {
	private List<Card> singles;
	private List<Card> pairs;
	private List<Card> triples;
	private List<Tractor> tractors;
	Trick() {
		this.singles = new LinkedList<Card>();
		this.pairs = new LinkedList<Card>();
		this.triples = new LinkedList<Card>();
		this.tractors = new LinkedList<Tractor>();
	}

	public void addSingle(Card card) {
		this.singles.add(card);
	}

	public void addPair(Card pair) {
		this.pairs.add(pair);
	}

	public void addTriple(Card triple) {
		this.triples.add(triple);
	}

	public void addTractor(Tractor tractor) {
		this.tractors.add(tractor);
	}

	public List<Card> getSingles() {
		return this.singles;
	}
	public List<Card> getPairs() {
		return pairs;
	}

	public List<Card> getTriples() {
		return triples;
	}

	public List<Tractor> getTractors() {
		return tractors;
	}

	public int countSingles() {
		return this.singles.size();
	}

	public int countPairs() {
		return this.pairs.size();
	}

	public int countTriples() {
		return this.triples.size();
	}

	public int countTractors() {
		return this.tractors.size();
	}

	public int countPlays() {
		return this.countSingles() + this.countPairs() + this.countTriples() + this.countTractors();
	}

	public String toString() {
		return "Singles: "+this.singles + "\nPairs: " + this.pairs + "\nTriples: " + this.triples + "\nTractors: " + this.tractors;
	}

}

class Tractor {
	int pairs;
	int triples;
	int start;
	int end;
	private int length;
	private int suit;
	Tractor(int pairs, int triples, int start, int suit, int length) {
		this.pairs = pairs;
		this.triples = triples;
		this.start = start;
		this.suit = suit;
		this.length = length;
	}
	public String toString()
	{
		return "tractor starting at "+Card.getCard(suit,start)+" of length "+length+" :: "+pairs+" pairs and "+triples+" triples";
	}
}
