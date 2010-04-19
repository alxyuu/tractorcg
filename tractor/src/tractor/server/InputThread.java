package tractor.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import tractor.lib.ErroneousMessageException;

public class InputThread extends Thread {
	private ConcurrentHashMap<User,BufferedReader> users;
	private final int limit;

	InputThread(ThreadGroup g, String n, int limit) {
		super(g,n);
		this.limit = limit;
		this.users = new ConcurrentHashMap<User,BufferedReader>(this.limit);
	}

	public void run() {
		//TODO: flood detection/prevention
		System.out.println("listening with "+this.getName());
		while(!users.isEmpty()) {
			for(Iterator<Map.Entry<User,BufferedReader>> i = this.users.entrySet().iterator(); i.hasNext();) {
				Map.Entry<User,BufferedReader> entry = i.next();
				User user = entry.getKey();
				BufferedReader in = entry.getValue();
				try {
					if(user.checkError()) {
						user.setError();
						i.remove();
						System.out.println(user+" input stream closed");
						continue;
					}
					while(in.ready()) {
						String line = in.readLine();
						user.getIO().read(line);
						if(!line.equals("00")) System.out.println("input: "+line+"-end-");
					} 
				} catch (IOException e) {
					e.printStackTrace();
					user.setError();
				} catch (ErroneousMessageException e) {
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(250);
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
		this.users.put(user,in);
	}
}
