package winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A geographical location expressed as a latitude and longitude
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
class Location {
	
	private final double longitude;
	private final double latitiude;
	
	Location(JSONObject jsonObject) throws JSONException {
		this.latitiude = jsonObject.getDouble("latitude");
		this.longitude = jsonObject.getDouble("longitude");
	}
	
	/**
	 * @return the latitude of this location
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * @return the longitude of this location
	 */
	public double getLatitude() {
		return latitiude;
	}
}