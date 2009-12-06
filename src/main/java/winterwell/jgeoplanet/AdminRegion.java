package winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An administrative region e.g. State, Country, County, Province, District, Ward.
 * These are not the same as place types.
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
class AdminRegion extends GeoPlanetResource {
	
	private final String name;
	private final String type;
	private final String code;
	
	AdminRegion(GeoPlanet client, JSONObject place, String name) throws JSONException {
		super(client);
		this.name = place.getString(name);
		String attrName = name + " attrs";
		JSONObject attrs = place.getJSONObject(attrName);
		String code = attrs.getString("code");
		this.code = (code.equals("") ? null : code);
		this.type = attrs.getString("type");
	}
	
	/**
	 * @return The name of this administrative region
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of place this administrative region is. The values
	 * returned here are presumably place type names as returned by
	 * {@link Place#getPlaceTypeName()}, but is not covered by the API docs
	 * and should not be assumed.
	 * @return The placeType of this administrative region e.g. "Country"
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns a short code for the region e.g. "IT" for Italy. This
	 * is <em>not</em the same as the (numeric) codes retrieved by {@link Place#placeTypeCode()}.
	 * May be null if there is no known short code.
	 * @return A short code for the region. May be null.
	 */
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "AdminRegion [code=" + code + ", name=" + name + ", type="
				+ type + "]";
	}

}