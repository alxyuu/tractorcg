package tractor.server;

import java.util.HashSet;

public class Chatroom {
	private final String name;
	private HashSet<User> users;
	Chatroom(String name) {
		this.name = name;
		this.users = new HashSet<User>();
	}
	public void join(User user) {
		this.users.add(user);
	}
	public void part(User user) {
		this.users.remove(user);
	}
	public String getName() {
		return this.name;
	}
	public int getID() {
		return this.hashCode();
	}
}
