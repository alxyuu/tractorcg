package tractor.client.game;

import java.util.ArrayList;
import java.util.Collections;

import tractor.client.Client;
import tractor.client.game.GraphicsCard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class PlayerHand {

	private float x, y;
	private static final int spacing = 4;
	private ArrayList<GraphicsCard> cards;
	//private String name;
	
	PlayerHand(float x, float y) {
		//this.name = Client.getInstance().getUsername();
		this.x = x;
		this.y = y;
		this.cards = new ArrayList<GraphicsCard>();
	}
	
	public void addCard(GraphicsCard card) {
		this.cards.add(card);
		Collections.sort(this.cards);
	}
	
	public void removeCard(GraphicsCard card) {
		this.cards.remove(card);
	}
	
	public void render(Graphics g) throws SlickException {
		float start = this.x - (spacing * this.cards.size())/2;
		for(GraphicsCard card : this.cards){ 
			g.drawImage(card.getImage(), start, y);
			start+=spacing;
		}
		//g.drawString(this.name,x,y);
	}
	
	
}
