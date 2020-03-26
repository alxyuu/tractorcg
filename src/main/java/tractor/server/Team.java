package tractor.server;

import java.util.ArrayList;
import java.util.List;

public class Team {
	
	private List<User> users;
	private int index;
	
	Team() {
		users = new ArrayList<User>();
		this.index = 0;
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
	
	public void goUp(int num) {
		for(User user : this.users)
			user.goUp(num);
	}
}
