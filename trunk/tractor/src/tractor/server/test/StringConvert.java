package tractor.server.test;

import tractor.lib.Card;

public class StringConvert {
	public static void main (String ... bobby) {
		String[] in = "three of spades, six of spades, nine of spades, ten of spades, ten of spades, jack of spades, queen of spades, queen of spades, king of spades, king of spades, king of spades, five of diamonds, seven of diamonds, eight of diamonds, nine of diamonds, ten of diamonds, jack of diamonds, queen of diamonds, king of diamonds, four of hearts, five of hearts, eight of hearts, nine of hearts, nine of hearts, ten of hearts, jack of hearts, queen of hearts, ace of hearts, three of clubs, four of clubs, seven of clubs, seven of clubs, jack of clubs, queen of clubs, king of clubs, king of clubs, ace of clubs, two of spades, two of hearts".split(", ");
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
