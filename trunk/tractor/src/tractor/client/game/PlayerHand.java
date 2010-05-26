package tractor.client.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tractor.client.game.GraphicsCard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;

public class PlayerHand {

	private float x, y, x2, y2;
	private static final int spacing = 20;
	private Map<CardButton, GraphicsCard> hand;
	private Collection<GraphicsCard> cards;
	private List<GraphicsCard> playedcards;
	private List<GraphicsCard> lasthand;
	//private String name;

	PlayerHand(float x, float y, float x2, float y2) {
		//this.name = Client.getInstance().getUsername();
		this.x = x - ( 100 - spacing )/2;
		this.y = y - 74;
		this.x2 = x2 - ( 100 - spacing )/2;
		this.y2 = y2 - 74;
		this.hand = Collections.synchronizedMap(new TreeMap<CardButton, GraphicsCard>());
		this.cards = Collections.synchronizedCollection(hand.values());
		this.playedcards = Collections.emptyList();
		this.lasthand = Collections.emptyList();
	}

	public Collection<GraphicsCard> getCards() {
		return this.cards;
	}
	public int frequency(GraphicsCard card) {
		return Collections.frequency(this.cards, card);
	}
	public void addCard(GUIContext container, GraphicsCard card) {
		this.hand.put(new CardButton(container,card,0,0,spacing,148),card);
		updateLocations();
	}

	public void removeCard(CardButton card) {
		this.hand.remove(card);
		this.updateLocations();
	}
	public void removeCard(GraphicsCard card) {
		this.cards.remove(card);
		updateLocations();
	}

	public void playCards(GraphicsCard ... played) {
		this.playedcards = Collections.synchronizedList(Arrays.asList(played));
	}

	public void playCards(List<GraphicsCard> played) {
		this.playedcards = Collections.synchronizedList(played);
	}

	public void clearTable() {
		this.lasthand = this.playedcards;
		this.playedcards.clear();
	}

	private void updateLocations() {
		float start = this.x - (spacing * this.cards.size())/2;
		CardButton card = null;
		for(Iterator<CardButton> i = hand.keySet().iterator(); true; ){
			card = i.next();
			card.setLocation(start,y);
			if(i.hasNext()) {
				card.setSize(spacing,148);
				start+=spacing;
			} else {
				card.setSize(100,148);
				break;
			}
		}		
	}
	public void render(GUIContext container, Graphics g) throws SlickException {
		for(Iterator<CardButton> i = hand.keySet().iterator(); i.hasNext(); ) {
			i.next().render(container, g);
		}
		float start = this.x2 - (spacing * this.playedcards.size())/2;
		for(GraphicsCard card : this.playedcards) {
			g.drawImage(card.getFullsizeImage(), start, y2);
			start+=spacing;
		}
	}


}
