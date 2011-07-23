package tractor.server;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
	private Vector<Chatroom> chatrooms;
	private String name;
	private boolean error;
	private Socket socket; 
	private Gameroom game;
	private int gameposition;
	private int gamescore;
	private PlayerHand hand;
	private Team team;

	/** Constructor of the User class that initializes the attributes
	 * @param socket
	 *
	 */
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
			this.chatrooms = new Vector<Chatroom>();

			this.io.write(this.md5,MessageFactory.LOGIN);

		} catch (Exception e) {
			e.printStackTrace();
			this.kill();
		}
	}

	/** It adds the user to the chatroom.
	 * @param chat
	 *
	 */
	public void addChatroom(Chatroom chat) {
		this.chatrooms.add(chat);
	}

	public Gameroom getCurrentGame() {
		return this.game;
	}

	public void setCurrentGame(Gameroom game) {
		this.game = game;
	}

	public int getGamePosition() {
		return this.gameposition;
	}

	public void setGamePosition(int position) {
		this.gameposition = position;
	}

	public void setGameScore(int score) {
		this.gamescore = score;
	}

	public int getGameScore() {
		return this.gamescore;
	}

	public PlayerHand getHand() {
		return this.hand;
	}

	public void newHand() {
		this.hand = new PlayerHand();
	}
	
	public Team getTeam() {
		return this.team;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}

	/**It removes the user from the chatroom.
	 * @param chat
	 * 
	 */
	public void removeChatroom(Chatroom chat) {
		this.chatrooms.remove(chat);
	}

	/**It checks whether there is an error for the user.
	 * @return
	 * 
	 */
	public boolean checkError() {
		if( this.error || this.socket == null || !this.socket.isConnected() || !this.io.isAlive())
			return true;
		return false;
	}

	/**It returns the IO.
	 * @return
	 * 
	 */
	public MessageFactory getIO() {
		return this.io;
	}

	/**It returns the MD5 of the user.
	 * @return
	 * 
	 */
	public String getMD5() {
		return this.md5;
	}

	/**It returns the name of the user.
	 * @return
	 * 
	 */
	public String getName() {
		return name;
	}

	/**It returns the server socket of the user.
	 * @return
	 * 
	 */
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * It removes the suer from the chatroom and closes the server socket.
	 */
	public void kill() {
		for(Iterator<Chatroom> i=this.chatrooms.iterator();i.hasNext();) {
			i.next().part(this);
			i.remove();
		}
		try {
			if(this.socket != null) {
				this.socket.close();
				this.socket = null;
			}

		}
		catch (IOException e) { this.socket = null; }
	}

	/**It sets the name of the user.
	 * @param name
	 * 
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * It sets the user's error.
	 */
	public void setError() {
		this.error = true;
	}

	/**
	 * It gets rid of the user's error
	 */
	public void clearError() {
		this.error = false;
	}

	public String toString() {
		return this.name;
	}
}