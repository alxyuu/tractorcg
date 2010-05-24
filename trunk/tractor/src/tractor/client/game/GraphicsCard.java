package tractor.client.game;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import tractor.lib.Card;

public class GraphicsCard extends Card {
	
	private static Image back,scaledback;
	public static final int SCALED_HEIGHT = 74;
	public static final int SCALED_WIDTH = 50;
	static {
		try {
			back = new Image("images/cards/back.png").getScaledCopy(100,148);
			scaledback = back.getScaledCopy(GraphicsCard.SCALED_WIDTH,GraphicsCard.SCALED_HEIGHT);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**Returns the image of the back of the card
	 * @return
	 * 
	 */
	public static Image getBackImage() {
		return GraphicsCard.scaledback;
	}
	
	public static Image getFullsizeBackImage() {
		return GraphicsCard.back;
	}

	/**It gets the card
	 * @param suit
	 * @param value
	 * @return
	 * 
	 */
	public static GraphicsCard getCard(int suit, int value) {
		return (GraphicsCard)Card.getCard(suit,value);
	}
	
	public static GraphicsCard getCard(String suit, String value) {
		return (GraphicsCard)Card.getCard(suit,value);
	}
	
	/**Fills the deck
	 * 
	 */
	public static void populateDeck() {
		for(int i=0; i<4; i++) {
			for(int k=0; k<13; k++) {
				cards.add(new GraphicsCard(i,k,"images/cards/s"+i+"_c"+k+".png"));
			}
		}
		cards.add(new GraphicsCard(Card.TRUMP, Card.SMALL_JOKER, "images/cards/joker_s.png"));
		cards.add(new GraphicsCard(Card.TRUMP, Card.BIG_JOKER, "images/cards/joker_b.png"));
	}
	
	private Image image;
	private Image scaled;
	
	GraphicsCard(int suit, int value, String filename) {
		super(suit,value);
		try {
			this.image = new Image(filename).getScaledCopy(100,148);
			this.scaled = image.getScaledCopy(GraphicsCard.SCALED_WIDTH,GraphicsCard.SCALED_HEIGHT);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	/**It returns the image of the card
	 * @return
	 * 
	 */
	public Image getImage() {
		return this.scaled;
	}
	
	public Image getFullsizeImage() {
		return this.image;
	}
}
