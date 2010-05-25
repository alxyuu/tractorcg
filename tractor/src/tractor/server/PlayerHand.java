package tractor.server;

import java.util.ArrayList;
import java.util.Collections;

import tractor.lib.Card;

public class PlayerHand {

	private ArrayList<Card> cards;
	//private String name;
	
	PlayerHand() {
		this.cards = new ArrayList<Card>();
	}
	
	public int frequency(Card card) {
		return Collections.frequency(this.cards, card);
	}
	
	public void addCard(Card card) {
		this.cards.add(card);
		//Collections.sort(this.cards);
	}
	
	public void removeCard(Card card) {
		this.cards.remove(card);
	}	
}
