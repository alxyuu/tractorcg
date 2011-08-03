package tractor.client.game;

import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

public class PressButton extends Button {
	ButtonReleasedListener released;
	private boolean pressed;
	public PressButton(GUIContext container, Image normal, Image mouseover, int x, int y) {
		super(container, normal, mouseover, x, y);
		this.pressed = false;
	}
	public void addButtonReleasedListener(ButtonReleasedListener l) {
		this.released = l;
	}
	public void mouseReleased(int button, int mx, int my) {
		if(this.released != null && this.pressed) {
			this.released.buttonReleased();
		}
		this.pressed = false;
	}
	public void mousePressed(int button, int mx, int my) {
		if(this.listener != null && this.isMouseOver()) {
			this.listener.buttonPressed();
			this.pressed = true;
		}
	}
}
