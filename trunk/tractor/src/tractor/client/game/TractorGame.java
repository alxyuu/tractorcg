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
	private OtherPlayerHand hand;
	private int players;
    public TractorGame(int players) {
        super("Tractor "+players+"-way");
        this.players = players;
    }
    
    @Override
    public void init(GameContainer container) throws SlickException {
    	System.out.println("init");
    	//this.io = Client.getInstance().getIO();
        GraphicsCard.populateDeck();
        this.hand = new OtherPlayerHand(100,200);
        for(int i=0;i<20;i++) {
        	this.hand.addCard();
        }
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
       this.hand.render(g);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer app = new AppGameContainer(new TractorGame(4));
            app.setDisplayMode(960,600,false);
            //app.setTargetFrameRate(30);
            System.out.println("start");
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        System.out.println("continue");
    }
}
