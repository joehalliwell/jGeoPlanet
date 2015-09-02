package com.joehalliwell.jgeoplanet;

/**
 * Superclass for other exceptions thrown by the library.
 * This exception is also thrown for network and protocol errors.
 *
 * @author Joe Halliwell
 */
public class GeoPlanetException extends Exception {

    private static final long serialVersionUID = 3442552352655929043L;

    GeoPlanetException(Exception e) {
        super(e);
    }

    GeoPlanetException(String message) {
        super(message);
    }
}
