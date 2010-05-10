package tractor.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import tractor.lib.MessageFactory;

public class Chatroom {
	private String name;
	private Set<User> users;
	
	Chatroom() {
		this.users = Collections.synchronizedSet(new HashSet<User>());
	}
	
	public Chatroom(String name) {
		this();
		this.setName(name);
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public void join(User user) {
		this.users.add(user);
		this.send(user, user.getName() + " has joined " + this.getName());
	}
	
	public void part(User user) {
		this.users.remove(user);
		this.send(user, user.getName() + " has left " + this.getName());
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
	
	public void send(User user, String message) {
		for(Iterator<User> i=this.users.iterator(); i.hasNext();) {
			User u = i.next();
			if(u!=user)
				u.getIO().write(this.getName()+"|"+message, MessageFactory.CHAT);
		}
	}
}
