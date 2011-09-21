package tractor.server;

import java.util.Comparator;

import tractor.lib.Card;

public class CardComparator implements Comparator<Card> {

	private int TRUMP_SUIT, TRUMP_NUMBER;
	
	public CardComparator(int suit, int number) {
		this.setTrump(suit,number);
	}
	
	public void setTrump(int suit, int number) {
		this.TRUMP_SUIT = suit;
		this.TRUMP_NUMBER = number;
	}
	
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

	@Override
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
}
