package tractor.lib;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageFactory {

	static public final int GAMECMD = 2;
	static public final int KEEPALIVE = 0;
	static public final int LOGIN = 1;
	static public final int MESSAGE = 3;

	private LinkedBlockingQueue<String>[] in;
	private LinkedBlockingQueue<String> out;
	private long lastUpdate;
	private long timeout;

	@SuppressWarnings("unchecked")
	public MessageFactory(long timeout) {
		this.in = (LinkedBlockingQueue<String>[]) new LinkedBlockingQueue[4];
		for(int i=0; i<4; i++) {
			this.in[i] = new LinkedBlockingQueue<String>();
		}
		this.out = new LinkedBlockingQueue<String>();
		
		this.timeout = timeout;
		this.lastUpdate = System.currentTimeMillis();
	}
	
	public void clearMessageQueue(int type) {
		this.in[type].clear();
	}
	
	public int getMessageSize(int type) {
		return this.in[type].size();
	}

	public String getNextMessage(int type) {
		return this.in[type].poll();
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
	public void write(String message, int type) {
		this.out.offer(type+message);
	}
}
