package tractor.server.handlers;

import tractor.lib.MessageFactory;
import tractor.server.User;

public class LoginHandler extends ServerHandler {
	public void run() {
		System.out.println("waiting for logins");
		while(true) {
			try {
				if(!waiting.isEmpty()) {
					User user = waiting.remove(0);
					MessageFactory io = user.getIO();
					if(io.getMessageSize(MessageFactory.LOGIN) >= 2) {
						String name = io.getNextMessage(MessageFactory.LOGIN);
						if(io.getNextMessage(MessageFactory.LOGIN).equals(user.getMD5())) {
							if(!users.containsKey(name.toUpperCase())) {
								user.setName(name);
								io.write("1",MessageFactory.LOGIN);
								users.put(name.toUpperCase(),user);
							} else {
								io.write("2",MessageFactory.LOGIN);
								io.clearMessageQueue(MessageFactory.LOGIN);
								waiting.add(user);
							}
						} else {
							io.write("3",MessageFactory.LOGIN);
							System.out.println("Auth Failure: MD5 mismatch from "+user.toString()+", booting");
							io.flush(); // wait until user gets the message
							// maybe fork so other users don't have to wait
							user.kill();
							user = null;
						}
					} else {
						if(!waiting.isEmpty()) {
							waiting.add(1,user);
						} else {
							waiting.add(user);
							Thread.sleep(500);
						}
					}
				} else {
					//wait for half a second if we're not doing anything...
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
