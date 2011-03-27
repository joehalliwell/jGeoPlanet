package com.winterwell.jgeoplanet;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.impl.SimpleLog;
import org.junit.BeforeClass;
import org.junit.Test;

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
