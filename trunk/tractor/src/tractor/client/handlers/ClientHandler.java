package tractor.client.handlers;

import tractor.client.Client;

public abstract class ClientHandler implements Runnable {

	protected IOFactory io;
	ClientHandler() {
		this.io = Client.getInstance().getIO();
	}
	
	abstract public void run();

}
