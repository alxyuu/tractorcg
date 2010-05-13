package tractor.client.handlers;

import tractor.client.Client;

public abstract class ClientHandler implements Runnable {

	protected IOFactory io;
	private String name;
	ClientHandler(String name) {
		this.io = Client.getInstance().getIO();
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	abstract public void run();

}
