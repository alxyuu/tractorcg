package tractor.server;

import tractor.lib.Card;

class Tractor {
	private int type;
	private int start;
	private int length;
	private int suit;
	Tractor(int type, int start, int suit, int length) {
		//2=doubles 3=trips
		this.type = type;
		this.start = start;
		this.suit = suit;
		this.length = length;
	}
	public int getType() {
		return this.type;
	}
	public int getLength() {
		return this.length;
	}
	public String toString()
	{
		return (this.type==2 ? "double" : this.type==3 ? "triple" : "unknown") + "tractor starting at "+Card.getCard(suit,start)+" of length "+length;
	}
}