package tractor.lib;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageFactory {

	static public final int GAMECMD = 2;
	static public final int KEEPALIVE = 0;
	static public final int LOGIN = 1;
	static public final int MESSAGE = 3;

	private LinkedBlockingQueue<String>[] in;
	private LinkedBlockingQueue<String> out;

	@SuppressWarnings("unchecked")
	public MessageFactory() {
		this.socket = s;
		this.in = (LinkedBlockingQueue<String>[]) new LinkedBlockingQueue[4];
		for(int i=0; i<4; i++) {
			this.in[i] = new LinkedBlockingQueue<String>();
		}
		this.out = new LinkedBlockingQueue<String>();

	}

	public boolean checkError() {
		if( this.socket == null || !this.socket.isConnected() || this.out == null || this.out.checkError() || this.in == null )
			return true;
		return false;
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
	public void read(String message) throws ErroneousMessageException {
		try {
			int type = Integer.parseInt(message.substring(0,1));
			this.in[type].add(message.substring(1,message.length()));
		} catch (NumberFormatException e) {
			throw new ErroneousMessageException("Message type not supplied");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ErroneousMessageException("Message type invalid");
		}
	}
	public void write(String message, int type) {
		this.out.offer(type+message);
	}
}
