package com.winterwell.jgeoplanet;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.winterwell.jgeoplanet.GeoPlanet;
import com.winterwell.jgeoplanet.GeoPlanetException;
import com.winterwell.jgeoplanet.InvalidAppIdException;
import com.winterwell.jgeoplanet.InvalidPlaceTypeException;
import com.winterwell.jgeoplanet.Place;
import com.winterwell.jgeoplanet.PlaceCollection;
import com.winterwell.jgeoplanet.PlaceNotFoundException;
import com.winterwell.jgeoplanet.PlaceType;

/**
 * Base class for jGeoPlanet tests
 * 
 * @author Joe Halliwell <joe@winterwell.com>
 */
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

	@Test(expected=AssertionError.class)
	public void testAssertionsEnabled() {
		assert false;
	}
	
}
