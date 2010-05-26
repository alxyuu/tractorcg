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
	public int compareTo(CardButton cb) {
		int compare = this.card.compareTo(cb.card);
		return (compare == 0) ? ( (this==cb) ? 0 : -1 ): compare;
	}
	public String toString() {
		String out = "";
		switch(this.card.getNumber()) {
		case GraphicsCard.TWO:
			if(card.getSuit() == GraphicsCard.TRUMP)
				out += "small joker";
			else
				out += "two";
			break;
		case GraphicsCard.THREE:
			if(card.getSuit() == GraphicsCard.TRUMP)
				out += "big joker";
			else
				out += "three";
			break;
		case GraphicsCard.FOUR:
			out += "four";
			break;
		case GraphicsCard.FIVE:
			out += "five";
			break;
		case GraphicsCard.SIX:
			out += "six";
			break;
		case GraphicsCard.SEVEN:
			out += "seven";
			break;
		case GraphicsCard.EIGHT:
			out+= "eight";
			break;
		case GraphicsCard.NINE:
			out+= "nine";
			break;
		case GraphicsCard.TEN:
			out += "ten";
			break;
		case GraphicsCard.JACK:
			out += "jack";
			break;
		case GraphicsCard.QUEEN:
			out += "queen";
			break;
		case GraphicsCard.KING:
			out += "king";
			break;
		case GraphicsCard.ACE:
			out += "ace";
			break;
		}
		out += " of ";
		switch(card.getSuit()) {
		case GraphicsCard.SPADES:
			out+="spades";
			break;
		case GraphicsCard.CLUBS:
			out+="clubs";
			break;
		case GraphicsCard.DIAMONDS:
			out+="diamonds";
			break;
		case GraphicsCard.HEARTS:
			out+="hearts";
			break;
		case GraphicsCard.TRUMP:
			out+="trump";
			break;
		}
		return out+" at "+this.getX() + "," + this.getY();
	}
}
