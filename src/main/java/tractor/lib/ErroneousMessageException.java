package tractor.lib;

public class ErroneousMessageException extends Exception {
	

	private static final long serialVersionUID = -4474777201282561634L;

	/** It constructs the erroneous message exception
	 * 
	 */
	public ErroneousMessageException() {
	}

	/** It constructs the erroneous message exception given a message.
	 * @param msg
	 */
	public ErroneousMessageException(String msg) {
		super(msg);
	}
}