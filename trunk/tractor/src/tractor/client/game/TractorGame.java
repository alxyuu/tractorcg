package tractor.client.game;

import java.util.HashMap;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.AppGameContainer;

import tractor.client.Client;
import tractor.client.handlers.IOFactory;
import tractor.lib.GameCommand;

public class TractorGame extends BasicGame {

	private IOFactory io;
	private HashMap<String,OtherPlayerHand> hands;
	private int players;
    public TractorGame(int players) {
        super("Tractor "+players+"-way");
        this.players = players;
        this.hands = new HashMap<String,OtherPlayerHand>();
        this.io = Client.getInstance().getIO();
        for(int i=1;i<players;i++) {
        	//TODO: calculate positions
        	hands.put("Player"+i, new OtherPlayerHand(0,0));
        }
    }
    
    @Override
    public void init(GameContainer container) throws SlickException {
    	System.out.println("init");
    	//this.io = Client.getInstance().getIO();
        GraphicsCard.populateDeck();
        /*this.hand = new OtherPlayerHand(100,200);
        for(int i=0;i<20;i++) {
        	this.hand.addCard();
        }*/
    }

    @Override
    public void update(GameContainer container, int delta)
            throws SlickException {
    	//System.out.println(delta);
    	while(io.hasNextMessage(IOFactory.GAMECMD)) {
    		String[] message = io.getNextMessage(IOFactory.GAMECMD).split(" ");
    		int primary = GameCommand.get(message[0]);
    		switch(primary) {
    		case GameCommand.UPDATE_STATE:
    			int secondary = GameCommand.get(message[1]);
    			switch(secondary) {
    			case GameCommand.WAITING:
    				//clear stuff and sit there?
    				break;
    			}
    			break;
    		case GameCommand.JOIN:
    			int position;
    			try {
    				position = Integer.parseInt(message[1]);
    				if(!this.hands.containsKey("Player"+position))
    					throw new Exception("random bullshit");
    			} catch (Exception e) {
    				//TODO: illegal position
    				break;
    			}
    			String username = message[2];
    			OtherPlayerHand hand = this.hands.remove("Player"+position);
    			this.hands.put(username, hand);
    			break;
    		}
    	}
    }

    @Override
    public void render(GameContainer container, Graphics g)
            throws SlickException {
        g.drawString("Hello, Slick world!", 0, 100);
       g.drawImage(GraphicsCard.getCard(GraphicsCard.CLUBS,GraphicsCard.ACE).getImage(), 100,100);
       //this.hand.render(g);
    }
}
