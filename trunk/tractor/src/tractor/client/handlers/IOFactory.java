package tractor.client.handlers; //move to tractor.client?

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import tractor.lib.MessageFactory;

//messagefactory with built in io handling, should only be used by client
public class IOFactory extends MessageFactory {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private ThreadGroup iogroup;

	/**Constructs.
	 * @param timeout
	 * 
	 */
	public IOFactory(long timeout) {
		super(timeout);
		iogroup = new ThreadGroup("IOFactory");
	}

	/**It initializes the IO.
	 * @param s
	 * @throws IOException
	 * 
	 */
	public void initIO(Socket s) throws IOException {
		this.socket = s;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(),false);

		this.addHandler(new InputHandler(in));
		this.addHandler(new OutputHandler(out));
		this.addHandler(new CommandHandler());
		this.addHandler(new ChatHandler());
	}

	private void addHandler(ClientHandler handler) {
		Thread run = new Thread( iogroup, handler, handler.getName() );
		run.start();
	}
	public boolean isAlive() {
		return this.socket != null && this.socket.isConnected() && super.isAlive();
	}
	/**It kills the factory.
	 * 
	 */
	public void kill() {
		Thread[] threads = new Thread[this.iogroup.activeCount()];
		this.iogroup.interrupt();
		this.iogroup.enumerate(threads);
		System.out.println("waiting for threads to die");
		for(int i=0;i<threads.length;i++) {
			try {
				threads[i].join();
			} catch (Exception e) {/*interrupted or nullpointer, shouldn't matter*/}
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
	public void reset() {
		this.kill();
		super.reset();
	}
}
