package winterwell.jgeoplanet;

/**
 * Thrown for general, network and protocol errors.
 * Superclass for other exceptions thrown by the library.
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class GeoPlanetException extends Exception {

	private static final long serialVersionUID = 3442552352655929043L;

	public GeoPlanetException() {
	}
	
	public GeoPlanetException(Exception e) {
		super(e);
	}

	public GeoPlanetException(String message) {
		super(message);
	}
}
