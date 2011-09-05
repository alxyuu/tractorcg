package tractor.server.test;

import tractor.lib.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class listtest {
	public static void main(String[] args) {
		/*ArrayList<Card> bob = new ArrayList<Card>();
		Card.populateDeck();
		bob.add(Card.getCard(Card.SPADES, Card.ACE));
		bob.add(Card.getCard(Card.DIAMONDS, Card.TEN));
		bob.add(null);
		Iterator<Card> it = bob.iterator();
		
		while(it.hasNext()) {
			System.out.println(it.next());
		}*/
		System.out.println(Arrays.toString("hi,lol".split(",",2)));
		System.out.println(Arrays.toString("hi,".split(",",2)));
	}
}
