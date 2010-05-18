package tractor.client.game;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Set;

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
	private int position;
	private GameContainer container;
	private String name;
	private PlayerHand hand;
	private Button startButton;
	//private OtherPlayerHand hand;
    public TractorGame(int position, int players, String name) {
        super("Tractor "+players+"-way");
        this.players = players;
        this.position = position;
        this.name = name;
        this.hands = new HashMap<String,OtherPlayerHand>();
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
        this.container = container;
        this.io = Client.getInstance().getIO();
        this.startButton = new Button(container,GraphicsCard.getCard(GraphicsCard.CLUBS,GraphicsCard.ACE).getImage(),container.getWidth()/2,container.getHeight()/2);
        this.startButton.setMouseOverImage(GraphicsCard.getCard(GraphicsCard.HEARTS,GraphicsCard.ACE).getImage());
        Point2D.Double point;
        switch(players) {
        case 2:
        	hands.put("Player" + ( (position == 1) ? 2 : 1 ), new OtherPlayerHand(container.getWidth()/2, 50));
        	break;
        case 3:
        	point = getCoordinates(5*Math.PI/6);
        	hands.put("Player"+(position%players+1), new OtherPlayerHand(point.x,point.y));
        	point = getCoordinates(Math.PI/6);
        	hands.put("Player"+((position+1)%players+1), new OtherPlayerHand(point.x,point.y));
        	break;
        default:
        	if(players < 3) {
        		//TODO: invalid players
        		System.out.println("invalid players SOME SHIT WENT WRONG");
        		break;
        	}
        	//hands.put("Player"+(position%players+1), new OtherPlayerHand(100, container.getHeight()/2));
        	double increment = Math.PI/(players-2);
        	double theta = Math.PI;
	        for(int i=position;i<players+position-1;i++) {
	        	point = getCoordinates(theta);
	        	hands.put("Player"+(i%players+1), new OtherPlayerHand(point.x,point.y));
	        	theta -= increment;
	        }
	        //hands.put("Player"+((players+position-2)%players+1), new OtherPlayerHand(container.getWidth()-100,container.getHeight()/2));
        }
    }
    
    public Point2D.Double getCoordinates(double theta) {
       	double radius = (container.getWidth()/2-100)*(container.getHeight()/2-50)/Math.sqrt(Math.pow((container.getHeight()/2-50)*Math.cos(theta), 2) + Math.pow((container.getWidth()/2-100)*Math.sin(theta), 2));
    	return new Point2D.Double(container.getWidth()/2+radius*Math.cos(theta), container.getHeight()/2-radius*Math.sin(theta));
    }

    @Override
    public void update(GameContainer container, int delta)
            throws SlickException {
    	//System.out.println(delta);
    	while(io.hasNextMessage(IOFactory.GAMECMD)) {
    		String msg = io.getNextMessage(IOFactory.GAMECMD);
    		int index = msg.indexOf("|");
    		try {
    			if(!msg.substring(0,index).equals(this.name)) {
    				throw new SlickException("game name mismatch");
    			}
    		} catch (Exception e) {
    			//TODO: some error handler
    			e.printStackTrace();
    			return;
    		}
    		msg = msg.substring(index+1);
    		String[] message = msg.split(" ");
    		//int primary = GameCommand.get(message[0]);
    		int primary = Integer.parseInt(message[0]);
    		switch(primary) {
    		case GameCommand.UPDATE_STATE:
    		{
    			//int secondary = GameCommand.get(message[1]);
    			int secondary = Integer.parseInt(message[1]);
    			switch(secondary) {
    			case GameCommand.WAITING: 
    			{
    				//clear stuff and sit there?
    			}
    			break;
    			case GameCommand.READY:
    			{
    				
    			}
    			break;
    			default:
    			{
    				//TODO: state not found
    			}
    			break;
    			}
    		}
    		break;
    		case GameCommand.JOIN:
    		{
    			int position;
    			try {
    				position = Integer.parseInt(message[1]);
    				if(!this.hands.containsKey("Player"+position))
    					throw new Exception("random bullshit");
    			} catch (Exception e) {
    				//TODO: illegal position
    				e.printStackTrace();
    				break;
    			}
    			OtherPlayerHand hand = this.hands.remove("Player"+position);
    			hand.setPlayer(message[2]);
    			this.hands.put(message[2], hand);
    		}
    		break;
    		case GameCommand.PART:
    		{
    			if(message[1].equals(Client.getInstance().getUsername())) {
    				for(int a = 0; a<100000; a++)
    					System.out.println("the game");
    				//TODO: clean up and part
    			} else if(this.hands.containsKey(message[1])) {
    				OtherPlayerHand hand = this.hands.remove(message[1]);
    				hand.setPlayer(null);
    				int position = Integer.parseInt(message[2]);
    				this.hands.put("Player"+position, hand);
    			}
    			//TODO: user not found
    		}
    		break;
    		}
    	}
    }

    @Override
    public void render(GameContainer container, Graphics g)
            throws SlickException {
        g.drawString("Hello, Slick world!", 0, 100);
       //g.drawImage(GraphicsCard.getCard(GraphicsCard.CLUBS,GraphicsCard.ACE).getImage(), 100,100);
       //this.hand.render(g);
        Set<String> keyset = hands.keySet();
        for(String key : keyset) {
        	hands.get(key).render(g);
        }
        this.startButton.render(container,g);
    }
}
