package tractor.client.game;

import tractor.client.game.GraphicsCard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class OtherPlayerHand {

	private int cards;
	private float x, y;
	private static final int spacing = 4;
	private String name;
	
	OtherPlayerHand(double x, double y) {
		this.cards = 0;
		this.name = null;
		this.x = (float)(x - ( GraphicsCard.SCALED_WIDTH - spacing )/2);
		this.y = (float)(y-GraphicsCard.SCALED_HEIGHT/2);
	}
	
	public void setPlayer(String name) {
		this.name = name;
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
