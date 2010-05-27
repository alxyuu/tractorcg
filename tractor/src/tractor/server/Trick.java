package tractor.server;

import java.util.LinkedList;
import java.util.List;

import tractor.lib.Card;

public class Trick {
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
	
	public int countCards() {
		int cards = this.countSingles() + this.countPairs()*2 + this.countTriples()*3;
		for(Tractor tractor : this.tractors) {
			cards += tractor.countPairs()*2 + tractor.countTriples()*3;
		}
		return cards;
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
	public int countPairs() {
		return this.pairs;
	}
	public int countTriples() {
		return this.triples;
	}
	public String toString()
	{
		return "tractor starting at "+Card.getCard(suit,start)+" of length "+length+" :: "+pairs+" pairs and "+triples+" triples";
	}
}
