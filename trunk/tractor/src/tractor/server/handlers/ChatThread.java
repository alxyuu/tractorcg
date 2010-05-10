package tractor.server.handlers;

import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import tractor.lib.MessageFactory;
import tractor.server.Chatroom;
import tractor.server.Server;
import tractor.server.User;

public class ChatThread extends Thread {

	private final int limit;
	private Vector<User> users;
	private ConcurrentHashMap<String, Chatroom> chatrooms;

	ChatThread(ThreadGroup g, String n, int limit) {
		super(g,n);
		this.limit = limit;
		this.users = new Vector<User>();
		this.chatrooms = Server.getInstance().getChatrooms();
	}

	public void run() {
		//TODO: LOTS OF OPTIMIZATION
		System.out.println("handling chat with "+this.getName());
		while(!users.isEmpty()) {
			for(Iterator<User> i = users.iterator();i.hasNext();) {
				User user = i.next();
				if(user.checkError()) {
					i.remove();
					System.out.println(user+" chat handler closed");
					continue;
				}
				MessageFactory io = user.getIO();
				while(io.hasNextMessage(MessageFactory.CHAT)){
					String msg = io.getNextMessage(MessageFactory.CHAT);
					int index = msg.indexOf("|");
					String id = msg.substring(0,index);
					Chatroom chat = this.chatrooms.get(id);
					if(chat == null) {
						System.out.println("exception in \""+this.getName()+"\": chatroom "+id+" not found");
						continue;
					}
					if(!chat.contains(user)) {
						System.out.println("security exception in \""+this.getName()+"\": "+user+" is not joined to "+this.chatrooms.get(id));
						continue;
					}
					chat.send(user, user.getName()+"> "+msg.substring(index+1));
				}
			}
			try {
				//dynamic sleeping?
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(this.getName()+" no longer serving users");
	}
	
	/** It checks if the chatroom is full based on the capacity.
	 * @return
	 *
	 */
	public boolean isFull() {
		return this.users.size() == this.limit;
	}
	
	/** It adds a user to the chat thread.
	 * @param user
	 *
	 */
	public void add(User user) {
		this.users.add(user);
	}
}
