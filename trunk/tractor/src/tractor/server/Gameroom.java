package tractor.server;

public class Gameroom extends Chatroom {
	private int players;
	public Gameroom(int players) {
		super();
		this.players = players;
		this.setName("@"+this.hashCode());
	}
	public boolean join(User user) {
		if(this.getSize() < this.players) {
			return super.join(user);
		}
		return false;
		//error handler
	}
}
