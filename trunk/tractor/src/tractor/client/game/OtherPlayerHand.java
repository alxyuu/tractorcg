package tractor.client.game;

import tractor.client.game.GraphicsCard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class OtherPlayerHand {

	private int cards,x,y;
	private static final int spacing = 6;
	
	OtherPlayerHand(int x, int y) {
		this.cards = 0;
		this.x = x - ( GraphicsCard.SCALED_WIDTH - spacing )/2;
		this.y = y-GraphicsCard.SCALED_HEIGHT/2;
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
		int start = this.x - (spacing * this.cards)/2;
		for(int i=0; i<this.cards; i++) {
			g.drawImage(GraphicsCard.getBackImage(), start, y);
			start+=6;
		}
	}
	
	
}
