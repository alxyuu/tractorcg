package tractor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tractor.lib.Card;

public class PlayerHand {

	private List<Card> cards;
	//private String name;
	
	PlayerHand() {
		this.cards = Collections.synchronizedList(new ArrayList<Card>());
	}
	
	public int frequency(Card card) {
		return Collections.frequency(this.cards, card);
	}
	
	public List<Card> getCards() {
		return this.cards;
	}
	public void addCard(Card card) {
		this.cards.add(card);
		//Collections.sort(this.cards);
	}
	
	public void removeCard(Card card) {
		this.cards.remove(card);
	}	
}
