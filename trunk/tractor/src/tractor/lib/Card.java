package tractor.lib;

public class Card {
	public static final int ACE = 13;
	public static final int TWO = 1;
	public static final int THREE = 2;
	public static final int FOUR = 3;
	public static final int FIVE = 4;
	public static final int SIX = 5;
	public static final int SEVEN = 6;
	public static final int EIGHT = 7;
	public static final int NINE = 8;
	public static final int TEN = 9;
	public static final int JACK = 10;
	public static final int QUEEN = 11;
	public static final int KING = 12;
	public static final int SMALL_JOKER = 9998;
	public static final int BIG_JOKE = 9999;
	public static final int SPADES = 2;
	public static final int CLUBS = 3;
	public static final int DIAMONDS = 4;
	public static final int HEARTS = 5;
	public static final int TRUMP = 1;
	public static int TRUMP_SUIT = 0;
	public static final int SET_TRUMP_BONUS = 13;
	
	private int value;
	private int suit;
	
	Card(int value, int suit) {
		this.value = value;
		this.suit = suit;
	}
	
	public int getSortingValue() {
		//return ( (this.suit == Card.TRUMP_SUIT) ? Card.TRUMP : this.suit ) *
		//wait for aaron
		return 0;
	}

}
