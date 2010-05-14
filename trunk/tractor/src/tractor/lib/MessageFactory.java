package tractor.lib;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageFactory {

	static public final int CHAT = 3;
	static public final int CHATCMD = 4;
	static public final int GAMECMD = 2;
	static public final int KEEPALIVE = 0;
	static public final int LOGIN = 1;

	private ConcurrentLinkedQueue<String>[] in;
	private ConcurrentLinkedQueue<String> out;
	private long timeout;
	private long keepalive;
	/*
	 * Last outgoing message
	 */
	private long lastPing;
	/*
	 * Last incoming message
	 */
	private long lastPong;

	/**Constructs the message factory.
	 * @param timeout
	 */
	@SuppressWarnings("unchecked")
	public MessageFactory(long timeout) {
		this.in = (ConcurrentLinkedQueue<String>[]) new ConcurrentLinkedQueue[5];
		for(int i=0; i<this.in.length; i++) {
			this.in[i] = new ConcurrentLinkedQueue<String>();
		}
		this.out = new ConcurrentLinkedQueue<String>();
		
		this.timeout = timeout;
		this.keepalive = timeout/3;
		this.lastPong = System.currentTimeMillis();
		this.lastPing = System.currentTimeMillis();
	}
	
	/** It clears the message queue.
	 * @param type
	 */
	public void clearMessageQueue(int type) {
		this.in[type].clear();
	}
	
	/** It waits until the write queue finishes writing.
	 * 
	 */
	public void flush() {
		while(this.hasNextWrite()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}
	/** Returns the size of the message
	 * @param type
	 * @return
	 */
	public int getMessageSize(int type) {
		return this.in[type].size();
	}

	/** It returns the next message
	 * @param type
	 * @return
	 */
	public String getNextMessage(int type) {
		return this.in[type].poll();
	}

	/** It returns the next message with type and blocking parameters
	 * @param type
	 * @param blocking
	 * @return
	 */
	public String getNextMessage(int type, boolean blocking) {
		if(blocking) {
			while(!this.hasNextMessage(type)) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					return null;
				}
			}
		}
		return this.getNextMessage(type);
	}

	/** It returns what is being written next.
	 * @return
	 */
	public String getNextWrite() {
		return this.out.poll();
	}

	/** It checks whether there is another message.
	 * @param type
	 * @return
	 */
	public boolean hasNextMessage(int type) {
		return !this.in[type].isEmpty();
	}

	/** It checks whether there is another message to be written.
	 * @return
	 */
	public boolean hasNextWrite() {
		return !this.out.isEmpty();
	}

	/** It checks whether the message factory is alive.
	 * @return
	 */
	public boolean isAlive() {
		return System.currentTimeMillis()-lastPong < timeout;
	}
	
	/** It reads the message.
	 * @param message
	 * @throws ErroneousMessageException
	 */
	public void read(String message) throws ErroneousMessageException {
		try {
			int type = Integer.parseInt(message.substring(0,1));
			this.renewPong();
			if(type == MessageFactory.KEEPALIVE) return;
			this.in[type].add(message.substring(1,message.length()));
			System.out.println("input: "+message+"-end-");
		} catch (NumberFormatException e) {
			throw new ErroneousMessageException("Message type not supplied");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ErroneousMessageException("Message type invalid");
		}
	}
	
	/** It updates the message.
	 * 
	 */
	public void renewPong() {
		this.lastPong = System.currentTimeMillis();
	}
	
	public void renewPing() {
		this.lastPing = System.currentTimeMillis();
	}
	
	/** It resets the message factory.
	 * 
	 */
	public void reset() {
		for(int i=0;i<this.in.length;i++) {
			this.in[i].clear();
		}
		this.out.clear();
		this.renewPong();
		this.renewPing();
	}
	
	/** It writes the message.
	 * @param message
	 * @param type
	 */
	public void write(String message, int type) {
		this.out.offer(type+message);
		this.renewPing();
		if( type != MessageFactory.KEEPALIVE ) System.out.println("output: "+message+"-end-");
	}
	
	public boolean writeTimeout() {
		return System.currentTimeMillis() - lastPing > keepalive;
	}
}
