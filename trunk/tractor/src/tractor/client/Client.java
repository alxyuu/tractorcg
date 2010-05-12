package tractor.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import tractor.client.handlers.IOFactory;
import tractor.lib.MessageFactory;

public class Client {

	public final static int BEGIN_CONNECT = 3;
	public final static int CONNECTED = 4;

	public final static int DISCONNECTED = 1;
	public final static int DISCONNECTING = 2;
	private static Client instance;
	//do something with this...
	//support hostnames?
	//public final static String ip = "10.4.6.197";
	public final static String ip = "192.168.0.2";
	public final static int NULL = 0;
	public final static int port = 443;
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

	public static void main(String ... bobby) {
		new Client();
	}
	
	Client() {
		Client.instance = this;
		this.clientview = new ClientView();
		this.clientview.setVisible(true);
		this.io = new IOFactory(15000);
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
						this.setError(ClientError.LOGIN_USERNAME_UNAVAILABLE, "login failure: username unavaible");
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
