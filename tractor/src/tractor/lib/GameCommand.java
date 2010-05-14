package tractor.lib;

import java.util.HashMap;

public class GameCommand {

	/*
	 * Primary commands
	 */
	public final static int NULL = -1;
	public final static int JOIN = 0;
	public final static int UPDATE_STATE = 1;
	
	
	
	/*
	 * Argument commands
	 */
	public final static int WAITING = 10;
	public final static int DEALING = 11;
	public final static int PLAYING = 12;
	
	
	private static HashMap<String,Integer> gset;
	static {
		gset = new HashMap<String,Integer>();
		gset.put("1",GameCommand.UPDATE_STATE);
		gset.put("10",GameCommand.WAITING);
		gset.put("11", GameCommand.DEALING);
		gset.put("12", GameCommand.PLAYING);
	}
	
	/**It returns a command
	 * @param s
	 * @return
	 * 
	 */
	public static int get(String s) {
		try {
			return gset.get(s);
		} catch (NullPointerException e) {
			return GameCommand.NULL;
		}
	}
}
