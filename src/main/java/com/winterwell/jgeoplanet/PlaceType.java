package com.winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A type of place such as: Country, Ocean, Town.
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class PlaceType extends GeoPlanetResource {

	private final int code;
	private final String name;
	private final String description;
	
	PlaceType(GeoPlanet client, JSONObject placeType) throws JSONException {
		super(client);
		code = placeType.getJSONObject("placeTypeName attrs").getInt("code");
		name = placeType.getString("placeTypeName");
		description = placeType.getString("placeTypeDescription");
	}
	
	/**
	 * Returns the numerical code corresponding to the place type e.g. 7 for a "Town"
	 * @return the place type code
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Returns the (localised) name of this place type e.g. "Town", "Country"
	 * @return name of this place type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a natural language description of this place type.
	 * This does not seem to be correctly localised at present.
	 * @return the description of this place type
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Return a URI corresponding to this placeType
	 * @return the URI of this place type
	 */
	public String getUri() {
		return getClient().getServiceUri() + "/placetype/" + code;
	}
	
	/**
	 * NB PlaceTypes are considered equal if they have the same code.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlaceType other = (PlaceType) obj;
		if (code != other.code)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	
	@Override
	public String toString() {
		return "PlaceType [code=" + code + ", description=" + description
				+ ", name=" + name + "]";
	}

}
