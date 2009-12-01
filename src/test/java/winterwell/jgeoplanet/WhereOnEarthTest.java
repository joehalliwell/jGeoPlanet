package winterwell.jgeoplanet;

import java.util.List;
import org.junit.Test;

import winterwell.jgeoplanet.Place;
import winterwell.jgeoplanet.PlaceCollection;
import winterwell.jgeoplanet.PlaceNotFoundException;
import winterwell.jgeoplanet.GeoPlanet;
import winterwell.jgeoplanet.GeoPlanetException;

public class WhereOnEarthTest {
	
	static String appId = "Hw4yIP3V34HUOs9sZlzJ74OGVtQFMU944Z8uLhnCDcPW7i0vf4.3o7mJCcZEz0NR0l9Eiw--";
	
	@Test
	public void testBasic() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place earth = w.getPlace(1);
		assert earth.getName().equals("Earth");
	}
		
	@Test(expected=PlaceNotFoundException.class)
	public void testInvalidWoeId() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		@SuppressWarnings("unused")
		Place junk = w.getPlace(11111111111L);
	}
	
	@Test
	public void testEdinburgh() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place edinburgh = w.getPlace("Edinburgh, UK");
		assert edinburgh.getWoeId() == 19344 : edinburgh.getWoeId();
	}
	
	@Test
	public void testParent() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place lothian = w.getPlace("Lothian");
		Place parent = lothian.getParent();
		assert parent.getName().equals("Scotland");
	}
	
	@Test
	public void testChildren() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place edinburgh = w.getPlace("Edinburgh, UK");
		List<Place> children = edinburgh.getChildren().get();
		assert children.size() > 100 : children.size();
		Place marchmont = w.getPlace("Marchmont, Edinburgh");
		assert children.contains(marchmont);
	}
	
	@Test
	public void testSiblings() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place marchmont = w.getPlace("Marchmont, Edinburgh");
		Place bruntsfield = w.getPlace("Bruntsfield, Edinburgh");
		assert marchmont.getSiblings().get().contains(bruntsfield);
	}
	
	@Test
	public void testAncestors() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		List<Place> anc = w.getPlace("Marchmont, Edinburgh").getAncestors().get();
		assert anc.contains(w.getPlace("Edinburgh"));
	}
	
	@Test
	public void testNotAPlace() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		List<Place> zs = w.getPlaces("zzzzzzzzzzzzzzzzzzzzz").get();
		assert zs.size() == 0;
	}
	
	@Test(expected=PlaceNotFoundException.class)
	public void testNoParent() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place earth = w.getPlace(1);
		assert earth.getName().equals("Earth");
		earth.getParent(); // This should throw a PlaceNotFoundException
	}
	
	@Test
	public void testCountries() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place earth = w.getPlace(1);
		List<Place> countries = earth.getChildren().type("Country").get();
		assert countries.size() > 200 : countries.size();
		Place country = countries.get(0);
		country.getName();
		Place parent = country.getParent();
		assert parent.equals(earth);
	}
	
	@Test
	public void testShortForm() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		PlaceCollection p = w.getPlaces("Milan, Italy").shortForm(true);
		Place milan = p.get(0);
		assert milan.isLongForm() == false;
		assert milan.getPostal() == null;
	}
	
	@Test
	public void testLocation() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		Place ed = w.getPlace("Edinburgh, UK");
		double longitude = ed.getCentroid().getLongitude();
		double lat = ed.getCentroid().getLatitude();
		assert Math.abs(lat - 55) < 1;
		assert Math.abs(longitude + 3.5) < 1;
		System.out.println(ed);
	}
	
	@Test
	public void testSize() throws GeoPlanetException {
		GeoPlanet w = new GeoPlanet(appId);
		PlaceCollection eds = w.getPlaces("Edinburgh");
		assert eds.size() == -1;
		Place ed = eds.get(0);
		assert eds.size() >= 1;
	}
}
