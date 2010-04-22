package tractor.server.handlers;

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
				for(int i = 0; i < waiting.size(); i++) {
					User user = waiting.get(i);
					if(user.checkError()) {
						user.kill();
						waiting.remove(i);
						System.out.println(user.toString()+" - connection closed");
						i--;
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				//do nothing
			}
		}
	}
}
