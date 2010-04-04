package com.winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A region of the Earth's surface defined by four corners and some
 * great circles.
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class BoundingBox {

	final Location northEast;
	final Location southWest;
	
	public BoundingBox(Location northEast, Location southWest) {
		if (northEast.latitude < southWest.latitude) {
			throw new IllegalArgumentException("North east corner is south of south west corner");
		}
		this.northEast = northEast;
		this.southWest = southWest;
	}
	
	BoundingBox(JSONObject bbox) throws JSONException {
		this(	new Location(bbox.getJSONObject("northEast")), 
				new Location(bbox.getJSONObject("southWest")));
	}
	
	public Location getNorthEast() {
		return northEast;
	}
	
	public Location getSouthWest() {
		return southWest;
	}
	
	public Location getNorthWest() {
		return new Location(southWest.longitude, northEast.latitude);
	}
	
	public Location getSouthEast() {
		return new Location(northEast.longitude, southWest.latitude);
	}
	
	/**
	 * Determine whether the specified location is contained within this bounding
	 * box.
	 * @param location the location to test
	 * @return true if the location is within this bounding box. False otherwise.
	 */
	public boolean contains(Location location) {
		if (location.latitude > northEast.latitude) return false;
		if (location.latitude < southWest.latitude) return false;
		if (northEast.longitude < 0	&& southWest.longitude >= 0 && southWest.longitude > northEast.longitude) {
			if (location.longitude < 0 && location.longitude > northEast.longitude) return false;
			if (location.longitude >= 0 && location.longitude < southWest.longitude) return false;
		}
		else {
			if (location.longitude > northEast.longitude) return false;
			if (location.longitude < southWest.longitude) return false;
		}
		return true;
	}
		
	/**
	 * Determine whether the specified bounding box is completely contained 
	 * within this one.
	 * @param other the bounding box to test
	 * @return true if the other bounding box is completely contained within this one. False otherwise.
	 */
	public boolean contains(BoundingBox other) {
		return (contains(other.southWest) && contains(other.northEast));
	}
	
	public boolean intersects(BoundingBox other) {
		return (contains(other.northEast)
				|| contains(other.southWest)
				|| contains(other.getNorthWest())
				|| contains(other.getSouthEast()));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((northEast == null) ? 0 : northEast.hashCode());
		result = prime * result
				+ ((southWest == null) ? 0 : southWest.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoundingBox other = (BoundingBox) obj;
		if (northEast == null) {
			if (other.northEast != null)
				return false;
		} else if (!northEast.equals(other.northEast))
			return false;
		if (southWest == null) {
			if (other.southWest != null)
				return false;
		} else if (!southWest.equals(other.southWest))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BoundingBox [northEast=" + northEast + ", southWest="
				+ southWest + "]";
	}
	
}
