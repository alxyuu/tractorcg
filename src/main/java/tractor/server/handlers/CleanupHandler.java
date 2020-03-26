package tractor.server.handlers;

import java.util.Iterator;

import tractor.server.User;

public class CleanupHandler extends ServerHandler {
	public void run() {
		System.out.println("waiting on dead connections");
		while(true) {
			try {
				for(String name : users.keySet()) {
					User user = users.get(name);
					if(user.checkError()) {
						user.kill();
						users.remove(name);
						System.out.println(user.toString()+" - connection closed");
					}
				}
				//for(int i = 0; i < waiting.size(); i++) {
				
				synchronized(waiting) {
					for(Iterator<User> i = waiting.iterator(); i.hasNext();) {
						User user = i.next();
						if(user.checkError()) {
							user.kill();
							i.remove();
							System.out.println(user.toString()+" - connection closed");
						}
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace(System.out);
				break;
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace(System.out);
				//do nothing
			}
		}
	}
}
