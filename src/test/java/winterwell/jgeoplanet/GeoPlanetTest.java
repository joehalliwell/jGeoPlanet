package winterwell.jgeoplanet;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

public class GeoPlanetTest {
	
	final static String propertyFile = "jgeoplanet.properties";
	final static String property = "applicationId";
	static GeoPlanet client;
	static String appId;
	
	@BeforeClass
	public static void getAppId() throws Exception {
		Properties properties = new Properties() ;
		try {
			InputStream is =  ClassLoader.getSystemResourceAsStream(propertyFile);
			properties.load(is);
			appId = properties.getProperty(property);
			if (appId == null) throw new Exception("Could not locate property");
			client = new GeoPlanet(appId);
		} catch (Exception e) {
			printTestSetupHelp();
			throw new Exception(e);
		}
	}
	
	static void printTestSetupHelp() {
		String m = "" +
				"***********************************************************************" +
				"ERROR! Could not locate application ID.\n" +
				"Please ensure that you have a properties file called '%1$s' on " +
				"your classpath and that it defines the '%2$s' property correctly. " +
				"Application IDs are available from %3$s" +
				"***********************************************************************";
		System.out.println(String.format(m, propertyFile, property, GeoPlanet.appIdUrl));
	}
	
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
	public void testChildren() throws GeoPlanetException {
		Place edinburgh = client.getPlace("Edinburgh, UK");
		List<Place> children = edinburgh.getChildren().get();
		assert children.size() > 100 : children.size();
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
	public void testCountries() throws GeoPlanetException {
		Place earth = client.getPlace(1);
		List<Place> countries = earth.getChildren().type("Country").get();
		assert countries.size() > 200 : countries.size();
		Place country = countries.get(0);
		country.getName();
		Place parent = country.getParent();
		assert parent.equals(earth);
	}
	
	@Test
	public void testShortForm() throws GeoPlanetException {
		PlaceCollection p = client.getPlaces("Milan, Italy").shortForm(true);
		Place milan = p.get(0);
		assert milan.isLongForm() == false;
		assert milan.getPostal() == null;
	}
	
	@Test
	public void testLocation() throws GeoPlanetException {
		Place ed = client.getPlace("Edinburgh, UK");
		double longitude = ed.getCentroid().getLongitude();
		double lat = ed.getCentroid().getLatitude();
		assert Math.abs(lat - 55) < 1;
		assert Math.abs(longitude + 3.5) < 1;
	}
	
	@Test
	public void testSize() throws GeoPlanetException {
		PlaceCollection eds = client.getPlaces("Edinburgh");
		assert eds.size() == -1;
		Place ed = eds.get(0);
		assert eds.size() >= 1;
	}
	
	@Test
	public void testPlaceType() throws GeoPlanetException {
		Place paris = client.getPlace("Paris, France");
		PlaceType town = paris.getPlaceType();
		assert town.getName().equals("Town") : town.getName();
		assert town.getCode() == 7 : town.getCode();
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
	public void testPlaceTypeNameWierdness() throws GeoPlanetException {
		Place aland = client.getPlace("Greenland");
		assert aland.getPlaceType().equals(client.getPlaceType("Country"));
		assert aland.getPlaceTypeNameVariant().equals("Province");
	}
	
	@Test(expected=InvalidPlaceType.class)
	public void testInvalidPlaceType() throws GeoPlanetException {
		client.getPlaceType("Province");
	}
	
	@Test
	public void testPlaceTypeEquality() throws GeoPlanetException {
		GeoPlanet g = new GeoPlanet(appId, "it");
		Place milano = g.getPlace("Milano, Italia");
		Place milan = client.getPlace("Milan, Italy");
		assert milano.getWoeId() == milan.getWoeId();
		assert milano.getPlaceType().equals(milan.getPlaceType());
	}
}
