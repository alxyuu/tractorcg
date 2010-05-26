package tractor.client.game;

import java.util.List;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import java.util.Iterator;

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
import tractor.lib.MessageFactory;
import tractor.server.User;

public class TractorGame extends BasicGame {

	private IOFactory io;
	private HashMap<String,OtherPlayerHand> hands;
	private int players;
	private int position;
	private GameContainer gamecontainer;
	private String name;
	private PlayerHand hand;
	private Button startButton;
	private boolean isHost;
	private boolean callingEnabled;
	private Button spades, clubs, diamonds, hearts, notrump;
	private Color background;
	private int called_cards;
	private int state;
	private int score;
	private List<GraphicsCard> dipai;
	private ArrayList<CardButton> selected;
	private boolean showDipai;
	private Button playButton;
	private String errorMessage;
	//private OtherPlayerHand hand;
	public TractorGame(int position, int players, String name) {
		super("Tractor "+players+"-way");
		this.players = players;
		this.position = position;
		this.name = name;
		this.hands = new HashMap<String,OtherPlayerHand>();
		this.isHost = false;
		this.called_cards = 0;
		this.selected = new ArrayList<CardButton>();
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
		this.gamecontainer = container;
		this.io = Client.getInstance().getIO();
		this.background = new Color(0,150,0);
		this.startButton = new Button(container,GraphicsCard.getCard(GraphicsCard.DIAMONDS,GraphicsCard.ACE).getImage(),GraphicsCard.getCard(GraphicsCard.CLUBS,GraphicsCard.ACE).getImage(),GraphicsCard.getCard(GraphicsCard.HEARTS,GraphicsCard.ACE).getImage(),container.getWidth()/2,container.getHeight()/2);
		this.startButton.addButtonPressedListener(new ButtonPressedListener() {
			public void buttonPressed() {
				sendCommand(GameCommand.START+"");
			}
		});
		try {
			this.spades = new Button(container,new Image("images/suits/"+GraphicsCard.SPADES+".png"), new Image("images/suits/"+GraphicsCard.SPADES+"s.png"), 600, 370);
			this.spades.addButtonPressedListener(new ButtonPressedListener() {
				public void buttonPressed() {
					sendCommand(GameCommand.PLAY_CARD+" "+GraphicsCard.SPADES + " " + GraphicsCard.TRUMP_NUMBER + " " + (called_cards+1));
				}
			});
			this.clubs = new Button(container,new Image("images/suits/"+GraphicsCard.CLUBS+".png"), new Image("images/suits/"+GraphicsCard.CLUBS+"s.png"), 631, 370);
			this.clubs.addButtonPressedListener(new ButtonPressedListener() {
				public void buttonPressed() {
					sendCommand(GameCommand.PLAY_CARD+" "+GraphicsCard.CLUBS + " " + GraphicsCard.TRUMP_NUMBER + " " + (called_cards+1));
				}
			});
			this.diamonds = new Button(container,new Image("images/suits/"+GraphicsCard.DIAMONDS+".png"), new Image("images/suits/"+GraphicsCard.DIAMONDS+"s.png"), 662, 370);
			this.diamonds.addButtonPressedListener(new ButtonPressedListener() {
				public void buttonPressed() {
					sendCommand(GameCommand.PLAY_CARD+" "+GraphicsCard.DIAMONDS + " " + GraphicsCard.TRUMP_NUMBER + " " + (called_cards+1));
				}
			});
			this.hearts = new Button(container,new Image("images/suits/"+GraphicsCard.HEARTS+".png"), new Image("images/suits/"+GraphicsCard.HEARTS+"s.png"), 693, 370);
			this.hearts.addButtonPressedListener(new ButtonPressedListener() {
				public void buttonPressed() {
					sendCommand(GameCommand.PLAY_CARD+" "+GraphicsCard.HEARTS + " " + GraphicsCard.TRUMP_NUMBER + " " + (called_cards+1));
				}
			});
			this.notrump = new Button(container,new Image("images/suits/"+GraphicsCard.TRUMP+".png"), new Image("images/suits/"+GraphicsCard.TRUMP+"s.png"), 724, 370);
			this.notrump.addButtonPressedListener(new ButtonPressedListener() {
				public void buttonPressed() {
					//TODO: differentiate between big and small
					sendCommand(GameCommand.PLAY_CARD+" "+GraphicsCard.TRUMP + " " + GraphicsCard.SMALL_JOKER + " " + (called_cards));
				}
			});
			this.playButton = new Button(container,new Image("images/play.png"), new Image("images/play_over.png"), container.getWidth()/2-30, 370);
			this.playButton.addButtonPressedListener(new ButtonPressedListener() {
				public void buttonPressed() {
					//might need to deselect cards but they sohuld be removed when the server responds anyways, doesn't matter?
					String cmd = GameCommand.PLAY_CARD + " " + selected.size();
					for(Iterator<CardButton> i = selected.iterator(); i.hasNext();) {
						GraphicsCard card = i.next().getCard();
						cmd += " " + card.getSuit() + " " + card.getNumber();
					}
					sendCommand(cmd);
					state = GameCommand.WAITING;
					playButton.hide();
				}
			});
		} catch (SlickException e) {
			e.printStackTrace();
		}

		Point2D.Double point,point2;
		switch(players) {
		case 2:
			hands.put("Player" + ( (position == 1) ? 2 : 1 ), new OtherPlayerHand(container.getWidth()/2, 50, container.getWidth()/2,125));
			break;
		case 3:
			point = getCoordinates(5*Math.PI/6);
			point2 = getTableCoordinates(5*Math.PI/6);
			hands.put("Player"+(position%players+1), new OtherPlayerHand(point.x,point.y,point2.x,point2.y));
			point = getCoordinates(Math.PI/6);
			point2 = getTableCoordinates(Math.PI/6);
			hands.put("Player"+((position+1)%players+1), new OtherPlayerHand(point.x,point.y,point2.x,point2.y));
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
				point2 = getTableCoordinates(theta);
				hands.put("Player"+(i%players+1), new OtherPlayerHand(point.x,point.y,point2.x,point2.y));
				theta -= increment;
			}
			//hands.put("Player"+((players+position-2)%players+1), new OtherPlayerHand(container.getWidth()-100,container.getHeight()/2));
		}
		this.hand = new PlayerHand(container.getWidth()/2,container.getHeight()-100, container.getWidth()/2,container.getHeight()-250 );
	}

	public Point2D.Double getCoordinates(double theta) {
		double radius = (gamecontainer.getWidth()/2-150)*(gamecontainer.getHeight()/2-50)/Math.sqrt(Math.pow((gamecontainer.getHeight()/2-50)*Math.cos(theta), 2) + Math.pow((gamecontainer.getWidth()/2-150)*Math.sin(theta), 2));
		return new Point2D.Double(gamecontainer.getWidth()/2+radius*Math.cos(theta), gamecontainer.getHeight()/2-radius*Math.sin(theta));
	}

	public Point2D.Double getTableCoordinates(double theta) {
		double radius = (gamecontainer.getWidth()/2-350)*(gamecontainer.getHeight()/2-125)/Math.sqrt(Math.pow((gamecontainer.getHeight()/2-125)*Math.cos(theta), 2) + Math.pow((gamecontainer.getWidth()/2-350)*Math.sin(theta), 2));
		return new Point2D.Double(gamecontainer.getWidth()/2+radius*Math.cos(theta), gamecontainer.getHeight()/2-radius*Math.sin(theta));
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
			final String[] message = msg.split(" ");
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
					this.callingEnabled = false;
					this.spades.hide();
					this.clubs.hide();
					this.hearts.hide();
					this.diamonds.hide();
					this.notrump.hide();
				}
				break;
				case GameCommand.PLAYING:
				{
					System.out.println("game start CLEAR DAT DIPAI");
					//if doesn't have dipai this shouldn't do anything
					for(Iterator<CardButton> i = this.selected.iterator(); i.hasNext();) {
						this.hand.removeCard(i.next());
					}
					this.selected.clear();
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
			case GameCommand.YOUR_TURN:
			{
				this.playButton.enable();
				this.playButton.show();
			}
			break;
			case GameCommand.PLAY_SUCCESS:
			{
				ArrayList<GraphicsCard> list = new ArrayList<GraphicsCard>();
				for(Iterator<CardButton> i = this.selected.iterator(); i.hasNext();) {
					CardButton card = i.next();
					list.add(card.getCard());
					this.hand.removeCard(card);
				}
				this.hand.playCards(list);
				this.selected.clear();
				this.state = GameCommand.PLAYING;
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
					checkCalling(toadd);
				} else {
					this.hands.get(message[1]).addCard();
				}
			}
			break;
			case GameCommand.PLAY_CARD:
			{
				if(this.state == GameCommand.DEALING) { // being called
					this.called_cards = Integer.parseInt(message[3]);
					ArrayList<GraphicsCard> list = new ArrayList<GraphicsCard>();
					for(int i = 0; i < called_cards; i++) {
						list.add(GraphicsCard.getCard(Integer.parseInt(message[2]),GraphicsCard.TRUMP_NUMBER));
					}

					// I'm so cool
					hand.sort(Integer.parseInt(message[2]));
					
					if(message[1].equals(Client.getInstance().getUsername())) {
						this.hand.playCards(list);
					} else {
						this.hands.get(message[1]).playCards(list);
					}
					checkAllCalling();
				} else if (this.state == GameCommand.PLAYING) {
					int cards = Integer.parseInt(message[2]);
					ArrayList<GraphicsCard> list = new ArrayList<GraphicsCard>();
					for(int i = 0; i < cards*2; i+=2) {
						list.add(GraphicsCard.getCard(message[i+3],message[i+4]));
					}
					
					if(message[1].equals(Client.getInstance().getUsername())) {
						this.hand.playCards(list);
					} else {
						this.hands.get(message[1]).playCards(list);
					}
				} else {
					System.out.println("STRANGER DANGER");
				}
			}
			break;
			case GameCommand.SET_STATS: 
			{
				GraphicsCard.TRUMP_NUMBER = Integer.parseInt(message[1]);
				int players = Integer.parseInt(message[2]);
				for( int i = 0; i < players*2; i+=2 ) {
					if(message[i+3].equals(Client.getInstance().getUsername())) {
						this.score = Integer.parseInt(message[i+4]);
					} else {
						this.hands.get(message[i+3]).setScore(Integer.parseInt(message[i+4]));
					}
				}
			}
			break;
			case GameCommand.CLEAR_TABLE:
			{
				for(Iterator<OtherPlayerHand> i = hands.values().iterator(); i.hasNext(); ) {
					i.next().clearTable();
				}
				hand.clearTable();
			}
			break;
			case GameCommand.DIPAI: 
			{
				Thread dpthread = new Thread() {
					public void run() {
						dipai = Collections.synchronizedList(new ArrayList<GraphicsCard>());
						int size = Integer.parseInt(message[1]);
						for(int i=0; i<size*2; i+=2) {
							dipai.add(GraphicsCard.getCard(message[i+2],message[i+3]));
						}
						showDipai = true;
						try{
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
						for(int i=0; i<size; i++) {
							hand.addCard(gamecontainer,dipai.get(i));
						}
						showDipai = false;
						playButton.enable();
						playButton.show();
					}
				};
				dpthread.start();
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
			case GameCommand.PLAY_INVALID:
			{
				this.errorMessage = message[1];
				for(int i=2; i<message.length;i++)
					errorMessage+=" "+message[i];
				this.playButton.enable();
				this.playButton.show();
				this.state = GameCommand.PLAYING;
			}
			break;
			default:
			{
				//some shit
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
		g.setColor(Color.black);
		for(Iterator<OtherPlayerHand> i = hands.values().iterator(); i.hasNext(); ) {
			i.next().render(g);
		}
		this.startButton.render(container,g);
		this.playButton.render(container,g);
		if(this.callingEnabled) {
			g.setColor(Color.black);
			g.drawLine(630, 370, 631, 400);
			g.drawLine(661, 370, 662, 400);
			g.drawLine(692, 370, 693, 400);
			g.drawLine(723, 370, 724, 400);
			this.spades.render(container,g);
			this.clubs.render(container,g);
			this.diamonds.render(container,g);
			this.hearts.render(container,g);
			this.notrump.render(container,g);
		}
		if(this.showDipai) {
			float x = container.getWidth()/2 - (100+this.dipai.size()*20)/2;
			float y = container.getHeight()/2 - 74;
			for(GraphicsCard card : this.dipai) {
				g.drawImage(card.getFullsizeImage(), x, y);
				x+=20;
			}
		}
		this.hand.render(container,g);
	}

	public int getState() {
		return this.state;
	}

	public void addSelected(CardButton card) {
		this.selected.add(card);
	}
	public void removeSelected(CardButton card) {
		this.selected.remove(card);
	}
	public void checkCalling(GraphicsCard card) {
		System.out.println("Card: "+card);
		if( ( card.getNumber() == GraphicsCard.TRUMP_NUMBER && this.hand.frequency(card) > called_cards ) || ( card.getSuit() == GraphicsCard.TRUMP && this.hand.frequency(card) >= called_cards && this.hand.frequency(card) >= 2 ) ) {
			switch(card.getSuit()) {
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
	}

	public void checkAllCalling() {
		this.spades.hide();
		this.clubs.hide();
		this.diamonds.hide();
		this.hearts.hide();
		this.notrump.hide();
		for(Iterator<GraphicsCard> i = this.hand.getCards().iterator(); i.hasNext(); ) {
			checkCalling(i.next());
		}
	}

	private void sendCommand(String message) {
		this.io.write(message, IOFactory.GAMECMD);
	}
}