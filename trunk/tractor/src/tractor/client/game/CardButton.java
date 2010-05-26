package tractor.client.game;

import org.newdawn.slick.gui.GUIContext;

import tractor.client.Client;
import tractor.lib.GameCommand;
import tractor.thirdparty.MouseOverArea;

public class CardButton extends MouseOverArea implements Comparable<CardButton> {
	private GraphicsCard card;
	private boolean selected;
	private TractorGame game;
	public CardButton(GUIContext container, GraphicsCard card, int x, int y, int width, int height) {
		super(container, card.getFullsizeImage(), x, y, width, height);
		this.card = card;
		this.selected = false;
		this.game = Client.getInstance().getGame();
	}
	public void mouseReleased(int button, int mx, int my) {
		if(this.game.getState() != GameCommand.DEALING  && this.game.getState() != GameCommand.WAITING && isMouseOver()) {
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
	public GraphicsCard getCard() {
		return this.card;
	}
	public int compareTo(CardButton cb) {
		int compare = this.card.compareTo(cb.card);
		return (compare == 0) ? ( (this==cb) ? 0 : -1 ): compare;
	}
}
