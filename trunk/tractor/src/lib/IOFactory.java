package tractor.lib;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class IOFactory {

	static public final int GAMECMD = 2;
	static private int INSTANCES = 0;
	static public final int KEEPALIVE = 0;
	static public final int LOGIN = 1;

	static public final int MESSAGE = 3;

	private LinkedBlockingQueue<String>[] messages;
	private LinkedBlockingQueue<String> toWrite;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private ThreadGroup io;

	@SuppressWarnings("unchecked")
	public IOFactory(Socket s) {
		this.socket = s;
		this.messages = (LinkedBlockingQueue<String>[]) new LinkedBlockingQueue[4];
		for(int i=0; i<4; i++) {
			this.messages[i] = new LinkedBlockingQueue<String>();
		}
		this.toWrite = new LinkedBlockingQueue<String>();

		try {
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new PrintWriter(socket.getOutputStream(),false);
		} catch(IOException e){
			e.printStackTrace();
			return;
		}

		int instance = ++IOFactory.INSTANCES;

		this.io = new ThreadGroup("io-"+instance);

		Thread listener = new Thread(this.io,"listener-"+instance) {
			public void run() {
				System.out.println("waiting for incoming messages");
				while(socket.isConnected()) {
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

		Thread output = new Thread(this.io,"output-"+instance) {
			public void run() {
				System.out.println("output stream open");
				while(!out.checkError()) {
					if(hasNextWrite()) {
						while(hasNextWrite()) {
							String line = getNextWrite();
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
		Thread keepalive = new Thread(this.io,"keepalive-"+instance) {
			public void run() {
				while(!checkError()) {
					write("0", IOFactory.KEEPALIVE);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		};
		keepalive.start();

	}

	public boolean checkError() {
		if( this.socket == null || !this.socket.isConnected() || this.out == null || this.out.checkError() || this.in == null )
			return true;
		return false;
	}

	public void clearMessageQueue(int type) {
		this.messages[type].clear();
	}

	public int getMessageSize(int type) {
		return this.messages[type].size();
	}

	public String getNextMessage(int type) {
		return this.messages[type].poll();
	}

	public String getNextWrite() {
		return this.toWrite.poll();
	}

	public boolean hasNextMessage(int type) {
		return !this.messages[type].isEmpty();
	}

	public boolean hasNextWrite() {
		return !this.toWrite.isEmpty();
	}
	public void read(String message) throws ErroneousMessageException {
		try {
			int type = Integer.parseInt(message.substring(0,1));
			this.messages[type].add(message.substring(1,message.length()));
		} catch (NumberFormatException e) {
			throw new ErroneousMessageException("Message type not supplied");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ErroneousMessageException("Message type invalid");
		}
	}
	public void write(String message, int type) {
		this.toWrite.offer(type+message);
	}

	public void kill() {	
		this.io.interrupt();
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
	}
}
