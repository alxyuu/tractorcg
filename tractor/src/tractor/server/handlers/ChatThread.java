package tractor.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import tractor.lib.ErroneousMessageException;
import tractor.lib.MessageFactory;
import tractor.server.Chatroom;
import tractor.server.Server;
import tractor.server.User;

public class ChatThread extends Thread {

	private final int limit;
	private Vector<User> users;
	private ConcurrentHashMap<Integer, Chatroom> chatrooms;

	ChatThread(ThreadGroup g, String n, int limit) {
		super(g,n);
		this.limit = limit;
		this.users = new Vector<User>();
		this.chatrooms = Server.getInstance().getChatrooms();
	}

	public void run() {
		System.out.println("handling chat with "+this.getName());
		while(!users.isEmpty()) {
			for(Iterator<User> i = users.iterator();i.hasNext();) {
				User user = i.next();
				MessageFactory io = user.getIO();
				while(io.hasNextMessage(MessageFactory.CHAT)){
					String msg = io.getNextMessage(MessageFactory.CHAT);
					int index = msg.indexOf("|");
					int id = -1;
					try {
						id = Integer.parseInt(msg.substring(0,index));
					} catch (Exception e) { //both negative index and number format
						System.out.println("exception in \""+this.getName()+"\": no chatroom id found");
						continue;
					}
					if(!this.chatrooms.contains(id)) {
						System.out.println("exception in \""+this.getName()+"\": chatroom "+id+" not found");
						continue;
					}
					if(!this.chatrooms.get(id).contains(user)) {
						System.out.println("security exception in \""+this.getName()+"\": "+user+" is not joined to "+this.chatrooms.get(id));
						continue;
					}
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
	
	public boolean isFull() {
		return this.users.size() == this.limit;
	}
	
	public void add(User user) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
		this.users.add(user);
	}
}
