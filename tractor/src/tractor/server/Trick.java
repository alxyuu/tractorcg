package tractor.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tractor.lib.Card;
import tractor.server.Tractor;

public class Trick {
	private List<Card> singles;
	private List<Card> pairs;
	private List<Card> triples;
	private List<Card> pc, tc;
	private List<Tractor> tractors;
	Trick() {
		this.singles = new LinkedList<Card>();
		this.pairs = new LinkedList<Card>();
		this.triples = new LinkedList<Card>();
		this.pc = new LinkedList<Card>();
		this.tc = new LinkedList<Card>();
		this.tractors = new LinkedList<Tractor>();
	}

	public void addSingle(Card card) {
		this.singles.add(card);
	}

	public void addPair(Card pair) {
		this.addPair(pair,true);
	}
	public void addPair(Card pair, boolean addToTotal) {
		this.pairs.add(pair);
		if(addToTotal)
			this.pc.add(pair);
	}

	public void addTriple(Card triple) {
		this.addTriple(triple, true);
	}
	public void addTriple(Card triple, boolean addToTotal) {
		this.triples.add(triple);
		if(addToTotal)
			this.tc.add(triple);
	}

	public void addTractor(Tractor tractor) {
		this.tractors.add(tractor);
		switch (tractor.getType()) {
		case 2:
			for(Card card : tractor.getCards()) {
				this.pc.add(card);
			}
			break;
		case 3:
			for(Card card : tractor.getCards()) {
				this.tc.add(card);
			}
			break;
		default:
			System.out.println("broken broken broken");
		}
	}

	public List<Card> getSingles() {
		return this.singles;
	}
	
	public List<Card> getPairs() {
		return pairs;
	}
	
	// cannot be modified, returned possibly out of order
	public List<Card> getPairsPlusTractors() {
		return pc;
	}

	public List<Card> getTriples() {
		return triples;
	}
	
	// cannot be modified, returned possibly out of order
	public List<Card> getTriplesPlusTractors() {
		return tc;
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
	
	public int countPairsPlusTractors() {
		return this.pc.size();
	}
	
	public int countTriples() {
		return this.triples.size();
	}
	
	public int countTriplesPlusTractors() {
		return this.tc.size();
	}
	
	public void tripleToPair(Card card) {
		this.triples.remove(card);
		this.tc.remove(card);
		this.addPair(card);
		this.addSingle(card);
	}
	
	public void pairToSingle(Card card) {
		this.pairs.remove(card);
		this.pc.remove(card);
		this.addSingle(card);
		this.addSingle(card);
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
			cards += tractor.getType()*tractor.getLength();
		}
		return cards;
	}

	public String toString() {
		return "Singles: "+this.singles + "\nPairs: " + this.pairs + "\nTriples: " + this.triples + "\nTractors: " + this.tractors;
	}

}

