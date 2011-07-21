package tractor.server;

import java.util.ArrayList;
import java.util.List;

import tractor.lib.Card;

class Tractor {
	private int type;
	private List<Card> cards;
	Tractor(int type, List<Card> cards) {
		//2=doubles 3=trips
		this.type = type;
		this.cards = new ArrayList<Card>(cards);
	}
	public int getType() {
		return this.type;
	}
	public List<Card> getCards() {
		return this.cards;
	}
	public int getLength() {
		return this.cards.size();
	}
	public String toString()
	{
		return (this.type==2 ? "double" : this.type==3 ? "triple" : "unknown") + "tractor: " + cards.toString();
	}
}