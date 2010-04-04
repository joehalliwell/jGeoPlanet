package com.winterwell.jgeoplanet;

import org.junit.Test;
/**
 * Tests for location and bounding box logic
 * @author Joe Halliwell <joe@winterwell.com>
 *
 */
public class LocationTest extends GeoPlanetTest {

	@Test
	public void testLocation() throws GeoPlanetException {
		Place ed = client.getPlace("Edinburgh, UK");
		double longitude = ed.getCentroid().getLongitude();
		double lat = ed.getCentroid().getLatitude();
		assert Math.abs(lat - 55) < 1;
		assert Math.abs(longitude + 3.5) < 1;
	}
	
	@Test
	public void testPlaceContainment() throws GeoPlanetException {
		Place bruntsfield = client.getPlace("Bruntsfield");
		Place edinburgh = client.getPlace("Edinburgh");
		assert edinburgh.contains(bruntsfield.getCentroid());
		assert edinburgh.contains(bruntsfield);
	}
	
	@Test
	public void testContainment() {
		BoundingBox bbox = new BoundingBox(new Location(10,10), new Location(-10,-10));
		assert bbox.contains(new Location(0,0));
		bbox = new BoundingBox(new Location(10,-10), new Location(-10,10));
		assert !bbox.contains(new Location(0,0));
	}
	
	

	@Test
	public void testDistanceShort() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh");
		Place glasgow = client.getPlace("Glasgow");
		double distance = edinburgh.getCentroid().distance(glasgow.getCentroid());
		assert distance >= 65 && distance <= 70 : distance;
	}

	@Test
	public void testDistanceLong() throws GeoPlanetException {
		GeoPlanet g = new GeoPlanet(appId);
		Place perth = g.getPlace("Perth, Australia");
		Place beijing = g.getPlace("Beijing");
		double distance = perth.getCentroid().distance(beijing.getCentroid());
		assert distance >= 7999 && distance <= 8000 : distance;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidLatitude() {
		new Location(91, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidLongitude() {
		new Location(0, -181);
	}
	
	@Test(expected=IllegalArgumentException.class) 
	public void testInvalidBoundingBox() {
		new BoundingBox(new Location(-10,-10), new Location(10,10));
	}
	
	@Test 
	public void testIntersection() {
		BoundingBox a = new BoundingBox(new Location(10, 10), new Location(-10, -10));
		BoundingBox b = new BoundingBox(new Location(15, 15), new Location(5, 5));
		assert a.intersects(b);
		assert b.intersects(a);
	}

	
}
