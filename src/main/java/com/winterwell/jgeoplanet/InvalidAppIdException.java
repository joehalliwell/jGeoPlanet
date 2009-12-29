package com.winterwell.jgeoplanet;

/**
 * Thrown if the GeoPlanet application ID was invalid.
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class InvalidAppIdException extends GeoPlanetException {

	private static final long serialVersionUID = 3492067671789631252L;
	
	private String appId;
	
	public InvalidAppIdException(String appId) {
		super("Invalid application ID: " + appId);
		this.appId = appId;
	}
	
	/**
	 * Returns the problematic application ID
	 * @return the problematic application ID
	 */
	public String getInvalidAppId() {
		return appId;
	}
}
