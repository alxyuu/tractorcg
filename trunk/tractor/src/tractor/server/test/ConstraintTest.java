package tractor.server.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
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
	private Trick currenTrick;
	
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
		hand1.addCard(Card.getCard(Card.SPADES, Card.ACE));
		hand1.addCard(Card.getCard(Card.SPADES, Card.KING));
		hand1.addCard(Card.getCard(Card.SPADES, Card.KING));
		
		PlayerHand hand2 = users.get(1).getHand();
		//add cards
		hand2.addCard(Card.getCard(Card.TRUMP, Card.SMALL_JOKER));
		hand2.addCard(Card.getCard(Card.TRUMP, Card.SMALL_JOKER));
		hand2.addCard(Card.getCard(Card.SPADES, Card.JACK));
		hand2.addCard(Card.getCard(Card.SPADES, Card.JACK));
		
		PlayerHand hand3 = users.get(2).getHand();
		//add cards
		hand3.addCard(Card.getCard(Card.SPADES, Card.SIX));
		hand3.addCard(Card.getCard(Card.SPADES, Card.SIX));
		hand3.addCard(Card.getCard(Card.SPADES, Card.TEN));
		hand3.addCard(Card.getCard(Card.SPADES, Card.TEN));
		
		PlayerHand hand4 = users.get(3).getHand();
		//add cards
		hand1.addCard(Card.getCard(Card.DIAMONDS, Card.THREE));
		hand1.addCard(Card.getCard(Card.SPADES, Card.FOUR));
		hand1.addCard(Card.getCard(Card.SPADES, Card.FIVE));
		hand1.addCard(Card.getCard(Card.SPADES, Card.FIVE));
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
			
			Trick trick = calculateTrick(played);
			
			//check if high only if there's more than one play
			if(trick.countPlays() >= 1) {
				
				//cock goes here
				
			} else if (trick.countPlays() == 0) { // this should never happen...
				System.out.println("some bad shit happened");
				return;
			}
			
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
					List<Card> seven = friedchicken.getCards(		Iterator <Card>it2=user.getHand().iterator();
		while(it2.hasNext())
		{
			Card c=it2.next();
			user.getHand().frequency(c);
		});

				}
			}*/
			
			
			
			//the trick is valid so set it
			this.currenTrick = trick;
			//assume lead is always high until beaten by someone else
			this.highest = user;
			//don't set suit until after the play has been verified as high
			this.currentSuit = suit;
		} else {
			//not lead, check following suit, playing doubles/tractors/triples/whatever
			//compare to highest user's play
			//make sure the number of cards are correct
			
			
			

		}
		
		System.out.println("congratulations all tests passed");
	}
	
	public Trick calculateTrick(List<Card> played) {
		Trick trick = new Trick();
		//calculate stuffs
		Iterator<Card> it=played.iterator();
		Card previous=it.next();
		ArrayList<Card> previousCards=new ArrayList<Card>();
		previousCards.add(previous);
		Card twoPrevious=null;
		while(it.hasNext())
		{
			Card current=it.next();
			if(current==previous) //if they are equal add to list for possible pair/triple/tractor
			{
				previousCards.add(current);
				twoPrevious=previous;
				previous=current;
			}
			else if(current.getSuit()!=Card.TRUMP&&current.getNumber()!=TRUMP_NUMBER) //if not special case
			{
				if(current.getNumber()==previous.getNumber()+1) //if next card is one higher than previous card add
				{
					if(previousCards.size()==1) //if only one other card held then its just a single so get rid of the old card and add the new one
					{
						trick.addSingle(previous);
						previousCards.clear();
						previousCards.add(current);
						twoPrevious=null;
						previous=current;
					}
					else if(twoPrevious!=previous) //if the last two cards aren't equal then the there is no tractor possibility so remove previous
					{
						trick.addSingle(previous);
						previousCards.remove(previousCards.size()-1);
						if(previousCards.size()==1) //if there's only one other card then add it as a single
						{
							trick.addSingle(twoPrevious);
							previousCards.clear();
							twoPrevious=null;
							previous=current;
							
						}
						else if(previousCards.size()==2) //if there was two add as pair
						{
							trick.addPair(new Pair(twoPrevious));
							previousCards.clear();
							twoPrevious=null;
							previous=current;
						}
						else if(previousCards.size()==3) //if there are 3 left then its a triple
						{
							trick.addTriple(new Triple(twoPrevious));
							previousCards.clear();
							twoPrevious=null;
							previous=current;
						}
						else //otherwise if theres more its a tractor
						{
							//INCOMPLETE: I want to somehow recursion this part
							//but i dont know how since its basically doing the whole calc trick thing over again
							//to find the parameters of the tractor
							//trick.addTractor()
							
							//previousCards.clear();
							//twoPrevious=null;
							//previous=current;
						}
					}
				}
				else //no tractor/pairs/triples
				{
					trick.addSingle(current);
					if(previousCards.size()==1) //if there's only one other card then add it as a single
					{
						trick.addSingle(previous);
						previousCards.clear();
						previous=current;
						
					}
					else if(previousCards.size()==2) //if there was two add as pair
					{
						trick.addPair(new Pair(previous));
						previousCards.clear();
						previous=current;
					}
					else if(previousCards.size()==3) //if there are 3 left then its a triple
					{
						trick.addTriple(new Triple(previous));
						previousCards.clear();
						previous=current;
					}
					else //otherwise if theres more its a tractor
					{
						int pairCount=0;
						int tripleCount=0;
						Iterator<Card> it2=previousCards.iterator();
						Card first=it2.next();
						Card before=first;
						Card current2=first;
						while(it2.hasNext())
						{
							it2.next();
							current2=it2.next(); //skips one since we know its a tractor
							if(before==current2)     //if third card equals first then triple
							{
								tripleCount++;
								if(it2.hasNext())
								{
									before=it2.next();
								}
							}
							else				//third and first different
							{
								pairCount++;
								before=current2;
							}
						}
						trick.addTractor(new Tractor(pairCount, tripleCount, first.getNumber(), current2.getNumber()));
						previousCards.clear();
						twoPrevious=null;
						previous=current;
					}
					
				}
			}
			else //INCOMPLETE: idk how to check whether its one bigger (like small vs big or trumpnum versus goodtrumpnum
			{
				
			}
			
		}
		return trick;
	}
}

