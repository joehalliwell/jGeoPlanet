package winterwell.jwoe;

public class WhereOnEarthException extends Exception {

	private static final long serialVersionUID = 3442552352655929043L;

	public WhereOnEarthException() {
	}
	
	public WhereOnEarthException(Exception e) {
		super(e);
	}

	public WhereOnEarthException(String message) {
		super(message);
	}
}
