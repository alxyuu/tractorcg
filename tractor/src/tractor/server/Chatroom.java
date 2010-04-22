package tractor.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Chatroom {
	private final String name;
	private Set<User> users;
	Chatroom(String name) {
		this.name = name;
		this.users = Collections.synchronizedSet(new HashSet<User>());
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
	public boolean contains(User user) {
		return this.users.contains(user);
	}
	public String toString() {
		return this.name;
	}
	public int getID() {
		return this.hashCode();
	}
}
