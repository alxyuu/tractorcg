package tractor.lib;

import java.util.HashMap;

public enum ChatCommand {
	
	C_JOIN,
	C_PART,
	E_ERR,
	G_CREATE,
	G_HOOK,
	G_PART,
	S_QUIT;
	
	private static HashMap<String,ChatCommand> cset;
	static {
		cset = new HashMap<String,ChatCommand>();
		cset.put("JOIN",C_JOIN);
		cset.put("PART",C_PART);
		cset.put("ERR", E_ERR);
		cset.put("GCREATE", G_CREATE);
		cset.put("GHOOK", G_HOOK);
		cset.put("GPART", G_PART);
		cset.put("QUIT", S_QUIT);
	}
	
	/**It returns a command
	 * @param s
	 * @return
	 * 
	 */
	public static ChatCommand get(String s) {
		return cset.get(s);
	}
}
