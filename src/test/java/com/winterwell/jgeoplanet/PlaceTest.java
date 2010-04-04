package com.winterwell.jgeoplanet;

import java.util.List;

import org.junit.Test;

/**
 * Tests for Place and related functionality
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 *
 */
public class PlaceTest extends GeoPlanetTest {

	@Test
	public void testEdinburgh() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh, UK");
		assert edinburgh.getWoeId() == 19344 : edinburgh.getWoeId();
	}

	@Test
	public void testParent() throws GeoPlanetException {
		Place lothian = client.getPlace("Lothian");
		Place parent = lothian.getParent();
		assert parent.getName().equals("Scotland");
	}

	@Test
	public void testNotAPlace() throws GeoPlanetException {
		List<Place> zs = client.getPlaces("zzzzzzzzzzzzzzzzzzzzz").get();
		assert zs.size() == 0;
	}
	
	@Test(expected=PlaceNotFoundException.class)
	public void testNoParent() throws GeoPlanetException {
		Place earth = client.getPlace(1);
		assert earth.getName().equals("Earth");
		earth.getParent(); // This should throw a PlaceNotFoundException
	}
	
	@Test
	public void testEquality() throws GeoPlanetException {
		GeoPlanet g = new GeoPlanet(appId);
		Place glasgow = g.getPlace("Glasgow");

		GeoPlanet g2 = new GeoPlanet(appId);
		Place glasgow2 = g2.getPlace("Glasgow");

		assert glasgow.equals(glasgow2);
		assert glasgow.getCentroid().equals(glasgow2.getCentroid());
		assert glasgow.getCountry().equals(glasgow2.getCountry());
	}

	@Test
	public void testPlaceEqualities() throws GeoPlanetException {
		GeoPlanet g = new GeoPlanet(appId, "it");
		Place milano = g.getPlace("Milano, Italia");
		Place milan = client.getPlace("Milan, Italy");
		assert milano.getWoeId() == milan.getWoeId();
		assert milano.equals(milan);
		assert milano.hashCode() == milan.hashCode();

		assert milano.getPlaceType().equals(milan.getPlaceType());
		assert milano.getPlaceType().hashCode() == milan.getPlaceType().hashCode();
	}
	
	@Test
	public void testGetCountry() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh");
		assert edinburgh.getCountry().getName().equals("United Kingdom");

		Place europe = client.getPlace("Europe");
		assert europe.getCountry() == null;
	}

	
}
