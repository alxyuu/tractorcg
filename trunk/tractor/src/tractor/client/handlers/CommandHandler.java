package tractor.client.handlers;

import tractor.client.ClientView;
import tractor.lib.ChatCommand;

public class CommandHandler extends ClientHandler {
	CommandHandler() {
		super("CommandHandler");
	}

	public void run() {
		while(true) {
			if(io.hasNextMessage(IOFactory.CHATCMD)) {
				String cmd = io.getNextMessage(IOFactory.CHATCMD);
				int index = cmd.indexOf(" ");
				String command;
				if(index == -1) {
					index = cmd.length();
					command = "";
				} else {
					command = cmd.substring(index+1).trim();
				}
				switch (ChatCommand.get(cmd.substring(0,index))) {
				case C_JOIN:
					ClientView.getInstance().join(command);
					break;
				case C_PART:
					ClientView.getInstance().part(command);
					//do nothing?
					break;
				case S_QUIT:
					//disconnect
					break;
				case G_HOOK:
					String[] args = command.split(" "); //[1] = user place
					ClientView.getInstance().join(args[0]);
					break;
				case G_PART:
					ClientView.getInstance().part(command);
					break;
				default:
					//some error handler
				}
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