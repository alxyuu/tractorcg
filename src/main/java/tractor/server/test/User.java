package tractor.server.test;

import tractor.server.PlayerHand;

public class User {
	
	private PlayerHand hand;

	public PlayerHand getHand() {
		return this.hand;
	}

	public void newHand() {
		this.hand = new PlayerHand();
	}
}