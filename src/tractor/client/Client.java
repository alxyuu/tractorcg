package tractor.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import tractor.client.handlers.IOFactory;
import tractor.client.game.TractorGame;
import tractor.client.game.TractorGameContainer;
import tractor.lib.MessageFactory;

public class Client {

	public final static int BEGIN_CONNECT = 3;
	public final static int CONNECTED = 4;

	public final static int DISCONNECTED = 1;
	public final static int DISCONNECTING = 2;
	private static Client instance;
	//do something with this...
	//support hostnames?
	public final static String ip = "127.0.0.1";
//	public final static String ip = "208.53.131.251";
	public final static int NULL = 0;
	public final static int port = 9741;
	public static Client getInstance() {
		return Client.instance;
	}
	private ClientView clientview;
	private int connectionStatus;
	private ClientError errorCode;
	private String errorMsg;
	private IOFactory io;
	private String md5;

	private String username;

	private TractorGame game;
	private TractorGameContainer container;
	private Thread gamethread;

	public static void main(String ... bobby) {
		new Client();
	}

	Client() {
		
		Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				// TODO Auto-generated method stub
				e.printStackTrace(System.out);
			}
		});
		
		Client.instance = this;
		this.io = new IOFactory(15000);
		this.clientview = new ClientView();
		this.clientview.setVisible(true);
		this.game = null;
		/*while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}*/
	}

	/**It connects to the network
	 * @param fork
	 * 
	 */
	public void connect(boolean fork) {
		this.connectionStatus = BEGIN_CONNECT;
		if(fork) {
			Thread connect = new Thread("connect") {
				public void run() {
					Client.getInstance().connect(false);
				}
			};
			connect.start();
		} else {
			System.out.println("connecting...");
			if(this.isConnected()) {
				return;
			} 
			io.reset();
			try {
				Socket s = new Socket();
				s.setSoTimeout(1000);
				System.out.println("Establishing Connection");
				s.connect(new InetSocketAddress(Client.ip,Client.port), 2000);
				System.out.println("Connection Established");
				s.setSoTimeout(15000);
				s.setKeepAlive(true);
				io.initIO(s);
			} catch (IOException e) {
				e.printStackTrace();
				//do something
				this.connectionStatus = DISCONNECTED;
				this.setError(ClientError.CONNECT_SERVER_TIMEOUT, "connect failure: server timeout");
				this.clientview.updateStatusTS();
			}
			//get md5 from server
			//TODO: max wait time
			while(!this.io.hasNextMessage(MessageFactory.LOGIN)) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.md5 = this.io.getNextMessage(MessageFactory.LOGIN);

			this.login(false);
		}
	}
	/**It gets the message factory
	 * @return
	 * 
	 */
	public IOFactory getIO() {
		return this.io;
	}

	/** It getts the game.
	 * @return
	 */
	public TractorGame getGame() {
		return this.game;
	}

	/** It sets the game.
	 * @param game
	 */
	public void setGame(TractorGame game) {
		this.game = game;
	}

	/** It starts the game
	 * 
	 */
	public void startGame() {
		this.gamethread = new Thread("game thread") {
			public void run() {
				try {
					container = new TractorGameContainer(game);
					container.setDisplayMode(1024,600,false);
					container.setTargetFrameRate(30);
					container.setAlwaysRender(true);
					//TODO: clean up game on close, don't exit whole program
					//container.setForceExit(false);
					container.start();
				} catch (SlickException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		this.gamethread.start();
	}

	/** It stops the game.
	 * 
	 */
	public void stopGame() {
		this.container.exit();
	}

	/**It gets the username
	 * @return
	 * 
	 */
	public String getUsername() {
		return username;
	}
	/**It gets the error code
	 * @return
	 * 
	 */
	public ClientError getErrorCode() {
		return this.errorCode;
	}
	/**It gets error message
	 * @return
	 * 
	 */
	public String getErrorMessage() {
		return this.errorMsg;
	}
	/**It gets the connection status
	 * @return
	 * 
	 */
	public int getConnectionStatus() {
		return this.connectionStatus;
	}
	/**It returns whether the client is connected
	 * @return
	 * 
	 */
	public boolean isConnected() {
		return this.io.isAlive();
	}

	/**It logs in to the network
	 * @param fork
	 * 
	 */
	public void login(boolean fork) {
		this.connectionStatus = BEGIN_CONNECT;
		if(fork) {
			Thread login = new Thread("login") {
				public void run() {
					Client.getInstance().login(false);
				}
			};
			login.start();
		} else {


			this.username = this.clientview.getUsername();
			this.io.write(this.username,MessageFactory.LOGIN);
			this.io.write(this.md5,MessageFactory.LOGIN);

			while(this.io.isAlive()) {
				if(this.io.hasNextMessage(MessageFactory.LOGIN)) {
					//TODO: catch erroneous message (i.e. not 1 char long, not a proper reply)
					char s = this.io.getNextMessage(MessageFactory.LOGIN).charAt(0);
					switch(s) {
					case '1': 
						this.connectionStatus = CONNECTED;
						this.clearError();
						this.io.write("JOIN #lobby", MessageFactory.CHATCMD);
						this.clientview.updateStatusTS();
						break;
					case '2':
						this.connectionStatus = DISCONNECTED;
						this.setError(ClientError.LOGIN_USERNAME_UNAVAILABLE, "login failure: username unavailable");
						this.clientview.updateStatusTS();
						break;
					case '3':
						this.connectionStatus = DISCONNECTED;
						this.setError(ClientError.LOGIN_MD5_MISMATCH, "login failure: md5 mismatch");
						this.clientview.updateStatusTS();
						break;
					default: 
						this.connectionStatus = DISCONNECTED;
						this.setError(ClientError.LOGIN_ERRONEOUS_MESSAGE, "login failure: unknown server response");
						this.clientview.updateStatusTS();
						break;
					}

				} else {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**It clears errors
	 * 
	 */
	private void clearError() {
		this.errorCode = ClientError.NO_ERROR;
		this.errorMsg = null;
	}
	/**It sets what the error is
	 * @param error
	 * @param message
	 * 
	 */
	private void setError(ClientError error, String message) {
		this.errorCode = error;
		this.errorMsg = message;
	}

	/**It sets what the username is
	 * @param username
	 * 
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
