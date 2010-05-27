package tractor.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	/** it returns the singles in the players hand.
	 * @param a
	 * @param s
	 * @return
	 */
	public Card Singles(int a,int s)
	{

		return this.cards.get(0);
	}
	/** It returns the pairs in the players hand.
	 * @param a
	 * @param s
	 * @return
	 */
	public List<Card> Pairs(int a,int s)
	{

		return this.cards;
	}
	/** It returns the tractors in the players hand.
	 * @param a
	 * @param l
	 * @param s
	 * @return
	 */
	public List<Card> Tractors(int a,int l,int s)
	{

		return this.cards;
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