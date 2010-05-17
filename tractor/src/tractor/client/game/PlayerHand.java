package tractor.client.game;

import tractor.client.Client;
import tractor.client.game.GraphicsCard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class PlayerHand {

	private int cards;
	private float x, y;
	private static final int spacing = 4;
	private String name;
	
	PlayerHand(float x, float y) {
		this.cards = 0;
		this.name = Client.getInstance().getUsername();
		this.x = x;
		this.y = y;
	}
	
	public void addCard() {
		this.cards++;
	}
	
	public void removeCard() {
		this.cards--;
	}
	
	public void reset() {
		this.cards = 0;
	}
	
	public void render(Graphics g) throws SlickException {
		if(this.name == null) {
			//TODO: paint no player stuff
			g.drawString("?",x,y);
		} else {
			float start = this.x - (spacing * this.cards)/2;
			for(int i=0; i<this.cards; i++) {
				g.drawImage(GraphicsCard.getBackImage(), start, y);
				start+=spacing;
			}
			g.drawString(this.name,x,y);
		}
	}
	
	
}
