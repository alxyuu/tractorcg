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
				this.setLocation(this.getX(),this.getY()-20);
				this.game.addSelected(this);
			} else {
				this.setLocation(this.getX(),this.getY()+20);
				this.game.removeSelected(this);
			}
			this.selected = !this.selected;
		}
	}
	/** It gets the card.
	 * @return
	 */
	public GraphicsCard getCard() {
		return this.card;
	}
	private int getSortingSuit() {
		if (this.card.getSuit()== GraphicsCard.TRUMP)
			return GraphicsCard.TRUMP+GraphicsCard.TRUMP+2;
		else if(this.card.getSuit() == this.game.getTrumpSuit() && this.card.getNumber() == this.game.getTrumpNumber())
			return GraphicsCard.TRUMP+GraphicsCard.TRUMP+1;
		else if(this.card.getNumber() == this.game.getTrumpNumber())
			return GraphicsCard.TRUMP+this.card.getSuit()+1;
		else if(this.card.getSuit() == this.game.getTrumpSuit())
			return GraphicsCard.TRUMP;
		else
			return this.card.getSuit();
	}
	public int compareTo(CardButton cb) {
		int compare;
		if(this.getSortingSuit() == cb.getSortingSuit()) {
			compare = this.card.getNumber()-cb.card.getNumber();
		} else {
			compare = this.getSortingSuit() - cb.getSortingSuit();
		}
		return (compare == 0) ? ( (this==cb) ? 0 : -1 ): compare;
	}
	public String toString() {
		return card.toString() + " at "+this.getX()+","+this.getY();
	}
}
