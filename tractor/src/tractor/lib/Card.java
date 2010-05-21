package tractor.lib;

import java.util.ArrayList;

public class Card implements Comparable<Card> {
	
	protected static final ArrayList<Card> cards;
	public static final int ACE = 12;
	public static final int TWO = 0;
	public static final int THREE = 1;
	public static final int FOUR = 2;
	public static final int FIVE = 3;
	public static final int SIX = 4;
	public static final int SEVEN = 5;
	public static final int EIGHT = 6;
	public static final int NINE = 7;
	public static final int TEN = 8;
	public static final int JACK = 9;
	public static final int QUEEN = 10;
	public static final int KING = 11;
	public static final int SMALL_JOKER = 9998;
	public static final int BIG_JOKER = 9999;
	public static final int SET_TRUMP_NUMBER = 9997;
	public static final int SET_TRUMP = 9996;
	public static final int SPADES = 0;
	public static final int CLUBS = 1;
	public static final int DIAMONDS = 2;
	public static final int HEARTS = 3;
	public static final int TRUMP = 4;
	public static final int CARDS_PER_SUIT = 13;
	public static int TRUMP_SUIT = 0;
	public static int TRUMP_NUMBER = 0;
	//public static final int SET_TRUMP_BONUS = 13;
	
	static {
		
		cards = new ArrayList<Card>();
	}
	
	/**It gets the card
	 * @param suit
	 * @param value
	 * @return
	 * 
	 */
	public static Card getCard(int suit, int value) {
		return cards.get(suit*Card.CARDS_PER_SUIT+value);
	}
	
	public static Card getCard(String s) {
		String[] split = s.split(" ");
		try {
			return cards.get(Integer.parseInt(split[0])*Card.CARDS_PER_SUIT+Integer.parseInt(split[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**It returns the deck
	 * @return
	 * 
	 */
	public static ArrayList<Card> getDeck() {
		return cards;
	}
	public static void main(String[] args) {
		System.out.println(Card.getCard(Card.HEARTS,Card.ACE));
	}
	
	/**It populates the deck with cards
	 * 
	 */
	public static void populateDeck() {
		for(int i=0; i<4; i++) {
			for(int k=0; k<13; k++) {
				cards.add(new Card(i,k));
			}
		}
		/*cards.add(new Card(Card.SPADES, Card.TWO));
		cards.add(new Card(Card.SPADES, Card.THREE));
		cards.add(new Card(Card.SPADES, Card.FOUR));
		cards.add(new Card(Card.SPADES, Card.FIVE));
		cards.add(new Card(Card.SPADES, Card.SIX));
		cards.add(new Card(Card.SPADES, Card.SEVEN));
		cards.add(new Card(Card.SPADES, Card.EIGHT));
		cards.add(new Card(Card.SPADES, Card.NINE));
		cards.add(new Card(Card.SPADES, Card.TEN));
		cards.add(new Card(Card.SPADES, Card.JACK));
		cards.add(new Card(Card.SPADES, Card.QUEEN));
		cards.add(new Card(Card.SPADES, Card.KING));
		cards.add(new Card(Card.SPADES, Card.ACE));
		cards.add(new Card(Card.CLUBS, Card.TWO));
		cards.add(new Card(Card.CLUBS, Card.THREE));
		cards.add(new Card(Card.CLUBS, Card.FOUR));
		cards.add(new Card(Card.CLUBS, Card.FIVE));
		cards.add(new Card(Card.CLUBS, Card.SIX));
		cards.add(new Card(Card.CLUBS, Card.SEVEN));
		cards.add(new Card(Card.CLUBS, Card.EIGHT));
		cards.add(new Card(Card.CLUBS, Card.NINE));
		cards.add(new Card(Card.CLUBS, Card.TEN));
		cards.add(new Card(Card.CLUBS, Card.JACK));
		cards.add(new Card(Card.CLUBS, Card.QUEEN));
		cards.add(new Card(Card.CLUBS, Card.KING));
		cards.add(new Card(Card.CLUBS, Card.ACE));
		cards.add(new Card(Card.DIAMONDS, Card.TWO));
		cards.add(new Card(Card.DIAMONDS, Card.THREE));
		cards.add(new Card(Card.DIAMONDS, Card.FOUR));
		cards.add(new Card(Card.DIAMONDS, Card.FIVE));
		cards.add(new Card(Card.DIAMONDS, Card.SIX));
		cards.add(new Card(Card.DIAMONDS, Card.SEVEN));
		cards.add(new Card(Card.DIAMONDS, Card.EIGHT));
		cards.add(new Card(Card.DIAMONDS, Card.NINE));
		cards.add(new Card(Card.DIAMONDS, Card.TEN));
		cards.add(new Card(Card.DIAMONDS, Card.JACK));
		cards.add(new Card(Card.DIAMONDS, Card.QUEEN));
		cards.add(new Card(Card.DIAMONDS, Card.KING));
		cards.add(new Card(Card.DIAMONDS, Card.ACE));
		cards.add(new Card(Card.HEARTS, Card.TWO));
		cards.add(new Card(Card.HEARTS, Card.THREE));
		cards.add(new Card(Card.HEARTS, Card.FOUR));
		cards.add(new Card(Card.HEARTS, Card.FIVE));
		cards.add(new Card(Card.HEARTS, Card.SIX));
		cards.add(new Card(Card.HEARTS, Card.SEVEN));
		cards.add(new Card(Card.HEARTS, Card.EIGHT));
		cards.add(new Card(Card.HEARTS, Card.NINE));
		cards.add(new Card(Card.HEARTS, Card.TEN));
		cards.add(new Card(Card.HEARTS, Card.JACK));
		cards.add(new Card(Card.HEARTS, Card.QUEEN));
		cards.add(new Card(Card.HEARTS, Card.KING));
		cards.add(new Card(Card.HEARTS, Card.ACE));*/
		cards.add(new Card(Card.TRUMP, Card.SMALL_JOKER));
		cards.add(new Card(Card.TRUMP, Card.BIG_JOKER));
	}
	
	private int suit;
	private int value;
	
	/**Creates the Card object
	 * @param suit
	 * @param value
	 * 
	 */
	protected Card(int suit, int value) {
		this.value = value;
		this.suit = suit;
	}
	
	/**Returns the number on the card
	 * @return
	 * 
	 */
	public int getNumber() {
		return this.value;
	}
	
	private int getSortingSuit() {
		return (this.suit == Card.TRUMP_SUIT) ? Card.TRUMP : this.suit;
	}
	private int getSortingValue() {
		return (this.value == Card.TRUMP_NUMBER) ? (this.suit == Card.TRUMP_SUIT ? Card.SET_TRUMP_NUMBER : Card.SET_TRUMP) : this.value;
	}
	
	public int compareTo(Card card) {
		if(this.suit == card.suit) {
			return this.value-card.value;
		} else {
			return this.getSortingSuit() - card.getSortingSuit();
		}
	}
	
	public String toString() {
		return this.suit + " " + this.value;
	}
	
	/**It gets the suit of the card
	 * @return
	 * 
	 */
	public int getSuit() {
		return this.suit;
	}

}
