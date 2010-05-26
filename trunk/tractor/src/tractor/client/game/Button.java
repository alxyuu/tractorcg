package tractor.client.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import tractor.thirdparty.MouseOverArea;

import tractor.client.Client;
import tractor.client.handlers.IOFactory;
import tractor.lib.GameCommand;

public class Button extends MouseOverArea {

	boolean enabled,show;
	private Image normalImage;
	private Image disabledImage;
	private Image mouseoverImage;
	private IOFactory io;
	private ButtonPressedListener listener;

	/** It constructs the button.
	 * @param container
	 * @param normal
	 * @param mouseover
	 * @param x
	 * @param y
	 */
	public Button(GUIContext container, Image normal, Image mouseover, int x, int y) {
		super(container, normal, x, y);

		this.normalImage = normal;
		this.mouseoverImage = mouseover;
		this.setNormalImage(normalImage);
		this.setMouseOverImage(mouseoverImage);

		this.io = Client.getInstance().getIO();

		this.hide();
		//this.show();
	}
	/** It constructs the button.
	 * @param container
	 * @param normalImage
	 * @param disabledImage
	 * @param mouseoverImage
	 * @param x
	 * @param y
	 */
	public Button(GUIContext container, Image normalImage, Image disabledImage, Image mouseoverImage, int x, int y) {
		super(container, normalImage, x-normalImage.getWidth()/2, y-normalImage.getHeight()/2);

		this.normalImage = normalImage;
		this.disabledImage = disabledImage;
		this.mouseoverImage = mouseoverImage;
		this.io = Client.getInstance().getIO();

		this.hide();
	}

	/** It shows the button.
	 * 
	 */
	public void show() {
		this.show = true;
	}
	/** It hides the button
	 * 
	 */
	public void hide() {
		this.disable();
		this.show = false;
	}
	/** It enables the use of the button.
	 * 
	 */
	public void enable() {
		this.enabled = true;
		this.setNormalImage(normalImage);
		this.setMouseOverImage(mouseoverImage);
	}

	/** It disables the use of the button.
	 * 
	 */
	public void disable() {
		this.enabled = false;
		this.setNormalImage(disabledImage);
		this.setMouseOverImage(disabledImage);
	}

	/** It adds a listener that checks whether the button has been pressed
	 * @param l
	 */
	public void addButtonPressedListener(ButtonPressedListener l) {
		this.listener = l;
	}
	public void mouseReleased(int button, int mx, int my) {
		if(this.enabled && this.listener != null && this.isMouseOver()) {
			this.listener.buttonPressed();
		}
	}
	/*public void mousePressed(int button, int mx, int my) {
		System.out.println("pressed");
	}*/
	public void render(GUIContext game, Graphics g) {
		if(this.show)
			super.render(game,g);
	}
}

