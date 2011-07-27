package tractor.client.game;

import org.newdawn.slick.gui.GUIContext;

import tractor.client.Client;
import tractor.lib.GameCommand;
import tractor.thirdparty.MouseOverArea;

public class CardButton extends MouseOverArea implements Comparable<CardButton> {
	private GraphicsCard card;
	private boolean selected;
	private TractorGame game;
	/** It constructs the card button.
	 * @param container
	 * @param card
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public CardButton(GUIContext container, GraphicsCard card, int x, int y, int width, int height) {
		super(container, card.getFullsizeImage(), x, y, width, height);
		this.card = card;
		this.selected = false;
		this.game = Client.getInstance().getGame();
	}
	public void mouseReleased(int button, int mx, int my) {
		if(this.game.getState() != GameCommand.DEALING  && this.game.getState() != GameCommand.WAITING && isMouseOver()) {
			System.out.println("CLICK: "+this);
			if(!selected) {
				this.up();
				this.game.addSelected(this);
			} else {
				this.down();
				this.game.removeSelected(this);
			}
		}
	}
	public void up() {
		this.setLocation(this.getX(),this.getY()-20);
		this.selected = true;
	}
	public void down() {
		this.setLocation(this.getX(),this.getY()+20);
		this.selected = false;
	}
	/** It gets the card.
	 * @return
	 */
	public GraphicsCard getCard() {
		return this.card;
	}
	public int compareTo(CardButton cb) {
		int compare = this.card.compareTo(cb.card);
		return (compare == 0) ? ( (this==cb) ? 0 : -1 ): compare;
	}
	public String toString() {
		return card.toString() + " at "+this.getX()+","+this.getY();
	}
}
