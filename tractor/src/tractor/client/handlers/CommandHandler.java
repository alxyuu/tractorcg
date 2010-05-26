package tractor.client.handlers;

import tractor.client.ClientView;
import tractor.client.game.TractorGame;
import tractor.lib.ChatCommand;
import tractor.lib.GameCommand;

public class CommandHandler extends ClientHandler {
	/** It constructs the command handler
	 * 
	 */
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
					String[] args = command.split(" "); //[0] = game room name, [1] = user position, [2] = game size
					int position, size;
					try {
						position = Integer.parseInt(args[1]);
						size = Integer.parseInt(args[2]);
					} catch (Exception e) { //numberformat or arrayindexoutofbounds
						//TODO: invalid arguments
						e.printStackTrace();
						break;
					}
					ClientView.getInstance().join(args[0]);
					io.write(GameCommand.JOIN+" "+args[0],IOFactory.GAMECMD);
					client.setGame(new TractorGame(position, size, args[0]));
					//client.setGame(new TractorGame(position, 3));
					client.startGame();
					break;
				case G_PART:
					ClientView.getInstance().part(command);
					break;
				default:
					//TODO: some error handler
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
