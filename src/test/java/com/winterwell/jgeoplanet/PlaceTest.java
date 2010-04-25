package com.winterwell.jgeoplanet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

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

	/**
	 * This currently fails: common doesn't provide json?
	 */
	@Test
	public void testCommonAncestor() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh");
		Place london = client.getPlace("London");

		Place ancestor = edinburgh.getCommonAncestor(london);
		System.out.println(ancestor);
	}

	/**
	 * http://where.yahooapis.com/v1/place/2507854/common/2380824?appid=[yourappidhere]
	 * @throws GeoPlanetException
	 */
	@Test
	public void testCommonAncestorDocExample() throws GeoPlanetException {
		Place a = client.getPlace(2507854);
		Place b = client.getPlace(2380824);
		Place c = a.getCommonAncestor(b);
	}

	@Test
	public void testRankFields() throws GeoPlanetException {
		Place bruntsfield = client.getPlace("Bruntsfield");
		bruntsfield.getPopulationRank();
		bruntsfield.getAreaRank();
	}

	@Test
	public void testAreaOrder() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh");
		Place london = client.getPlace("London");
		assert Place.AREA_ORDER.compare(edinburgh, london) < 0;
		assert Place.AREA_ORDER.compare(london, edinburgh) > 0;
		List<Place> places = Arrays.asList(london, edinburgh);
		Collections.sort(places, Place.AREA_ORDER);
		Assert.assertEquals(edinburgh, places.get(0));
	}

	@Test
	public void testPopulationOrder() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh");
		Place london = client.getPlace("London");
		assert Place.POPULATION_ORDER.compare(edinburgh, london) < 0;
		assert Place.POPULATION_ORDER.compare(london, edinburgh) > 0;
		List<Place> places = Arrays.asList(london, edinburgh);
		Collections.sort(places, Place.POPULATION_ORDER);
		Assert.assertEquals(edinburgh, places.get(0));
	}
	
	@Test
	public void testWildcard() throws GeoPlanetException {
		Place london = client.getPlace("London, UK");
		assert client.getPlaces("Lon*, UK").get().contains(london);
	}
	
	@Test
	public void testPlaceWithSpaceInName() throws GeoPlanetException {
		Place mk = client.getPlace("Milton Keynes, UK");
		Assert.assertEquals(29062, mk.getWoeId());
		
		mk = client.getPlace("Milton Keynes");
		Assert.assertEquals(29062, mk.getWoeId());
		
		assert client.getPlaces("Milt*, UK").get().contains(mk);
	}
	
	@Test
	public void testWeirdPlaceNames() throws GeoPlanetException {
		{
			Place p = client.getPlace("Acock's Green");
			Assert.assertEquals(2413668,  p.getWoeId());
		}
		{
			Place p = client.getPlace("Goring-on-sea");
			Assert.assertEquals(21382,  p.getWoeId());
		}
	}
	
	@Test
	public void testBadQueries() throws GeoPlanetException {
		Place mk = client.getPlace("Milton Keynes,");
		Assert.assertEquals(29062, mk.getWoeId());
		
		mk = client.getPlace("      Milton Keynes");
		Assert.assertEquals(29062, mk.getWoeId());
		
		assert client.getPlaces("Milton Keynes, UK").get().contains(mk);
	}

}
