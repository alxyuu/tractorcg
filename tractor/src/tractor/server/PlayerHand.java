package tractor.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import tractor.lib.Card;

public class PlayerHand {

	private List<Card> cards;
	private List<Card> currentPlay;
	//private String name;

	public PlayerHand() {
		this.cards = Collections.synchronizedList(new ArrayList<Card>());
		this.currentPlay = Collections.emptyList();
	}
	
	public PlayerHand(PlayerHand hand) {
		this.cards = Collections.synchronizedList(new ArrayList<Card>(hand.getCards()));
		this.currentPlay = Collections.emptyList();
	}

	/**It gives the frequency that the card appears in the players hand.
	 * @param card
	 * @return
	 */
	public int frequency(Card card) {
		return Collections.frequency(this.cards, card);
	}

	/** It checks whether the player's hand contains a particular card.
	 * @param card
	 * @return
	 */
	public boolean contains(Card card) {
		return this.cards.contains(card);
	}
	
	public boolean contains(Collection<Card> card) {
		return this.cards.containsAll(card);
	}
	
	public Iterator<Card> iterator() {
		return this.cards.iterator();
	}
	
	public void sort(Comparator<Card> comparator) {
		Collections.sort(this.cards,comparator);
	}
	/** It returns the Player's cards.
	 * @return
	 */
	public List<Card> getCards() {
		return this.cards;
	}
	
	/** It adds a card to the hand.
	 * @param card
	 */
	public void addCard(Card card) {
		this.cards.add(card);
	}
	
	/** It removes the card from the players hand.
	 * @param card
	 */
	public void removeCard(Card card) {
		this.cards.remove(card);
	}
	
	public void removeAllCards(Collection<Card> cards) {
		this.cards.removeAll(cards);
	}

	/** It sets the current play.
	 * @param play
	 */
	public void setCurrentPlay(List<Card> play) {
		try {
			this.currentPlay = Collections.synchronizedList(play);
		} catch (NullPointerException e) {
			this.currentPlay = Collections.emptyList();
		}
	}

	/** It gets the current play in the players hand.
	 * @return
	 */
	public List<Card> getCurrentPlay() {
		return this.currentPlay;
	}
}