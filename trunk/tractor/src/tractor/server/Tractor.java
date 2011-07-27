package tractor.server;

import java.util.ArrayList;
import java.util.List;

import tractor.lib.Card;

public class Tractor {
	private int type;
	private List<Card> cards;
	public Tractor(int type, List<Card> cards) {
		//2=doubles 3=trips
		this.type = type;
		this.cards = new ArrayList<Card>(cards);
	}
	public int getType() {
		return this.type;
	}
	public void tripleToPair() {
		if(this.type == 3)
			this.type = 2;
	}
	public List<Card> getCards() {
		return this.cards;
	}
	public int getLength() {
		return this.cards.size();
	}
	public Card getStartingCard() {
		return this.cards.get(0);
	}
	public String toString()
	{
		return (this.type==2 ? "double" : this.type==3 ? "triple" : "unknown") + "tractor: " + cards.toString();
	}
	
	public boolean equals(Tractor t) {
		if(t.type == this.type && this.cards.containsAll(t.cards) && t.cards.containsAll(this.cards))
			return true;
		return false;
	}
}