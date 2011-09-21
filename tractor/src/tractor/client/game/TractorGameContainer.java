package tractor.client.game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;

import tractor.client.Client;
import tractor.client.handlers.IOFactory;

public class TractorGameContainer extends AppGameContainer{

	public TractorGameContainer(Game game) throws SlickException {
		super(game);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void destroy() {
		super.destroy();
		Client.getInstance().getIO().write("GPART "+((TractorGame)this.game).getName(), IOFactory.CHATCMD);
	}
	
	

}
