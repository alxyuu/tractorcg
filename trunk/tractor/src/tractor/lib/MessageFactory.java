package tractor.lib;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageFactory {

	static public final int CHAT = 3;
	static public final int CHATCMD = 4;
	static public final int GAMECMD = 2;
	static public final int KEEPALIVE = 0;
	static public final int LOGIN = 1;

	private LinkedBlockingQueue<String>[] in;
	private long lastUpdate;
	private LinkedBlockingQueue<String> out;
	private long timeout;

	@SuppressWarnings("unchecked")
	public MessageFactory(long timeout) {
		this.in = (LinkedBlockingQueue<String>[]) new LinkedBlockingQueue[5];
		for(int i=0; i<this.in.length; i++) {
			this.in[i] = new LinkedBlockingQueue<String>();
		}
		this.out = new LinkedBlockingQueue<String>();
		
		this.timeout = timeout;
		this.lastUpdate = System.currentTimeMillis();
	}
	
	public void clearMessageQueue(int type) {
		this.in[type].clear();
	}
	
	public void flush() {
		while(this.hasNextWrite()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}
	public int getMessageSize(int type) {
		return this.in[type].size();
	}

	public String getNextMessage(int type) {
		return this.in[type].poll();
	}

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

	public String getNextWrite() {
		return this.out.poll();
	}

	public boolean hasNextMessage(int type) {
		return !this.in[type].isEmpty();
	}

	public boolean hasNextWrite() {
		return !this.out.isEmpty();
	}

	public boolean isAlive() {
		return System.currentTimeMillis()-lastUpdate < timeout;
	}
	
	public void read(String message) throws ErroneousMessageException {
		try {
			int type = Integer.parseInt(message.substring(0,1));
			this.in[type].add(message.substring(1,message.length()));
			this.renew();
		} catch (NumberFormatException e) {
			throw new ErroneousMessageException("Message type not supplied");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ErroneousMessageException("Message type invalid");
		}
	}
	
	public void renew() {
		this.lastUpdate = System.currentTimeMillis();
	}
	
	public void reset() {
		for(int i=0;i<this.in.length;i++) {
			this.in[i].clear();
		}
		this.out.clear();
		this.renew();
	}
	
	public void write(String message, int type) {
		this.out.offer(type+message);
	}
}
