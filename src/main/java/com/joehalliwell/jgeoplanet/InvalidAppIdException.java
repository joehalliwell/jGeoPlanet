package com.joehalliwell.jgeoplanet;

/**
 * Thrown if the GeoPlanet application ID was invalid.
 *
 * @author Joe Halliwell
 */
public class InvalidAppIdException extends GeoPlanetException {

    private static final long serialVersionUID = 3492067671789631252L;

    private final String appId;

    public InvalidAppIdException(String appId) {
        super("Invalid application ID: " + appId);
        this.appId = appId;
    }

    /**
     * Returns the problematic application ID
     *
     * @return the problematic application ID
     */
    public String getInvalidAppId() {
        return appId;
    }
}
