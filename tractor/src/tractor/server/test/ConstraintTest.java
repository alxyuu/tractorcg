package tractor.server.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import tractor.lib.Card;
import tractor.server.PlayerHand;
import tractor.server.test.User;

public class ConstraintTest {
	private User currentUser;
	private List<User> users;
	private User lead;
	private User highest;
	private int currentSuit;
	private int TRUMP_SUIT, TRUMP_NUMBER;
	
	public static void main(String[] args) {
		Card.populateDeck();
		new ConstraintTest().test();
	}
	
	ConstraintTest() {
		this.users = new ArrayList<User>();
		users.add(new User());
		users.add(new User());
		users.add(new User());
		users.add(new User());
		for(User user : users) {
			user.newHand();
		}
		lead = users.get(0);
		highest = users.get(0);
		
		/*
		 * MAKE THE PLAYER HANDS HERE
		 */
		PlayerHand hand1 = users.get(0).getHand();
		hand1.addCard(Card.getCard(Card.SPADES, Card.ACE));
		
		PlayerHand hand2 = users.get(1).getHand();
		//add cards
		
		PlayerHand hand3 = users.get(2).getHand();
		//add cards
		
		PlayerHand hand4 = users.get(3).getHand();
		//add cards
	}
	
	public void test() {
		
		/*
		 * SET TRUMP AND ADD CARDS HERE
		 */
		this.TRUMP_SUIT = Card.SPADES;
		this.TRUMP_NUMBER = Card.JACK;
		
		User user = users.get(0);

		ArrayList<Card> played = new ArrayList<Card>();
		played.add(Card.getCard(Card.SPADES,Card.ACE));
		//add cards
		
		
		
		
		
		Collections.sort(played);
		
		if(user == this.lead) {
			//the player is the first player, check to make sure the play is high
			
			Iterator<Card> it = played.iterator();
			Card card = it.next();
			int suit = (card.getNumber() == this.TRUMP_NUMBER) ? Card.TRUMP : card.getSuit();
			while(it.hasNext()) {
				card = it.next();
				//if it's not the same suit and both this and the previous suit aren't trump
				if(!(suit == card.getSuit() || (card.getSuit() == this.TRUMP_SUIT || card.getSuit() == Card.TRUMP || card.getNumber() == this.TRUMP_NUMBER) && (suit == this.TRUMP_SUIT || suit == Card.TRUMP))) {
					System.out.println("must play same suit");
					return;
				}
				suit = ( card.getNumber() == this.TRUMP_NUMBER ) ? Card.TRUMP : card.getSuit();
			}
			this.currentSuit = suit;
			
			
			/*boolean isHigh=true;
			int suits=0;
			int length=2;
			List<Card> Tractor2=user.getHand().Tractors(0,length,suits);
			List<Card> Pairs=user.getHand().Pairs(0,suits);
			Card Singles=user.getHand().Singles(0,suits);
			for(Iterator<User> i2 = users.iterator();i2.hasNext();)
			{
				User u = i2.next();
				if(u != user)
				{
					PlayerHand friedchicken = u.getHand();
					List<Card> seven = friedchicken.getCards();

				}
			}*/
			
			
			

		} else {
			//not lead, check following suit, playing doubles/tractors/triples/whatever
			//compare to highest user's play
			//make sure the number of cards are correct
			
			
			

		}
	}
}
