package tractor.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Vector;
import tractor.lib.IOFactory;
import tractor.server.User;

class Server {

	private ServerSocket socket;
	private ConcurrentHashMap<String,User> users;
	private Vector<User> waiting;
	final int PORT;

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
					try {
						Socket sock = socket.accept();
						System.out.println(sock);
						sock.setSoTimeout(15000);
						sock.setKeepAlive(true);
						User user = new User(sock);
						waiting.add(user);
					} catch (IOException e) {
						e.printStackTrace();
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
							IOFactory io = user.getIO();
							if(io.getMessageSize(IOFactory.LOGIN) >= 2) {
								String name = io.getNextMessage(IOFactory.LOGIN);
								if(io.getNextMessage(IOFactory.LOGIN).equals(user.getMD5())) {
									if(!users.containsKey(name)) {
										user.setName(name);
										io.write("1",IOFactory.LOGIN);
										users.put(name,user);
									} else {
										io.write("2+",IOFactory.LOGIN);
										io.clearMessageQueue(IOFactory.LOGIN);
										waiting.add(user);
									}
								} else {
									io.write("3",IOFactory.LOGIN);
									System.out.println("Auth Failure: MD5 mismatch from "+user.toString()+", booting");
									io.kill();
									user = null;
								}
							} else {
								//System.out.println("where did they go");
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
							if(user.getIO().checkError()) {
								user.getIO().kill();
								users.remove(name);
								System.out.println(user.toString()+" - connection closed");
							}
						}
						for(int i = 0; i < waiting.size(); i++) {
							User user = waiting.get(i);
							if(user.getIO().checkError()) {
								user.getIO().kill();
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
