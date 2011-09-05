package tractor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import tractor.lib.MessageFactory;

public class Chatroom {
	private String name;
	protected List<User> users;

	Chatroom() {
		this.users = Collections.synchronizedList(new ArrayList<User>());
	}

	/**Constructs the chatroom class and initializes the attributes.
	 * @param name
	 * 
	 */
	public Chatroom(String name) {
		this();
		this.setName(name);
	}

	/** It sets the name of the chatroom.
	 * @param name
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/** It gets the list of users in the chatroom.
	 * @return
	 */
	protected List<User> getUsers() {
		return this.users;
	}

	/** It gets the number of users in the chatroom.
	 * @return
	 */
	public int getSize() {
		return this.users.size();
	}

	/**It joins the user to the chatroom
	 * @param user
	 * 
	 */
	public boolean join(User user) {
		if(this.users.contains(user))
			return false;
		
		StringBuilder msg = new StringBuilder("LIST ");
		msg.append(name);
		for(User u : this.users) {
			msg.append(" ");
			msg.append(u.getName());
		}
		
		this.users.add(user);
		this.send(user, user.getName() + " has joined " + this.getName());
		
		user.getIO().write(msg.toString(),MessageFactory.CHATCMD);
		
		return true;
	}

	/**It removes the user from the chatroom
	 * @param user
	 * 
	 */
	public void part(User user) {
		this.users.remove(user);
		this.send(user, user.getName() + " has left " + this.getName());
	}

	/**It returns the name of the chatroom
	 * @return
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**It checks if the user is in the chatroom
	 * @param user
	 * @return
	 * 
	 */
	public boolean contains(User user) {
		return this.users.contains(user);
	}

	public String toString() {
		return this.name;
	}

	/**it gets the ID of the chatroom
	 * @return
	 * 
	 */
	public int getID() {
		return this.hashCode();
	}

	/**It sends the message to all users in the chatroom
	 * @param user
	 * @param message
	 * 
	 */
	public void send(User user, String message) {
		for(Iterator<User> i=this.users.iterator(); i.hasNext();) {
			User u = i.next();
			if(u!=user)
				u.getIO().write(this.getName()+"|"+message, MessageFactory.CHAT);
		}
	}
}
