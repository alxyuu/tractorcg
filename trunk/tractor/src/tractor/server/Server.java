package tractor.server;

import java.net.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Vector;
import tractor.server.User;
import tractor.server.handlers.*;

public class Server {

	private static Server instance;
	//private final int MAX_USERS = 500;
	
	public static Server getInstance() {
		return Server.instance;
	}
	
	public static void main(String ... bobby) {
		Server host = new Server();
		host.addHandler(new ConnectionHandler(), "listener");
		host.addHandler(new LoginHandler(), "login");
		host.addHandler(new CleanupHandler(), "user cleanup");
		host.addHandler(new CommandHandler(), "command handler");
	}
	
	private ThreadGroup handlers;
	private final int PORT;
	private ServerSocket socket;
	private ConcurrentHashMap<String,User> users;
	private ConcurrentHashMap<String,Chatroom> chatrooms;
	private Vector<User> waiting;

	Server() {
		this(443);
	}

	Server(int port) {
		Server.instance = this;
		this.PORT = port;
		try {
			System.out.println("initing socket");
			this.socket = new ServerSocket(this.PORT);
			this.users = new ConcurrentHashMap<String,User>();
			this.chatrooms = new ConcurrentHashMap<String,Chatroom>();
			this.waiting = new Vector<User>();	
			this.handlers = new ThreadGroup("Server Handlers");
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("FATAL ERROR: failed to initialize socket");
			System.exit(0);
		}
	}
	
	public void addHandler(ServerHandler handler, String name) {
		Thread t = new Thread(this.handlers, handler, name);
		t.start();
	}

	public void close() throws IOException {
		this.socket.close();
	}

	public ServerSocket getSocket() {
		return this.socket;
	}
	
	public ConcurrentHashMap<String,Chatroom> getChatrooms() {
		return this.chatrooms;
	}
	
	public ConcurrentHashMap<String,User> getUsers() {
		return this.users;
	}
	
	public Vector<User> getWaiting() {
		return this.waiting;
	}
}
