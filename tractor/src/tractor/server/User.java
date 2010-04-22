package tractor.server;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;
import tractor.lib.MessageFactory;

public class User {
	
	private static MessageDigest md;
	private static String md5salt;
	static {
		try {
			md5salt = "HI LOL";
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			//this should never be reached..?
			System.out.println("FATAL ERROR: failed to find md5 algorithm");
			System.exit(0);
		}
	}
	
	private MessageFactory io;
	private String md5;
	public Queue<String> messages;
	private String name;
	private boolean error;
	private Socket socket; 
	
	public User(Socket socket) {
		try {
			this.socket = socket;
			
			byte[] digest = md.digest((md5salt+this.socket.toString()).getBytes()); 
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<digest.length;i++) {
				hexString.append(Integer.toHexString(0xFF & digest[i]));
			}
			this.md5 = hexString.toString();
			hexString.setLength(0);
			//this.md5 = new String(md.digest((md5salt+this.socket.toString()).getBytes()));
			
			this.name = "addr=" + this.socket.getInetAddress().toString() + ":" + this.socket.getPort();
			this.error = false;
			
			this.io = new MessageFactory(15000);

			this.io.write(this.md5,MessageFactory.LOGIN);
			
		} catch (Exception e) {
			e.printStackTrace();
			this.kill();
		}
	}

	public boolean checkError() {
		if( this.error || this.socket == null || !this.socket.isConnected() || !this.io.isAlive())
			return true;
		return false;
	}
	
	public MessageFactory getIO() {
		return this.io;
	}

	public String getMD5() {
		return this.md5;
	}

	public String getName() {
		return name;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public void kill() {	
		try {
			if(this.socket != null) {
				this.socket.close();
				this.socket = null;
			}

		}
		catch (IOException e) { this.socket = null; }
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setError() {
		this.error = true;
	}
	
	public void clearError() {
		this.error = false;
	}
	
	public String toString() {
		return this.name;
	}
}