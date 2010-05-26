package tractor.client.game;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import tractor.client.game.GraphicsCard;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class OtherPlayerHand {

	private int cards;
	private float x, y, x2, y2;
	private static final int spacing = 4;
	private static final int spacing2 = 20;
	private String name;
	private List<GraphicsCard> playedcards;
	private int score;

	OtherPlayerHand(double x, double y, double x2, double y2) {
		this.cards = 0;
		this.name = null;
		this.x = (float)(x - ( GraphicsCard.SCALED_WIDTH - spacing )/2);
		this.y = (float)(y-GraphicsCard.SCALED_HEIGHT/2);
		this.x2 = (float)(x2 - ( GraphicsCard.SCALED_WIDTH*2 - spacing2 )/2);
		this.y2 = (float)(y2-GraphicsCard.SCALED_HEIGHT);
		this.playedcards = Collections.emptyList();
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

	public void playCards(GraphicsCard ... played) {
		this.playedcards = Collections.synchronizedList(Arrays.asList(played));
	}

	public void playCards(List<GraphicsCard> played) {
		this.playedcards = Collections.synchronizedList(played);
	}

	public void clearTable() {
		this.playedcards.clear();
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
			start = this.x2 - (spacing2 * this.playedcards.size())/2;
			for(GraphicsCard card : this.playedcards) {
				g.drawImage(card.getFullsizeImage(), start, y2);
				start+=spacing2;
			}
		}
	}

	public void setScore(int score) {
		this.score = score;
	}
	public int getScore() {
		return this.score;
	}


}
