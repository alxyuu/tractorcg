package tractor.client.handlers;

import java.io.BufferedReader;
import java.io.IOException;

import tractor.lib.ErroneousMessageException;

public class InputHandler extends ClientHandler {

	BufferedReader in;
	InputHandler(BufferedReader in) {
		super("InputHandler");
		this.in = in;
	}
	public void run() {
		System.out.println("waiting for incoming messages");
		while(io.isAlive()) {
			try {
				if(in.ready()) {
					String line = in.readLine();
					io.read(line);
					if(!line.equals("00")) System.out.println("input: "+line+"-end-");
				} else {
					//dynamic sleeping?
					Thread.sleep(100);
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (ErroneousMessageException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				return;
			}

		}	
	}

}
