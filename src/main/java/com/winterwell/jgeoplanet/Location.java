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