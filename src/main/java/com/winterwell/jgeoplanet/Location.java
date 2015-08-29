package com.winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A geographical location expressed as a latitude and longitude.
 *
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class Location {

    private final static double DIAMETER_OF_EARTH = 6378.1 * 2;

    final double longitude;
    final double latitude;

    Location(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getDouble("latitude"),
                jsonObject.getDouble("longitude"));
    }

    /**
     * Construct a new location object. Handy for computing distances.
     *
     * @param latitude  the latitiude of the location. Must be >-90 and <90
     * @param longitude the longitude of the location. Must be >-180 and <180
     * @throws IllegalArgumentException if the co-ordinates aren't valid
     */
    public Location(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude: " + longitude);
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the latitude of this location.
     *
     * @return the latitude of this location.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns  the longitude of this location. A number between -180 and 180.
     *
     * @return the longitude of this location.
     */
    public double getLongitude() {
        return longitude;
    }


    /**
     * Rough and ready distance in kilometres between this location
     * and the specified other.
     * Uses the Haversine formula.
     *
     * @param other
     * @return distance in kilometres
     * @see http://en.wikipedia.org/wiki/Great-circle_distance
     */
    public double distance(Location other) {
        final double lat = latitude * Math.PI / 180;
        final double lon = longitude * Math.PI / 180;
        final double olat = other.latitude * Math.PI / 180;
        final double olon = other.longitude * Math.PI / 180;

        double sin2lat = Math.sin((lat - olat) / 2);
        sin2lat = sin2lat * sin2lat;
        double sin2long = Math.sin((lon - olon) / 2);
        sin2long = sin2long * sin2long;
        return DIAMETER_OF_EARTH
                * Math.asin(
                Math.sqrt(sin2lat + Math.cos(lat) * Math.cos(olat) * sin2long));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Location other = (Location) obj;
        if (Double.doubleToLongBits(latitude) != Double
                .doubleToLongBits(other.latitude)) {
            return false;
        }
        if (Double.doubleToLongBits(longitude) != Double
                .doubleToLongBits(other.longitude)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Location (" + latitude + " N, " + longitude + " E)";
    }

}