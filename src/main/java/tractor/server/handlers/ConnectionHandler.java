package tractor.server.handlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import tractor.server.Server;
import tractor.server.User;

public class ConnectionHandler extends ServerHandler {
	public void run() {
		System.out.println("listening for connections");
		ServerSocket socket = Server.getInstance().getSocket();
		IOHandler io = new IOHandler();
		while(true) {
			//TODO: proper overflow response
			//if(users.size()+waiting.size() < MAX_USERS) {
			try {
				Socket sock = socket.accept();
				System.out.println(sock);
				sock.setSoTimeout(15000);
				sock.setKeepAlive(true);
				User user = new User(sock);
				io.add(user);
				synchronized(waiting) {
					waiting.add(user);
				}
			} catch (IOException e) {
				e.printStackTrace(System.out);
			}
			/*} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
		}
	}
}
