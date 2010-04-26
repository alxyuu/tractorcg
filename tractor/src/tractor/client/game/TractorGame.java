package tractor.client.game;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;

import tractor.client.Client;
import tractor.lib.IOFactory;

public class TractorGame extends BasicGame {

	private IOFactory io;
    public TractorGame() {
        super("SimpleTest");
    }
    
    @Override
    public void init(GameContainer container) throws SlickException {
    	System.out.println("init");
    	//this.io = Client.getInstance().getIO();
        GraphicsCard.populateDeck();
    }

    @Override
    public void update(GameContainer container, int delta)
            throws SlickException {
    	//System.out.println(delta);
    	/*while(io.hasNextMessage(IOFactory.GAMECMD)) {
    		io.getNextMessage(IOFactory.GAMECMD);
    	}*/
    }

    @Override
    public void render(GameContainer container, Graphics g)
            throws SlickException {
        g.drawString("Hello, Slick world!", 0, 100);
       g.drawImage(GraphicsCard.getCard(GraphicsCard.CLUBS,GraphicsCard.ACE).getImage(), 100,100);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer app = new AppGameContainer(new TractorGame());
            app.setDisplayMode(704,396,false);
            //app.setTargetFrameRate(30);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
