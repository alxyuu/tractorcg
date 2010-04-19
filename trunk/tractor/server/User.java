package tractor.server;

import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;
import tractor.lib.IOFactory;

class User {
	
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
	
	//TODO: timeouts and shits
	private long connectTime;
	private IOFactory io;
	private String md5;
	public Queue<String> messages;
	private String name;
	private boolean ready;
	private Socket socket; 
	
	User(Socket socket) {
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
			this.ready = false;
			
			this.io = new IOFactory(this.socket);

			this.io.write(this.md5,IOFactory.LOGIN);
			
		} catch (Exception e) {
			e.printStackTrace();
			this.io.kill();
		}
	}

	public String getMD5() {
		return this.md5;
	}
	
	public IOFactory getIO() {
		return this.io;
	}

	public String getName() {
		return name;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReadyState(boolean ready) {
		this.ready = ready;
	}

	public String toString() {
		return this.name;
	}
	
}