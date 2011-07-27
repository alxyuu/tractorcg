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
	public static final int SMALL_JOKER = 0;
	public static final int BIG_JOKER = 1;
	public static final int SET_TRUMP_NUMBER = 14;
	public static final int SET_TRUMP = 13;
	public static final int SPADES = 0;
	public static final int CLUBS = 1;
	public static final int DIAMONDS = 2;
	public static final int HEARTS = 3;
	public static final int TRUMP = 4;
	public static final int CARDS_PER_SUIT = 13;
	public static int TRUMP_SUIT = -1;
	public static int TRUMP_NUMBER = -1;
	//TODO: EL PROBLEMO - MORE THAN ONE TRUMP SUIT/NUMBER WHEN RUNNING SERVER BECAUSE OF MULTIPLE GAMES BAWWW
	//^ fixed by moving trump_suit and trump_number to gameroom, no?
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

	/*public static Card getCard(String s) {
		String[] split = s.split(" ");
		try {
			return cards.get(Integer.parseInt(split[0])*Card.CARDS_PER_SUIT+Integer.parseInt(split[1]));
		} catch (NumberFormatException e) {
			return null;
		}
	}*/
	/** It gets the card.
	 * @param suit
	 * @param value
	 * @return
	 */
	public static Card getCard(String suit, String value) {
		try {
			return cards.get(Integer.parseInt(suit)*Card.CARDS_PER_SUIT+Integer.parseInt(value));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/*public static Card getNextCard(String suit, String value) {
		return Card.getNextCard(Card.getCard(suit,value));
	}
	
	public static Card getNextCard(Card card) {
		if(card.getSuit() != Card.TRUMP_SUIT) {
			if(card.getNumber() == Card.ACE) {
				return null;
			}
			if(card.getSuit() == Card.TRUMP) {
				if(card.getNumber() == Card.BIG_JOKER) { 
					return null;
				} else { //should be small joker for sure...
					return Card.getCard(Card.TRUMP, Card.BIG_JOKER);
				}
			}
			return Card.getCard(card.getSuit(), card.getNumber()+1);
		} else {
			if(card.getNumber() == Card.TRUMP_NUMBER)
				return Card.getCard(Card.TRUMP, Card.SMALL_JOKER);
			else if(card.getNumber() )
			else
				return Card.getCard(card.getSuit(), card.getNumber()+1);
		}
	}*/

	/**It returns the deck
	 * @return
	 * 
	 */
	public static ArrayList<Card> getDeck() {
		return cards;
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

	/** it gets the sorted suit.
	 * @return
	 */
	private int getSortingSuit() {
		if (this.suit == Card.TRUMP)
			return Card.TRUMP+Card.TRUMP+2;
		else if(this.suit == Card.TRUMP_SUIT && this.value == Card.TRUMP_NUMBER)
			return Card.TRUMP+Card.TRUMP+1;
		else if(this.value == Card.TRUMP_NUMBER)
			return Card.TRUMP+this.suit+1;
		else if(this.suit == Card.TRUMP_SUIT)
			return Card.TRUMP;
		else
			return this.suit;
	}
	
	/** It gets the sorting value of the card.
	 * @return
	 */
	private int getSortingValue() {
		//return (this.value == Card.TRUMP_NUMBER) ? ((this.suit == Card.TRUMP_SUIT) ? Card.SET_TRUMP_NUMBER : Card.SET_TRUMP) : this.value;
		//set trump sorting was taken care of in getSortingSuit, I think
		return this.value;
	}

	public int compareTo(Card card) {
		if(this.getSortingSuit() == card.getSortingSuit()) {
			return this.getSortingValue()-card.getSortingValue();
		} else {
			return this.getSortingSuit() - card.getSortingSuit();
		}
		//return this.getrandombs() - card.getrandombs();
	}

	public static String getNameOfNumber(int num) {
		switch(num) {
		case Card.TWO:
			return "Two";
		case Card.THREE:
			return "Three";
		case Card.FOUR:
			return "Four";
		case Card.FIVE:
			return "Five";
		case Card.SIX:
			return "Six";
		case Card.SEVEN:
			return "Seven";
		case Card.EIGHT:
			return "Eight";
		case Card.NINE:
			return "Nine";
		case Card.TEN:
			return "Ten";
		case Card.JACK:
			return "Jack";
		case Card.QUEEN:
			return "Queen";
		case Card.KING:
			return "King";
		case Card.ACE:
			return "Ace";
		}
		return "";
	}
	
	public static String getNameOfSuit(int num) {
		switch(num) {
		case Card.SPADES:
			return "Spades";
		case Card.CLUBS:
			return "Clubs";
		case Card.DIAMONDS:
			return "Diamonds";
		case Card.HEARTS:
			return "Hearts";
		case Card.TRUMP:
			return "No Trump";
		}
		return "";
	}
	
	public String toString() {
		String out = "";
		switch(this.value) {
		case Card.TWO:
			if(this.suit == Card.TRUMP)
				out += "small joker";
			else
				out += "two";
			break;
		case Card.THREE:
			if(this.suit == Card.TRUMP)
				out += "big joker";
			else
				out += "three";
			break;
		case Card.FOUR:
			out += "four";
			break;
		case Card.FIVE:
			out += "five";
			break;
		case Card.SIX:
			out += "six";
			break;
		case Card.SEVEN:
			out += "seven";
			break;
		case Card.EIGHT:
			out+= "eight";
			break;
		case Card.NINE:
			out+= "nine";
			break;
		case Card.TEN:
			out += "ten";
			break;
		case Card.JACK:
			out += "jack";
			break;
		case Card.QUEEN:
			out += "queen";
			break;
		case Card.KING:
			out += "king";
			break;
		case Card.ACE:
			out += "ace";
			break;
		}
		out += " of ";
		switch(this.suit) {
		case Card.SPADES:
			out+="spades";
			break;
		case Card.CLUBS:
			out+="clubs";
			break;
		case Card.DIAMONDS:
			out+="diamonds";
			break;
		case Card.HEARTS:
			out+="hearts";
			break;
		case Card.TRUMP:
			out+="trump";
			break;
		}
		return out;

	}

	/**It gets the suit of the card
	 * @return
	 * 
	 */
	public int getSuit() {
		return this.suit;
	}
	
	public int getGameSuit(int trumpsuit, int trumpnumber) {
		return ( this.getSuit() == trumpsuit || this.getNumber() == trumpnumber ) ? Card.TRUMP : this.getSuit();
	}

}
