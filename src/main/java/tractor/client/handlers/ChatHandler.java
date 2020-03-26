package tractor.client.handlers;

import tractor.client.ChatPane;
import tractor.client.ClientView;

public class ChatHandler extends ClientHandler {

	ChatHandler() {
		super("ChatHandler");
	}

	public void run() {
		while(true) {
			if(io.hasNextMessage(IOFactory.CHAT)){
				String msg = io.getNextMessage(IOFactory.CHAT);
				int index = msg.indexOf("|");
				String room = msg.substring(0,index).trim();
				ChatPane chat = ClientView.getInstance().getChatroom(room);
				if( chat != null)
					chat.append(msg.substring(index+1)); //invoke later?
				else
					System.out.println("NOTICE: "+room+" not joined");

			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

}
