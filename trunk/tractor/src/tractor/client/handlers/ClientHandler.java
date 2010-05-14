package tractor.client.handlers;

import tractor.client.Client;

public abstract class ClientHandler implements Runnable {

	protected IOFactory io;
	protected Client client;
	private String name;
	ClientHandler(String name) {
		this.client = Client.getInstance();
		this.io = client.getIO();
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	abstract public void run();

}
