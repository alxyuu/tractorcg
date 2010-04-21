package tractor.lib; //move to tractor.client?

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//messagefactory with built in io handling, should only be used by client
public class IOFactory extends MessageFactory {
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private ThreadGroup iogroup;
	
	public IOFactory(long timeout) {
		super(timeout);
		iogroup = new ThreadGroup("IOFactory");
	}
	
	public void initIO(Socket s) throws IOException {
		this.socket = s;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(),false);
		
		Thread listener = new Thread(iogroup,"listener") {
			public void run() {
				System.out.println("waiting for incoming messages");
				while(socket.isConnected() && isAlive()) { //need both?
					try {
						if(in.ready()) {
							String line = in.readLine();
							read(line);
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

		Thread output = new Thread(iogroup,"output") {
			public void run() {
				System.out.println("output stream open");
				while(!out.checkError() && isAlive()) { //need both?
					if(hasNextWrite()) {
						do {
							String line = getNextWrite();
							if(!line.equals("00")) System.out.println("output: "+line+"-end-");
							out.println(line);
							out.flush();
						} while(hasNextWrite());
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
		Thread keepalive = new Thread(iogroup,"keepalive") {
			public void run() {
				while(true) {
					write("0", MessageFactory.KEEPALIVE);
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
	}
	
	public void reset() {
		
		super.reset();
		
		Thread[] threads = new Thread[this.iogroup.activeCount()];
		this.iogroup.interrupt();
		this.iogroup.enumerate(threads);
		System.out.println("waiting for threads to die");
		for(int i=0;i<threads.length;i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {}
		}
		System.out.println("threads dead");
		
		try {
			if(this.socket != null) {
				this.socket.close();
				this.socket = null;
			}
		}
		catch (IOException e) { this.socket = null; }
		try {
			if(this.in != null) {
				this.in.close();
				this.in = null;
			}
		}
		catch (IOException e) { this.in = null; }
		if(this.out != null) {
			this.out.close();
			this.out = null;
		}
		
		//clean up messagefactory
	}
}
