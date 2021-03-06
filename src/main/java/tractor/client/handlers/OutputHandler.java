package tractor.client.handlers;

import java.io.PrintWriter;

import tractor.lib.MessageFactory;

public class OutputHandler extends ClientHandler {

	PrintWriter out;
	/** It constructs the output handler.
	 * @param out
	 */
	OutputHandler(PrintWriter out) {
		super("OutputHandler");
		this.out = out;
	}

	public void run() {
		System.out.println("output stream open");
		while(!out.checkError() && io.isAlive()) { //need both?
			if(io.hasNextWrite()) {
				do {
					String line = io.getNextWrite();
					System.out.println("OUT: " + line);
					out.println(line);
					out.flush();
				} while(io.hasNextWrite());
			} else if (io.writeTimeout()) {
				io.write("0", MessageFactory.KEEPALIVE);
			} else {
				try {
					//dynamic sleeping?
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
		out.close();
		out = null;
	}
}
