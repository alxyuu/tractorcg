package tractor.client.game;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Set;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
	private boolean isHost;
	private boolean callingEnabled;
	private Button spades, clubs, diamonds, hearts, notrump;
	private Color background;
	private int called_cards;
	private int state;
	//private OtherPlayerHand hand;
    public TractorGame(int position, int players, String name) {
        super("Tractor "+players+"-way");
        this.players = players;
        this.position = position;
        this.name = name;
        this.hands = new HashMap<String,OtherPlayerHand>();
        this.isHost = false;
        this.called_cards = 0;
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
        this.background = new Color(0,150,0);
        this.startButton = new Button(container,GraphicsCard.getCard(GraphicsCard.DIAMONDS,GraphicsCard.ACE).getImage(),GraphicsCard.getCard(GraphicsCard.CLUBS,GraphicsCard.ACE).getImage(),GraphicsCard.getCard(GraphicsCard.HEARTS,GraphicsCard.ACE).getImage(),container.getWidth()/2,container.getHeight()/2);
        this.startButton.addButtonPressedListener(new ButtonPressedListener() {
        	public void buttonPressed() {
        		io.write(GameCommand.START+"",IOFactory.GAMECMD);
        	}
        });
        try {
	        this.spades = new Button(container,new Image("images/suits/"+GraphicsCard.SPADES+".png"), new Image("images/suits/"+GraphicsCard.SPADES+"s.png"), 600, 380);
	        this.spades.addButtonPressedListener(new ButtonPressedListener() {
	        	public void buttonPressed() {
	        		io.write(GameCommand.PLAY_CARD+" "+GraphicsCard.SPADES + " " + GraphicsCard.TRUMP_NUMBER + " " + (++called_cards), IOFactory.GAMECMD);
	        	}
	        });
	        this.clubs = new Button(container,new Image("images/suits/"+GraphicsCard.CLUBS+".png"), new Image("images/suits/"+GraphicsCard.CLUBS+"s.png"), 631, 380);
	        this.clubs.addButtonPressedListener(new ButtonPressedListener() {
	        	public void buttonPressed() {
	        		io.write(GameCommand.PLAY_CARD+" "+GraphicsCard.CLUBS + " " + GraphicsCard.TRUMP_NUMBER + " " + (++called_cards), IOFactory.GAMECMD);
	        	}
	        });
	        this.diamonds = new Button(container,new Image("images/suits/"+GraphicsCard.DIAMONDS+".png"), new Image("images/suits/"+GraphicsCard.DIAMONDS+"s.png"), 662, 380);
	        this.diamonds.addButtonPressedListener(new ButtonPressedListener() {
	        	public void buttonPressed() {
	        		io.write(GameCommand.PLAY_CARD+" "+GraphicsCard.DIAMONDS + " " + GraphicsCard.TRUMP_NUMBER + " " + (++called_cards), IOFactory.GAMECMD);
	        	}
	        });
	        this.hearts = new Button(container,new Image("images/suits/"+GraphicsCard.HEARTS+".png"), new Image("images/suits/"+GraphicsCard.HEARTS+"s.png"), 693, 380);
	        this.hearts.addButtonPressedListener(new ButtonPressedListener() {
	        	public void buttonPressed() {
	        		io.write(GameCommand.PLAY_CARD+" "+GraphicsCard.HEARTS + " " + GraphicsCard.TRUMP_NUMBER + " " + (++called_cards), IOFactory.GAMECMD);
	        	}
	        });
	        this.notrump = new Button(container,new Image("images/suits/"+GraphicsCard.TRUMP+".png"), new Image("images/suits/"+GraphicsCard.TRUMP+"s.png"), 724, 380);
	        this.notrump.addButtonPressedListener(new ButtonPressedListener() {
	        	public void buttonPressed() {
	        		//TODO: differentiate between big and small
	        		io.write(GameCommand.PLAY_CARD+" "+GraphicsCard.TRUMP + " " + GraphicsCard.SMALL_JOKER + " " + (called_cards), IOFactory.GAMECMD);
	        	}
	        });
        } catch (SlickException e) {
        	e.printStackTrace();
        }
        
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
        this.hand = new PlayerHand(container.getWidth()/2,container.getHeight()-100);
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
    			this.state = secondary;
    			switch(secondary) {
    			case GameCommand.WAITING: 
    			{
    				//clear stuff and sit there?
    				this.startButton.disable();
    			}
    			break;
    			case GameCommand.READY:
    			{
    				this.startButton.enable();
    			}
    			break;
    			case GameCommand.DEALING:
    			{
    				this.startButton.hide();
        			this.callingEnabled = true;
    			}
    			break;
    			case GameCommand.DIPAI:
    			{
    				//do nothing?
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
    		case GameCommand.DEALING:
    		{
    			if(message[1].equals(Client.getInstance().getUsername())) {
    				GraphicsCard toadd = GraphicsCard.getCard(message[2],message[3]);
    				this.hand.addCard(container,toadd);
    				if(toadd.getNumber() == GraphicsCard.TRUMP_NUMBER && this.hand.frequency(toadd) > called_cards || toadd.getSuit() == GraphicsCard.TRUMP && this.hand.frequency(toadd) >= called_cards && this.hand.frequency(toadd) >= 2) {
    					switch(toadd.getSuit()) {
    					case GraphicsCard.SPADES:
    						this.spades.enable();
    						this.spades.show();
    						break;
    					case GraphicsCard.CLUBS:
    						this.clubs.enable();
    						this.clubs.show();
    						break;
    					case GraphicsCard.DIAMONDS:
    						this.diamonds.enable();
    						this.diamonds.show();
    						break;
    					case GraphicsCard.HEARTS:
    						this.hearts.enable();
    						this.hearts.show();
    						break;
    					case GraphicsCard.TRUMP:
    						this.notrump.enable();
    						this.notrump.show();
    						break;
    					default:
    						//some shit went wrong
    						System.out.println("STRANGER DANGER");
    					}
    				}
    			} else {
    				this.hands.get(message[1]).addCard();
    			}
    		}
    		break;
    		case GameCommand.PLAY_CARD:
    		{
    			if(this.state == GameCommand.DEALING) { // being called
    				
    			} else if (this.state == GameCommand.PLAYING) {
    				
    			} else {
    				System.out.println("STRANGER DANGER");
    			}
    		}
    		break;
    		case GameCommand.SET_HOST:
    		{
    			if(message[1].equals(Client.getInstance().getUsername())) {
    				this.isHost = true;
    				this.startButton.show();
    			} else {
    				this.isHost = false;
    				this.startButton.hide();
    			}
    		}
    		break;
    		}
    	}
    }

    @Override
    public void render(GameContainer container, Graphics g)
            throws SlickException {
    	g.setColor(this.background);
    	g.fillRect(0,0,container.getWidth(),container.getHeight());
    	g.setColor(Color.white);
        g.drawString("Hello, Slick world!", 0, 100);
        Set<String> keyset = hands.keySet();
        for(String key : keyset) {
        	hands.get(key).render(g);
        }
        this.startButton.render(container,g);
        if(this.callingEnabled) {
        	g.setColor(Color.black);
        	g.drawLine(630, 380, 631, 410);
        	g.drawLine(661, 380, 662, 410);
        	g.drawLine(692, 380, 693, 410);
        	g.drawLine(723, 380, 724, 410);
        	this.spades.render(container,g);
        	this.clubs.render(container,g);
        	this.diamonds.render(container,g);
        	this.hearts.render(container,g);
        	this.notrump.render(container,g);
        }
        this.hand.render(container,g);
    }
    
    public int getState() {
    	return this.state;
    }
}
