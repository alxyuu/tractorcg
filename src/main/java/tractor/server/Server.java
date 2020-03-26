package tractor.server;

import java.net.ServerSocket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Vector;

import tractor.lib.Card;
import tractor.server.User;
import tractor.server.handlers.*;

public class Server {

	private static Server instance;
	//private final int MAX_USERS = 500;

	/**It returns the instance of the server
	 * @return
	 *
	 */
	public static Server getInstance() {
		return Server.instance;
	}

	public static void main(String ... bobby) {
		Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				// TODO Auto-generated method stub
				e.printStackTrace(System.out);
			}
		});

		Server host = new Server();
		host.addHandler(new ConnectionHandler(), "listener");
		host.addHandler(new LoginHandler(), "login");
		host.addHandler(new CleanupHandler(), "user cleanup");
		host.addHandler(new CommandHandler(), "command handler");
		Card.populateDeck();
	}

	private ThreadGroup handlers;
	private final int PORT;
	private ServerSocket socket;
	private ConcurrentHashMap<String,User> users;
	private ConcurrentHashMap<String,Chatroom> chatrooms;
	private Vector<User> waiting;

	Server() {
		this(Integer.parseInt(System.getenv("PORT")));
	}

	Server(int port) {
		Server.instance = this;
		this.PORT = port;
		try {
			System.out.println("initing socket on port " + this.PORT);
			this.socket = new ServerSocket(this.PORT);
			this.users = new ConcurrentHashMap<String,User>();
			this.chatrooms = new ConcurrentHashMap<String,Chatroom>();
			this.waiting = new Vector<User>();
			this.handlers = new ThreadGroup("Server Handlers");
		} catch (IOException e) {
			e.printStackTrace(System.out);
			System.out.println("FATAL ERROR: failed to initialize socket");
			System.exit(0);
		}
	}

	/**It adds a server handler.
	 * @param handler
	 * @param name
	 *
	 */
	public void addHandler(ServerHandler handler, String name) {
		Thread t = new Thread(this.handlers, handler, name);
		t.start();
	}

	/**It closes a server socket.
	 * @throws IOException
	 *
	 */
	public void close() throws IOException {
		this.socket.close();
	}

	/**It returns the socket of the server
	 * @return
	 *
	 */
	public ServerSocket getSocket() {
		return this.socket;
	}

	/**It returns the various chatrooms on the server.
	 * @return
	 *
	 */
	public ConcurrentHashMap<String,Chatroom> getChatrooms() {
		return this.chatrooms;
	}

	/**It gets the users on the server.
	 * @return
	 *
	 */
	public ConcurrentHashMap<String,User> getUsers() {
		return this.users;
	}

	/**It returns the users waiting to login
	 * @return
	 *
	 */
	public Vector<User> getWaiting() {
		return this.waiting;
	}
}
