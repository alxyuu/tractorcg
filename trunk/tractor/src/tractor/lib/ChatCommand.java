package tractor.lib;

import java.util.HashMap;

public enum ChatCommand {
	
	C_JOIN,
	C_PART,
	G_CREATE,
	G_HOOK,
	S_QUIT;
	
	private static HashMap<String,ChatCommand> cset;
	static {
		cset = new HashMap<String,ChatCommand>();
		cset.put("JOIN",C_JOIN);
		cset.put("PART",C_PART);
		cset.put("GCREATE", G_CREATE);
		cset.put("GHOOK", G_HOOK);
		cset.put("QUIT", S_QUIT);
	}
	
	public static ChatCommand get(String s) {
		return cset.get(s);
	}
}