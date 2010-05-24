package tractor.server;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import tractor.lib.MessageFactory;

public class Chatroom {
	private String name;
	protected ConcurrentLinkedQueue<User> users;

	Chatroom() {
		this.users = new ConcurrentLinkedQueue<User>();
	}
	
	/**Constructs the chatroom class and initializes the attributes.
	 * @param name
	 * 
	 */
	public Chatroom(String name) {
		this();
		this.setName(name);
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	protected ConcurrentLinkedQueue<User> getUsers() {
		return this.users;
	}
	
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
		this.users.add(user);
		this.send(user, user.getName() + " has joined " + this.getName());
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
