package tractor.server;

public class InputHandler extends ThreadGroup {
	
	InputHandler() {
		super("InputHandler");
		this.setDaemon(true);
		
	}

}
