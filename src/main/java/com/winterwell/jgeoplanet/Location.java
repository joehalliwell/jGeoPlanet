package com.winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A geographical location expressed as a latitude and longitude
 *
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class Location {

	private final static double DIAMETER_OF_EARTH = 6378.1 * 2;
	
	private final double longitude;
	private final double latitiude;

	Location(JSONObject jsonObject) throws JSONException {
		this.latitiude = jsonObject.getDouble("latitude");
		this.longitude = jsonObject.getDouble("longitude");
	}


	/**
	 * Returns  the longitude of this location.
	 * @return the longitude of this location.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the latitude of this location.
	 * @return the latitude of this location.
	 */
	public double getLatitude() {
		return latitiude;
	}

	protected boolean containedIn(Location northEast, Location southWest) {
		assert northEast.latitiude >= southWest.latitiude;
		assert northEast.longitude >= southWest.longitude;
		return (latitiude <= northEast.latitiude
				&& longitude <= northEast.longitude
				&& latitiude >= southWest.latitiude
				&& longitude >= southWest.longitude);
	}

	/**
	 * Rough and ready distance in kilometres between this location
	 * and the specified other.
	 * Uses the Haversine formula.
	 * @see http://en.wikipedia.org/wiki/Great-circle_distance
	 * @param other
	 * @return distance in kilometres
	 */
	public double distance(Location other) {
		final double lat = latitiude * Math.PI / 180;
		final double lon = longitude * Math.PI / 180;
		final double olat = other.latitiude * Math.PI / 180;
		final double olon = other.longitude * Math.PI / 180;
		
		double sin2lat = Math.sin((lat - olat)/2);
		sin2lat = sin2lat * sin2lat;
		double sin2long = Math.sin((lon - olon)/2);
		sin2long = sin2long * sin2long;
		return  DIAMETER_OF_EARTH
			* Math.asin(
					Math.sqrt(sin2lat + Math.cos(lat) * Math.cos(olat) * sin2long));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitiude);
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
		if (Double.doubleToLongBits(latitiude) != Double
				.doubleToLongBits(other.latitiude)) {
			return false;
		}
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude)) {
			return false;
		}
		return true;
	}

}