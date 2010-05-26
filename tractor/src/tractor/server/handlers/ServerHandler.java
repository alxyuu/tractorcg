package tractor.server.handlers;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import tractor.server.Server;
import tractor.server.User;

public abstract class ServerHandler implements Runnable {

	protected Vector<User> waiting;
	protected ConcurrentHashMap<String, User> users;

	ServerHandler() {
		waiting = Server.getInstance().getWaiting();
		users = Server.getInstance().getUsers();
	}
	abstract public void run();

}
