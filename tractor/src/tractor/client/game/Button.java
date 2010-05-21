package tractor.client.game;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;

import tractor.client.Client;
import tractor.client.handlers.IOFactory;
import tractor.lib.GameCommand;

public class Button extends MouseOverArea {

	boolean enabled,show;
	private Image normalImage;
	private Image disabledImage;
	private Image mouseoverImage;
	private IOFactory io;
	
	public Button(GUIContext container, Image normalImage, Image disabledImage, Image mouseoverImage, int x, int y) {
		super(container, normalImage, x-normalImage.getWidth()/2, y-normalImage.getHeight()/2);
		
		this.normalImage = normalImage;
		this.disabledImage = disabledImage;
		this.mouseoverImage = mouseoverImage;
		this.io = Client.getInstance().getIO();
		
		this.show();
		this.disable();
	}
	
	public void show() {
		this.show = true;
	}
	public void hide() {
		this.disable();
		this.show = false;
	}
	public void enable() {
		this.enabled = true;
		this.setNormalImage(normalImage);
		this.setMouseOverImage(mouseoverImage);
	}
	
	public void disable() {
		this.enabled = false;
		this.setNormalImage(disabledImage);
		this.setMouseOverImage(disabledImage);
	}

	public void mouseReleased(int button, int mx, int my) {
		if(this.enabled) {
			io.write(GameCommand.START+"",IOFactory.GAMECMD);
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
