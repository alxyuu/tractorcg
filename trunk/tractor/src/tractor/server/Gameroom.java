package tractor.server;

public class Gameroom extends Chatroom {
	private int players;
	Gameroom(int players) {
		super();
		this.players = players;
		this.setName("@"+this.hashCode());
	}
	public void join(User user) {
		if(this.getSize() < this.players)
			super.join(user);
		//error handler
	}
}
