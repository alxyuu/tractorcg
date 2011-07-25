package tractor.lib;


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
	public final static int YOUR_TURN = 5;
	public final static int CLEAR_TABLE = 6;
	public final static int SET_STATS = 7;
	public final static int PLAY_INVALID = 8;
	public final static int PLAY_SUCCESS = 9;
	/*
	 * Argument commands
	 */
	public final static int WAITING = 10;
	public final static int READY = 11;
	public final static int START = 12;
	public final static int DEALING = 13;
	public final static int DIPAI  = 14;
	public final static int PLAYING = 15;
}
