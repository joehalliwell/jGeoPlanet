package com.winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A geographical location expressed as a latitude and longitude
 *
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class Location {

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
	 * Rough and ready distance in metres between this and other.
	 * Uses the Haversine formula
	 * http://en.wikipedia.org/wiki/Great-circle_distance
	 * @param other
	 * @return
	 */
	public double distance(Location other) {
		final double diameterOfEarth = 6378100 * 2;
		double sin2lat = Math.sin((latitiude - other.latitiude)/2);
		sin2lat = sin2lat * sin2lat;
		double sin2long = Math.sin((longitude - other.longitude)/2);
		sin2long = sin2long * sin2long;
		return  diameterOfEarth
			* Math.asin(
					Math.sqrt(sin2lat + Math.cos(latitiude) * Math.cos(other.latitiude) * sin2long));
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