class Trick {
	private List<Card> singles;
	private List<Pair> pairs;
	private List<Triple> triples;
	private List<Tractor> tractors;
	Trick() {
		this.singles = new LinkedList<Card>();
		this.pairs = new LinkedList<Pair>();
		this.triples = new LinkedList<Triple>();
		this.tractors = new LinkedList<Tractor>();
	}
	
	public void addSingle(Card card) {
		this.singles.add(card);
	}
	
	public void addPair(Pair pair) {
		this.pairs.add(pair);
	}
	
	public void addTriple(Triple triple) {
		this.triples.add(triple);
	}
	
	public void addTractor(Tractor tractor) {
		this.tractors.add(tractor);
	}
	
	public int countSingles() {
		return this.singles.size();
	}
	
	public int countPairs() {
		return this.pairs.size();
	}
	
	public int countTriples() {
		return this.triples.size();
	}
	
	public int countTractors() {
		return this.tractors.size();
	}
	
	public int countPlays() {
		return this.countSingles() + this.countPairs() + this.countTriples() + this.countTractors();
	}
	
}
class Pair
{
	Card c;
	Pair(Card card)
	{
		this.c = card;
	}
	public int getSuit()
	{
		return c.getSuit();
	}
	public int getNumber()
	{
		return c.getNumber();
	}
}

class Triple extends Pair
{
	Triple(Card card) {
		super(card);
	}
}

class Tractor {
	int pairs;
	int triples;
	int start;
	int end;
	Tractor(int pairs, int triples, int start, int end) {
		this.pairs = pairs;
		this.triples = triples;
		this.start = start;
		this.end = end;
	}
}
