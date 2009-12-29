package com.winterwell.jgeoplanet;

/**
 * A GeoPlanetResource is a resource bound to a particular GeoPlanet
 * instance. As such it has an associated language and application ID.
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
public abstract class GeoPlanetResource {

	private final GeoPlanet client;
	
	GeoPlanetResource(GeoPlanet client) {
		this.client = client;
	}
	
	/**
	 * Returns the client that was used to retrieve this place.
	 * Handy for checking the language, application ID etc.
	 * @return the client associated with this place
	 */
	public GeoPlanet getClient() {
		return this.client;
	}
}
