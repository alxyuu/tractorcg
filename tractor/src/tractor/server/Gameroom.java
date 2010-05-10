package tractor.server;

public class Gameroom extends Chatroom {
	Gameroom() {
		super();
		this.setName("@"+this.hashCode());
	}
}
