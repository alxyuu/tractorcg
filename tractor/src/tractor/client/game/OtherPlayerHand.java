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
	private float x_i, y_i, x2_i, y2_i;
	private static final int spacing = 4;
	private static final int spacing2 = 20;
	private String name;
	private List<GraphicsCard> playedcards;
	private List<GraphicsCard> lasthand;
	private int score;

	/** It constructs the other players hand
	 * @param x
	 * @param y
	 */
	OtherPlayerHand(double x, double y, double x2, double y2) {
		this.cards = 0;
		this.name = null;
		this.x = this.x_i = (float)(x - ( GraphicsCard.SCALED_WIDTH - spacing )/2);
		this.y = this.y_i = (float)(y-GraphicsCard.SCALED_HEIGHT/2);
		this.x2 = this.x2_i = (float)(x2 - ( GraphicsCard.SCALED_WIDTH*2 - spacing2 )/2);
		this.y2 = this.y2_i = (float)(y2-GraphicsCard.SCALED_HEIGHT);
		this.playedcards = Collections.emptyList();
	}

	/** It sets the player given the name parameter.
	 * @param name
	 */
	public void setPlayer(String name) {
		this.name = name;
	}

	/** It adds cards to the other players hand
	 * 
	 */
	public void addCard() {
		this.cards++;
	}

	/** It removes cards from the other players hand
	 * 
	 */
	public void removeCard() {
		this.cards--;
	}
	public void removeCard(int num) {
		this.cards -= num;
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
		this.playedcards.clear();
	}

	/** It clears the other players hand
	 * 
	 */
	public void reset() {
		this.cards = 0;
		this.x = this.x_i;
		this.y = this.y_i;
		this.x2 = this.x2_i;
		this.y2 = this.y2_i;
	}

	/** It draws the other players hand
	 * @param g
	 * @throws SlickException
	 */
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

	/** It sets the score.
	 * @param score
	 */
	public void setScore(int score) {
		this.score = score;
	}
	/** It retrieves the score.
	 * @return
	 */
	public int getScore() {
		return this.score;
	}


}
