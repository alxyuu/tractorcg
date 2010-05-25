package tractor.lib;

import java.util.HashMap;

public class GameCommand {

	/*
	 * Primary commands
	 */
	public final static int NULL = -1;
	public final static int JOIN = 0;
	public final static int UPDATE_STATE = 1;
	public final static int PART = 2;
	public final static int SET_HOST = 3;
	public final static int PLAY_CARD = 4;
	public final static int THIS_TURN = 5;
	public final static int CLEAR_TABLE = 6;
	public final static int SET_STATS = 7;
	
	
	
	/*
	 * Argument commands
	 */
	public final static int WAITING = 10;
	public final static int READY = 11;
	public final static int START = 12;
	public final static int DEALING = 13;
	public final static int DIPAI  = 14;
	public final static int PLAYING = 15;
	
	
	/*private static HashMap<String,Integer> gset;
	static {
		gset = new HashMap<String,Integer>();
		
		gset.put("0", GameCommand.JOIN);
		gset.put("1", GameCommand.UPDATE_STATE);
		
		gset.put("10", GameCommand.WAITING);
		gset.put("11", GameCommand.READY);
		gset.put("12", GameCommand.START);
		gset.put("13", GameCommand.DEALING);
		gset.put("14", GameCommand.DIPAI);
		gset.put("15", GameCommand.PLAYING);
	}
	
	/**It returns a command
	 * @param s
	 * @return
	 * 
	 */
	/*public static int get(String s) {
		try {
			return gset.get(s);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return GameCommand.NULL;
		}
	}*/
}
