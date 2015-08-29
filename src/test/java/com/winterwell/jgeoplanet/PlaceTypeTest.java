package com.winterwell.jgeoplanet;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for placetypes and placetype filtering
 *
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class PlaceTypeTest extends GeoPlanetTest {

    @Test
    public void testPlaceType() throws GeoPlanetException {
        Place paris = client.getPlace("Paris, France");
        PlaceType town = paris.getPlaceType();
        assertThat(town.getName(), is("Town"));
        assertThat(town.getCode(), is(7));
    }

    @Test
    public void testPlaceTypeNameWierdness() throws GeoPlanetException {
        Place aland = client.getPlace("Greenland");
        assertThat(aland.getPlaceType(), is(client.getPlaceType("Country")));
        assertThat(aland.getPlaceTypeNameVariant(), is("Province"));
    }

    @Test(expected = InvalidPlaceTypeException.class)
    public void testInvalidPlaceType() throws GeoPlanetException {
        client.getPlaceType("Province");
    }

    @Test
    public void testMultipleTypenames() throws GeoPlanetException {
        GeoPlanet g = new GeoPlanet(appId);
        Place edinburgh = g.getPlaces("Edinburgh, UK").typename("Country", "Town").get(0);
        assertThat(edinburgh, is(not(nullValue())));
        assertThat(edinburgh.getName(), is("Edinburgh"));
    }

    @Test
    public void testMultipleTypes() throws GeoPlanetException {
        PlaceType country = client.getPlaceType("Country");
        PlaceType town = client.getPlaceType("Town");
        Place edinburgh = client.getPlaces("Edinburgh, UK").type(country, town).get(0);
        assertThat(edinburgh, is(not(nullValue())));
        assertThat(edinburgh.getName(), is("Edinburgh"));
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void testType() throws GeoPlanetException {
        Place edinburgh = client.getPlaces("Edinburgh, UK").typename("Country").get(0);
    }

    @Test
    public void testClearTypename() throws GeoPlanetException {
        GeoPlanet g = new GeoPlanet(appId);
        Place edinburgh = g.getPlaces("Edinburgh, UK").typename("Country").typename().get(0);
        assertThat(edinburgh, is(not(nullValue())));
        assertThat(edinburgh.getName(), is("Edinburgh"));
    }

    @Test
    public void testClearType() throws GeoPlanetException {
        Place edinburgh = client.getPlaces("Edinburgh, UK").typename("Country").type().get(0);
        assertThat(edinburgh, is(not(nullValue())));
        assertThat(edinburgh.getName(), is("Edinburgh"));
    }
}

