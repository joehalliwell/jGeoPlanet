package winterwell.jwoe;

import java.util.List;
import org.junit.Test;

public class WhereOnEarthTest {
	
	static String appId = "Hw4yIP3V34HUOs9sZlzJ74OGVtQFMU944Z8uLhnCDcPW7i0vf4.3o7mJCcZEz0NR0l9Eiw--";
	
	@Test
	public void testBasic() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place earth = w.getPlace(1);
		assert earth.getName().equals("Earth");
	}
		
	@Test(expected=PlaceNotFoundException.class)
	public void testInvalidWoeId() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		@SuppressWarnings("unused")
		Place junk = w.getPlace(11111111111L);
	}
	
	@Test
	public void testEdinburgh() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place edinburgh = w.getPlace("Edinburgh, UK");
		assert edinburgh.getWoeId() == 19344 : edinburgh.getWoeId();
	}
	
	@Test
	public void testParent() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place lothian = w.getPlace("Lothian");
		Place parent = lothian.getParent();
		assert parent.getName().equals("Scotland");
	}
	
	@Test
	public void testChildren() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place edinburgh = w.getPlace("Edinburgh, UK");
		List<Place> children = edinburgh.getChildren().get();
		assert children.size() > 100 : children.size();
		Place marchmont = w.getPlace("Marchmont, Edinburgh");
		assert children.contains(marchmont);
	}
	
	@Test
	public void testSiblings() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place marchmont = w.getPlace("Marchmont, Edinburgh");
		Place bruntsfield = w.getPlace("Bruntsfield, Edinburgh");
		assert marchmont.getSiblings().get().contains(bruntsfield);
	}
	
	@Test
	public void testAncestors() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		List<Place> anc = w.getPlace("Marchmont, Edinburgh").getAncestors().get();
		assert anc.contains(w.getPlace("Edinburgh"));
	}
	
	@Test
	public void testNotAPlace() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		List<Place> zs = w.getPlaces("zzzzzzzzzzzzzzzzzzzzz").get();
		assert zs.size() == 0;
	}
	
	@Test(expected=PlaceNotFoundException.class)
	public void testNoParent() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place earth = w.getPlace(1);
		assert earth.getName().equals("Earth");
		earth.getParent(); // This should throw a PlaceNotFoundException
	}
	
	@Test
	public void testCountries() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place earth = w.getPlace(1);
		List<Place> countries = earth.getChildren().type("Country").get();
		assert countries.size() > 200 : countries.size();
		Place country = countries.get(0);
		country.getName();
		Place parent = country.getParent();
		assert parent.equals(earth);
	}
	
	@Test
	public void testShortForm() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		PlaceCollection p = w.getPlaces("Milan, Italy").shortForm(true);
		Place milan = p.get(0);
		assert milan.isLongForm() == false;
		assert milan.getPostal() == null;
	}
	
	@Test
	public void testLocation() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		Place ed = w.getPlace("Edinburgh, UK");
		double longitude = ed.getCentroid().getLongitude();
		double lat = ed.getCentroid().getLatitude();
		assert Math.abs(lat - 55) < 1;
		assert Math.abs(longitude + 3.5) < 1;
		System.out.println(ed);
	}
	
	@Test
	public void testSize() throws WhereOnEarthException {
		WhereOnEarth w = new WhereOnEarth(appId);
		PlaceCollection eds = w.getPlaces("Edinburgh");
		assert eds.size() == -1;
		Place ed = eds.get(0);
		assert eds.size() >= 1;
	}
}
