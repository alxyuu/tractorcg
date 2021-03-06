package tractor.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import tractor.lib.ErroneousMessageException;
import tractor.server.User;

public class InputThread extends Thread {
	private ConcurrentHashMap<User,BufferedReader> users;
	private final int limit;

	/** It constructs the input thread.
	 * @param g
	 * @param n
	 * @param limit
	 */
	InputThread(ThreadGroup g, String n, int limit) {
		super(g,n);
		this.limit = limit;
		this.users = new ConcurrentHashMap<User,BufferedReader>(this.limit);
	}

	public void run() {
		/* INHERENT DESIGN FLAW:
		 * server will not handle messages as it receives them, user are not synchronized
		 */
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
						System.out.println("IN: " + line);
						user.getIO().read(line);
					}
				} catch (IOException e) {
					e.printStackTrace(System.out);
					user.setError();
				} catch (ErroneousMessageException e) {
					e.printStackTrace(System.out);
				}
			}

			try {
				//dynamic sleeping?
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace(System.out);
			}
		}
		System.out.println(this.getName()+" no longer serving users");
	}

	/** It checks if the input thread is full.
	 * @return
	 *
	 */
	public boolean isFull() {
		return this.users.size() == this.limit;
	}

	/** It adds a user to the input thread.
	 * @param user
	 * @throws IOException
	 *
	 */
	public void add(User user) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
		this.users.put(user,in);
	}
}
