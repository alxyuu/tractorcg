package tractor.client.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

import tractor.client.game.GraphicsCard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;

public class PlayerHand {

	private float x, y, x2, y2;
	private static final int spacing = 20;
	private Map<CardButton, GraphicsCard> hand;
	//private Collection<GraphicsCard> cards;
	private List<GraphicsCard> playedcards;
	private List<GraphicsCard> lasthand;
	private boolean show_last;
	//private String name;

	/** It constructs the player hand.
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 */
	PlayerHand(float x, float y, float x2, float y2) {
		//this.name = Client.getInstance().getUsername();
		this.x = x - ( 100 - spacing )/2;
		this.y = y - 74;
		this.x2 = x2 - ( 100 - spacing )/2;
		this.y2 = y2 - 74;
		this.hand = new ConcurrentSkipListMap<CardButton, GraphicsCard>();
		//this.cards = hand.values();
		this.playedcards = Collections.emptyList();
		this.lasthand = Collections.emptyList();
		this.show_last = false;
	}

	/** It gets the cards.
	 * @return
	 */
	public Collection<GraphicsCard> getCards() {
		return this.hand.values();
	}
	
	/** it returns the frequency of a card.
	 * @param card
	 * @return
	 */
	public int frequency(GraphicsCard card) {
		synchronized(this) {
			return Collections.frequency(this.hand.values(), card);
		}
	}
	/** It adds a card to the players hand.
	 * @param container
	 * @param card
	 */
	public void addCard(GUIContext container, GraphicsCard card) {
		synchronized(this) {
			this.hand.put(new CardButton(container,card,0,(int)y,spacing,148),card);
			updateLocations();
		}
	}

	/** It removes a card from the players hand.
	 * @param card
	 */
	public void removeCard(CardButton card) {
		synchronized(this) {
			card.setAcceptingInput(false);
			if(this.hand.get(card)==null) {
				System.out.println("UH OH HOTDOG "+this.hand.containsKey(card));
			}
			System.out.println("Removing with CardButton reference: "+this.hand.remove(card));
			//System.out.println(this.hand.toString().replace(", ",",\n"));
			//dispose of cardbutton?
			updateLocations();
		}
	}

	/** It plays the card.
	 * @param played
	 */
	public void playCards(GraphicsCard ... played) {
		this.playedcards = Collections.synchronizedList(Arrays.asList(played));
	}

	/** It plays the card.
	 * @param played
	 */
	public void playCards(List<GraphicsCard> played) {
		this.playedcards = Collections.synchronizedList(played);
	}

	/** It clears the table.
	 * 
	 */
	public void clearTable() {
		this.lasthand = this.playedcards;
		this.playedcards = Collections.emptyList();
	}
	
	/** It sorts the players hand based on the trump suit.
	 * @param trumpsuit
	 */
	public void sort(int trumpsuit, int trumpnumber) {
		synchronized(this) {
			//REMOVE EVERYTHING AND READD TO SORT LOL
			HashMap<CardButton,GraphicsCard> temp = new HashMap<CardButton,GraphicsCard>();
			Iterator<CardButton> fail = this.hand.keySet().iterator();
			while( fail.hasNext() ) {
				CardButton cb = fail.next();
				temp.put(cb,this.hand.get(cb));
				fail.remove();
			}
			GraphicsCard.TRUMP_SUIT = trumpsuit;
			GraphicsCard.TRUMP_NUMBER = trumpnumber;
			fail = temp.keySet().iterator();
			while( fail.hasNext() ) {
				CardButton cb = fail.next();
				this.hand.put(cb,temp.get(cb));
				fail.remove();
			}
			updateLocations();
		}
	}

	/** It updates the location of the players hand.
	 * 
	 */
	/*private void updateLocations() {
		//never called from unsynchronzied block so no need to sync
		float start = this.x - (spacing * this.cards.size())/2;
		CardButton card = null;
		Iterator<CardButton> i = hand.keySet().iterator();
		if(i.hasNext()) {
			while( true ){
				card = i.next();
				card.setX(start);
				if(i.hasNext()) {
					card.setSize(spacing,148);
					start+=spacing;
				} else {
					card.setSize(100,148);
					break;
				}
			}
		}
	}*/
	private void updateLocations() {
		//never called from unsynchronzied block so no need to sync
		float start = this.x - (spacing * this.hand.size())/2;
		CardButton card = null;
		CardButton previous = null;
		Iterator<CardButton> i = hand.keySet().iterator();
		while( i.hasNext() ){
			card = i.next();
			card.setX(start);
			if(card.isAcceptingInput()) {
				if(i.hasNext()) {
					card.setSize(spacing,148);
					start+=spacing;
				} else {
					card.setSize(100,148);
					break;
				}
				previous = card;
			} else {
				if(!i.hasNext() && previous != null ) {
					previous.setSize(100,148);
					break;
				}
			}
		}
	}
	
	public void setShowLastTrick(boolean s) {
		this.show_last = s;
	}
	/** It draws the hand.
	 * @param container
	 * @param g
	 * @throws SlickException
	 */
	public void render(GUIContext container, Graphics g) throws SlickException {
		synchronized(this) {
			for(Iterator<CardButton> i = hand.keySet().iterator(); i.hasNext(); ) {
				CardButton cb = i.next();
				if(cb.isAcceptingInput())
					cb.render(container, g);
			}
		}
		
		if(this.show_last) {
			float start = this.x2 - (spacing * this.lasthand.size())/2;
			for(GraphicsCard card : this.lasthand) {
				g.drawImage(card.getFullsizeImage(), start, y2);
				start+=spacing;
			}
		} else {
			float start = this.x2 - (spacing * this.playedcards.size())/2;
			for(GraphicsCard card : this.playedcards) {
				g.drawImage(card.getFullsizeImage(), start, y2);
				start+=spacing;
			}
		}
	}


}