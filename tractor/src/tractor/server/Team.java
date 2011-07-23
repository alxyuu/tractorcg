package tractor.server;

import java.util.ArrayList;
import java.util.List;

import tractor.lib.Card;

public class Team {
	
	private List<User> users;
	private int index;
	private int TRUMP_NUMBER;
	
	Team() {
		users = new ArrayList<User>();
		this.index = 0;
		this.TRUMP_NUMBER = Card.TWO;
	}
	
	public void clear() {
		this.users.clear();
	}
	
	public void add(User user) {
		this.users.add(user);
	}
	
	public boolean setCurrentUser(User user) {
		int idx = this.users.indexOf(user);
		if(idx != -1) {
			this.index = idx;
			return true;
		} else {
			return false;
		}
	}
	
	public User next() {
		this.index++;
		if(this.index >= this.users.size())
			this.index=0;
		return this.users.get(index);
	}
	
	public int goUp(int num) {
		this.TRUMP_NUMBER += num;
		return this.TRUMP_NUMBER;
	}
	
	public int getCurrentTrump() {
		return this.TRUMP_NUMBER;
	}
}
