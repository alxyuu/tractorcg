package tractor.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Vector;
import tractor.lib.MessageFactory;
import tractor.server.User;

class Server {

	private ServerSocket socket;
	private ConcurrentHashMap<String,User> users;
	private Vector<User> waiting;
	private IOHandler io;
	private final int PORT;
	private final int MAX_USERS = 500;

	public static void main(String ... bobby) {
		Server host = new Server();
		host.listen();
	}

	Server() {
		this(443);
	}

	Server(int port) {
		this.PORT = port;
		try {
			System.out.println("initing socket");
			this.socket = new ServerSocket(this.PORT);
			this.users = new ConcurrentHashMap<String,User>();
			this.waiting = new Vector<User>();	
			this.io = new IOHandler();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("FATAL ERROR: failed to initialize socket");
			System.exit(0);
		}
	}

	public ServerSocket getSocket() {
		return this.socket;
	}

	public void listen() {

		Thread listener = new Thread("listener") {
			public void run() {
				System.out.println("listening for connections");
				while(true) {
					//TODO: proper overflow response
					if(users.size()+waiting.size() < MAX_USERS) {
						try {
							Socket sock = socket.accept();
							System.out.println(sock);
							sock.setSoTimeout(15000);
							sock.setKeepAlive(true);
							User user = new User(sock);
							io.add(user);
							waiting.add(user);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		listener.start();

		Thread login = new Thread("login") {
			public void run() {
				System.out.println("waiting for logins");
				while(true) {
					try {
						if(!waiting.isEmpty()) {
							User user = waiting.remove(0);
							MessageFactory io = user.getIO();
							if(io.getMessageSize(MessageFactory.LOGIN) >= 2) {
								String name = io.getNextMessage(MessageFactory.LOGIN);
								if(io.getNextMessage(MessageFactory.LOGIN).equals(user.getMD5())) {
									if(!users.containsKey(name)) {
										user.setName(name);
										io.write("1",MessageFactory.LOGIN);
										users.put(name,user);
									} else {
										io.write("2",MessageFactory.LOGIN);
										io.clearMessageQueue(MessageFactory.LOGIN);
										waiting.add(user);
									}
								} else {
									io.write("3",MessageFactory.LOGIN);
									System.out.println("Auth Failure: MD5 mismatch from "+user.toString()+", booting");
									//delay kill? user may not have received message yet
									user.kill();
									user = null;
								}
							} else {
								if(!waiting.isEmpty()) {
									waiting.add(1,user);
								} else {
									waiting.add(user);
									Thread.sleep(500);
								}
							}
						} else {
							//wait for half a second if we're not doing anything...
							Thread.sleep(500);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		login.start();

		Thread dispose = new Thread("dispose") {
			public void run() {
				System.out.println("waiting on dead connections");
				while(true) {
					try {
						for(String name : users.keySet()) {
							User user = users.get(name);
							if(user.checkError()) {
								user.kill();
								users.remove(name);
								System.out.println(user.toString()+" - connection closed");
							}
						}
						for(int i = 0; i < waiting.size(); i++) {
							User user = waiting.get(i);
							if(user.checkError()) {
								user.kill();
								waiting.remove(i);
								System.out.println(user.toString()+" - connection closed");
								i--;
							}
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						//do nothing
					} catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						//do nothing
					}
				}
			}
		};
		dispose.start();
	}

	public void close() throws IOException {
		this.socket.close();
	}
}
