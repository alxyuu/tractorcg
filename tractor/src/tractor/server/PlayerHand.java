package tractor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tractor.lib.Card;

public class PlayerHand {

	private List<Card> cards;
	private List<Card> currentPlay;
	//private String name;

	PlayerHand() {
		this.cards = Collections.synchronizedList(new ArrayList<Card>());
		this.currentPlay = Collections.emptyList();
	}

	public int frequency(Card card) {
		return Collections.frequency(this.cards, card);
	}

	public boolean contains(Card card) {
		return this.cards.contains(card);
	}
	public List<Card> getCards() {
		return this.cards;
	}
	public void addCard(Card card) {
		this.cards.add(card);
		//Collections.sort(this.cards);
	}
	public Card Singles(int a,int s)
	{

		return this.cards.get(0);
	}
	public List<Card> Pairs(int a,int s)
	{

		return this.cards;
	}
	public List<Card> Tractors(int a,int l,int s)
	{

		return this.cards;
	}
	public void removeCard(Card card) {
		this.cards.remove(card);
	}

	public void setCurrentPlay(List<Card> play) {
		try {
			this.currentPlay = Collections.synchronizedList(play);
		} catch (NullPointerException e) {
			this.currentPlay = Collections.emptyList();
		}
	}

	public List<Card> getCurrentPlay() {
		return this.currentPlay;
	}
}