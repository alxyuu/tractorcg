package tractor.server.handlers;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tractor.lib.MessageFactory;
import tractor.server.User;



public class OutputThread extends Thread {
	private ConcurrentHashMap<User,PrintWriter> users;
	private final int limit;

	/** It constructs the output thread with the given parameters
	 * @param g
	 * @param n
	 * @param limit
	 */
	OutputThread(ThreadGroup g, String n, int limit) {
		super(g,n);
		this.limit = limit;
		this.users = new ConcurrentHashMap<User,PrintWriter>(this.limit);
	}

	public void run() {
		System.out.println("writing with "+this.getName());
		while(!users.isEmpty()) {
			for(Iterator<Map.Entry<User,PrintWriter>> i = this.users.entrySet().iterator(); i.hasNext();) {
				Map.Entry<User,PrintWriter> entry = i.next();
				User user = entry.getKey();
				PrintWriter out = entry.getValue();
				if(out.checkError() || user.checkError()) {
					user.setError();
					i.remove();
					System.out.println(user+" output stream closed");
					continue;
				}
				if(user.getIO().hasNextWrite()) {
					do {
						String line = user.getIO().getNextWrite();
						out.println(line);
						out.flush();
					} while(user.getIO().hasNextWrite());
				} else if(user.getIO().writeTimeout()) {
					user.getIO().write("0", MessageFactory.KEEPALIVE);
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

	/** It checks if the output thread is full.
	 * @return
	 *
	 */
	public boolean isFull() {
		return this.users.size() == this.limit;
	}

	/** It adds the user to the output thread.
	 * @param user
	 * @throws IOException
	 *
	 */
	public void add(User user) throws IOException {
		PrintWriter out = new PrintWriter(user.getSocket().getOutputStream(),false);
		this.users.put(user,out);
	}
}
