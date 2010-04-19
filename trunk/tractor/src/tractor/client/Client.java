package tractor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import tractor.lib.ErroneousMessageException;
import tractor.lib.MessageFactory;

class Client {

	public static void main(String ... boobie) {
		new Client("192.168.0.2",443,"bobby");
	}

	private MessageFactory io;
	private final int MAX_TRIES = 5;
	private String md5;
	private boolean ready;
	private Socket socket;
	private String username;
	private BufferedReader in;
	private PrintWriter out;

	Client(String ip, int port, String username) {

		try {
			this.socket = new Socket(ip,port);
			this.socket.setSoTimeout(15000);
			this.socket.setKeepAlive(true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(),false);
		} catch(IOException e){
			e.printStackTrace();
			return;
		}

		this.io = new MessageFactory(15000);
		this.ready = false;
		this.username = username;



		Thread listener = new Thread("listener") {
			public void run() {
				System.out.println("waiting for incoming messages");
				while(socket.isConnected()) {
					try {
						if(in.ready()) {
							String line = in.readLine();
							io.read(line);
							if(!line.equals("00")) System.out.println("input: "+line+"-end-");
						} else {
							Thread.sleep(250);
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
		};
		listener.start();

		Thread output = new Thread("output") {
			public void run() {
				System.out.println("output stream open");
				while(!out.checkError()) {
					if(io.hasNextWrite()) {
						while(io.hasNextWrite()) {
							String line = io.getNextWrite();
							if(!line.equals("00")) System.out.println("output: "+line+"-end-");
							out.println(line);
							out.flush();
						}
					} else {
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							return;
						}
					}
				}
				out.close();
				out = null;
			}
		};
		output.start();

		//TODO: merge with input
		Thread keepalive = new Thread("keepalive") {
			public void run() {
				while(true) {
					io.write("0", MessageFactory.KEEPALIVE);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		};
		keepalive.setDaemon(true);
		keepalive.start();

		if(this.login()) {

		} else {
			System.out.println("login failed");
			System.exit(0);
		}

	}

	private boolean login() {
		//get md5 from server
		while(!this.io.hasNextMessage(MessageFactory.LOGIN)) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.md5 = this.io.getNextMessage(MessageFactory.LOGIN);

		this.io.write(this.username,MessageFactory.LOGIN);
		this.io.write(this.md5,MessageFactory.LOGIN);


		int tries = 1;

		login: while(!ready && this.socket.isConnected() && tries <= MAX_TRIES) {
			if(this.io.hasNextMessage(MessageFactory.LOGIN)) {
				char s = this.io.getNextMessage(MessageFactory.LOGIN).charAt(0);
				switch(s) {
				case '1': 
					this.ready = true;
					return true;
				case '2':
					for(int i=0; i<tries; i++) {
						this.username += "_";
					}
					this.io.write(this.username,MessageFactory.LOGIN);
					this.io.write(this.md5,MessageFactory.LOGIN);

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
