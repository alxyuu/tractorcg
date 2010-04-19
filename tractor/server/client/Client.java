package tractor.client;

import java.io.IOException;
import java.net.Socket;
import tractor.lib.IOFactory;

class Client {
	
	public static void main(String ... boobie) {
		new Client("192.168.0.2",443,"bobby");
	}
	
	private IOFactory io;
	private final int MAX_TRIES = 5;
	private String md5;
	private boolean ready;
	private Socket socket;
	private String username;

	Client(String ip, int port, String username) {
		try {
			this.socket = new Socket(ip,port);
			this.socket.setSoTimeout(15000);
			this.socket.setKeepAlive(true);

			this.io = new IOFactory(this.socket);
			this.ready = false;
			this.username = username;

			if(this.login()) {

			} else {
				System.out.println("login failed");
				System.exit(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean login() {
		//get md5 from server
		while(!this.io.hasNextMessage(IOFactory.LOGIN)) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.md5 = this.io.getNextMessage(IOFactory.LOGIN);

		this.io.write(this.username,IOFactory.LOGIN);
		this.io.write(this.md5,IOFactory.LOGIN);


		int tries = 1;

		login: while(!ready && this.socket.isConnected() && tries <= MAX_TRIES) {
			if(this.io.hasNextMessage(IOFactory.LOGIN)) {
				char s = this.io.getNextMessage(IOFactory.LOGIN).charAt(0);
				switch(s) {
				case '1': 
					this.ready = true;
					return true;
				case '2':
					for(int i=0; i<tries; i++) {
						this.username += "_";
					}
					this.io.write(this.username,IOFactory.LOGIN);
					this.io.write(this.md5,IOFactory.LOGIN);

					tries++;
					continue login;
				case '3': return false;
				default: return false;
				}
				
			} else {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
