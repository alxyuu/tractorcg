package tractor.lib;

public class ErroneousMessageException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4474777201282561634L;

	public ErroneousMessageException() {
	}

	public ErroneousMessageException(String msg) {
		super(msg);
	}
}