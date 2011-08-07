package tractor.server.test;

import tractor.lib.Card;

public class StringConvert {
	public static void main (String ... bobby) {
		String[] in = "five of spades, six of spades, eight of spades, eight of spades, nine of spades, ten of spades, king of spades, four of clubs, five of clubs, eight of clubs, nine of clubs, ten of clubs, ten of clubs, queen of clubs, king of clubs, king of clubs, three of diamonds, three of diamonds, four of diamonds, six of diamonds, seven of diamonds, seven of diamonds, eight of diamonds, eight of diamonds, jack of diamonds, four of hearts, seven of hearts, eight of hearts, eight of hearts, nine of hearts, jack of hearts, jack of hearts, jack of hearts, queen of hearts, queen of hearts, two of spades, two of spades, two of clubs, small joker of trump".split(", ");
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
