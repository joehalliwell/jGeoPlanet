package com.winterwell.jgeoplanet;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.junit.Test;

/**
 * Miscellaneous tests
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 *
 */
public class MiscTest extends GeoPlanetTest {
	@Test
	public void testBasic() throws GeoPlanetException {
		Place earth = client.getPlace(1);
		assert earth.getName().equals("Earth");
	}

	@Test(expected=InvalidAppIdException.class)
	public void testInvalidAppId() throws GeoPlanetException {
		GeoPlanet g = new GeoPlanet("invalid-app-id");
	}

	@Test(expected=PlaceNotFoundException.class)
	public void testInvalidWoeId() throws GeoPlanetException {
		client.getPlace(11111111111L);
	}

	@Test(expected=PlaceNotFoundException.class)
	public void testNegativeWoeId() throws GeoPlanetException {
		client.getPlace(-1);
	}

	@Test
	public void testSize() throws GeoPlanetException {
		PlaceCollection eds = client.getPlaces("Edinburgh");
		assert eds.size() == -1;
		eds.get(0);
		assert eds.size() >= 1;
	}
	
	@Test
	public void testLocalisation() throws GeoPlanetException {
		GeoPlanet g = new GeoPlanet(appId, "it");
		Place milan = g.getPlace("Milano, Italia");
		assert milan.getWoeId() == 718345;
		assert milan.getClient().getLanguage().startsWith("it");
		assert milan.getPlaceType().getName().equals("Città");
	}
	
	@Test
	public void testFocusWeirdness() throws GeoPlanetException {
		int kents = client.getPlaces("Kent, UK").get().size();
		int kents2 = client.getPlaces("Kent%2CUK").get().size();
		Assert.assertEquals(40, kents);
		Assert.assertEquals(1, kents2);
	}

	@Test
	public void testShortForm() throws GeoPlanetException {
		PlaceCollection p = client.getPlaces("Milan, Italy").shortForm(true);
		Place milan = p.get(0);
		assert milan.isLongForm() == false;
		assert milan.getPostal() == null;
	}
	
	@Test
	public void testFijiAncestors() throws GeoPlanetException {
		Place fiji = client.getPlace("Fiji");
		List<Place> towns = fiji.getAncestors().get(); // This 404s...
	}
	
	@Test
	public void testFiji() throws GeoPlanetException {
		Place fiji = client.getPlace("Fiji");
		Place suva = client.getPlace("Suva");
		List<Place> towns = fiji.getDescendents().typename("Town").get();

		//System.out.println(suva.getBoundingBox());
		//System.out.println(fiji.getBoundingBox());
		
		assert towns.contains(suva);
		assert fiji.contains(suva);
	}

	@Test
	public void testFiltering() throws GeoPlanetException, URIException {
		Place uk = client.getPlace("UK");
		List<Place> filtered = new ArrayList<Place>();
		List<Place> leeds = client.getPlaces("Leeds").get();
		for (Place p : leeds) {
		    if (uk.contains(p)) filtered.add(p);
		}
		System.out.println("Leeds in UK: " + filtered.size());
		System.out.println("Outside the UK: " + (leeds.size() - filtered.size()));
	}
	
	@Test
	public void testGeocode() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh, UK");
		Place other = client.getPlace(edinburgh.getCentroid());
		
		System.out.println(other);
		assert edinburgh.contains(other);
	}
}
