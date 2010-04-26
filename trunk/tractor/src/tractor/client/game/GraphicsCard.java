package tractor.client.game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import tractor.lib.Card;

public class GraphicsCard extends Card {
	
	private static Image back;
	static {
		try {
			back = new Image("images/cards/back.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Image getBackImage() {
		return GraphicsCard.back;
	}

	public static GraphicsCard getCard(int suit, int value) {
		return (GraphicsCard)Card.getCard(suit,value);
	}
	
	public static void populateDeck() {
		for(int i=0; i<4; i++) {
			for(int k=0; k<13; k++) {
				cards.add(new GraphicsCard(i,k,"images/cards/s"+i+"_c"+k+".png"));
			}
		}
		cards.add(new GraphicsCard(Card.TRUMP, Card.SMALL_JOKER, "images/cards/joker_s.png"));
		cards.add(new GraphicsCard(Card.TRUMP, Card.BIG_JOKER, "images/cards/joker_s.png"));
	}
	
	private Image image;
	
	GraphicsCard(int suit, int value, String filename) {
		super(suit,value);
		try {
			this.image = new Image(filename);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public Image getImage() {
		return this.image;
	}
}
