package tractor.server.test;

import tractor.lib.Card;

public class StringConvert {
	public static void main (String ... bobby) {
		String[] in = "three of spades, three of spades, four of spades, four of spades, five of spades, five of spades, five of spades, eight of spades, nine of spades, jack of spades, ace of spades, three of clubs, four of clubs, five of clubs, seven of clubs, seven of clubs, jack of clubs, queen of clubs, two of hearts, two of hearts, three of hearts, five of hearts, five of hearts, seven of hearts, seven of hearts, seven of hearts, jack of hearts, king of hearts, two of diamonds, four of diamonds, five of diamonds, seven of diamonds, nine of diamonds, queen of diamonds, king of diamonds, six of hearts, small joker of trump, small joker of trump, big joker of trump".split(", ");
		String out = "";
		for(String semi : in) {
			String[] card = semi.split(" ");
			String suit, num = "";
			if(card[2].equals("of")) {
				suit = "Card.TRUMP";
				if(card[0].equals("small")) {
					num = "Card.SMALL_JOKER";
				} else {
					num = "Card.BIG_JOKER";
				}
			} else {
				suit = "Card."+card[2].toUpperCase();
				num = "Card."+card[0].toUpperCase();
			}
			out += "played.add(Card.getCard("+suit+","+num+"));\n";
		}
		System.out.println(out);
	}
}
