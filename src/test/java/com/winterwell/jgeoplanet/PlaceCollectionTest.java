package com.winterwell.jgeoplanet;

import java.util.List;

import org.junit.Test;

/**
 * Tests for place collections
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 *
 */
public class PlaceCollectionTest extends GeoPlanetTest {
	
	@Test
	public void testChildren() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh, UK");
		List<Place> children = edinburgh.getChildren().get();
		assert children.size() > 100 : children.size();
		Place marchmont = client.getPlace("Marchmont, Edinburgh");
		assert children.contains(marchmont);
	}
	
	@Test
	public void testChildrenDegree() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Scotland");
		List<Place> children = edinburgh.getChildren().degree(3).get();
		Place marchmont = client.getPlace("Marchmont, Edinburgh");
		assert children.contains(marchmont);
	}

	@Test
	public void testSiblings() throws GeoPlanetException {
		Place marchmont = client.getPlace("Marchmont, Edinburgh");
		Place bruntsfield = client.getPlace("Bruntsfield, Edinburgh");
		assert marchmont.getSiblings().get().contains(bruntsfield);
	}

	@Test
	public void testAncestors() throws GeoPlanetException {
		List<Place> anc = client.getPlace("Marchmont, Edinburgh").getAncestors().get();
		assert anc.contains(client.getPlace("Edinburgh"));
	}
	
	@Test
	public void testBelongsTo() throws GeoPlanetException {
		List<Place> bts = client.getPlace("Marchmont, Edinburgh").getBelongTos().get();
	}
	
	@Test
	public void testDescendents() throws GeoPlanetException {
		Place uk = client.getPlace("Edinburgh");
		List<Place> desc = uk.getDescendents().get();
	}

	@Test
	public void testCountries() throws GeoPlanetException {
		Place earth = client.getPlace(1);
		List<Place> countries = earth.getChildren().typename("Country").get();
		assert countries.size() > 200 : countries.size();
		Place country = countries.get(0);
		country.getName();
		Place parent = country.getParent();
		assert parent.equals(earth);
	}
}
