package tractor.server.handlers;

import java.io.IOException;

import tractor.server.User;

// "threadedhandler" class?
class IOHandler extends ThreadGroup {

	private InputThread[] in;
	private OutputThread[] out;
	private ChatThread[] chat;

	IOHandler() {
		super("IOHandler");

		this.in = new InputThread[50];
		this.out = new OutputThread[50];
		this.chat = new ChatThread[50];
		

	}

	/** It adds a user to the IO handler.
	 * @param user
	 *
	 */
	public void add(User user) {
		try {
			//TODO: synchronize input and output?
			for(int i=0;i<this.in.length;i++) {
				if(this.in[i] != null && this.in[i].getState() == Thread.State.TERMINATED)
					this.in[i] = null;
				if(this.in[i]==null) {
					this.in[i] = new InputThread(this,"InputThread-"+(i+1),10);
					this.in[i].add(user);
					this.in[i].start();
					break;
					//return i; 
				} else if(!this.in[i].isFull()) {
					this.in[i].add(user);
					break;
					//return i;
				}	
			}
			for(int i=0;i<this.out.length;i++) {
				if(this.out[i] != null && this.out[i].getState() == Thread.State.TERMINATED)
					this.out[i] = null;
				if(this.out[i]==null) {
					this.out[i] = new OutputThread(this,"OutputThread-"+(i+1),10);
					this.out[i].add(user);
					this.out[i].start();
					break;
					//return i; 
				} else if(!this.out[i].isFull()) {
					this.out[i].add(user);
					break;
					//return i;
				}	
			}
			for(int i=0;i<this.chat.length;i++) {
				if(this.chat[i] != null && this.chat[i].getState() == Thread.State.TERMINATED)
					this.chat[i] = null;
				if(this.chat[i]==null) {
					this.chat[i] = new ChatThread(this,"ChatThread-"+(i+1),10);
					this.chat[i].add(user);
					this.chat[i].start();
					break;
					//return i; 
				} else if(!this.chat[i].isFull()) {
					this.chat[i].add(user);
					break;
					//return i;
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
			user.setError();
			//return -1;
		}
		//return -1;
	}

}
