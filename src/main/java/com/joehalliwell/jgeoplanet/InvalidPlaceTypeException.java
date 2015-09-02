package com.joehalliwell.jgeoplanet;

/**
 * Thrown if an invalid place type is cited.
 *
 * @author Joe Halliwell
 */
public class InvalidPlaceTypeException extends GeoPlanetException {

    private static final long serialVersionUID = -4976221039931412361L;
    private String placeTypeName;

    public InvalidPlaceTypeException(String placeTypeName) {
        super("Invalid place type: " + placeTypeName);
        this.placeTypeName = placeTypeName;
    }

    /**
     * Returns the invalid place type name that caused this
     * exception.
     *
     * @return the invalid place type name
     */
    public String getInvalidPlaceTypeName() {
        return placeTypeName;
    }

}
