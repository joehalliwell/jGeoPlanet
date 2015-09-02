package com.joehalliwell.jgeoplanet;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests for Place and related functionality
 *
 * @author Joe Halliwell
 */
public class PlaceTest extends GeoPlanetTest {

    @Test
    public void testEdinburgh() throws GeoPlanetException {
        Place edinburgh = client.getPlace("Edinburgh, UK");
        assertThat(edinburgh.getWoeId(), is(19344L));
    }

    @Test
    public void testParent() throws GeoPlanetException {
        Place lothian = client.getPlace("Lothian");
        Place parent = lothian.getParent();
        assertThat(parent.getName(), is("Scotland"));
    }

    @Test
    public void testNotAPlace() throws GeoPlanetException {
        List<Place> zs = client.getPlaces("zzzzzzzzzzzzzzzzzzzzz").get();
        assertThat(zs.size(), is(0));
    }

    @Test(expected = PlaceNotFoundException.class)
    public void testNoParent() throws GeoPlanetException {
        Place earth = client.getPlace(1);
        assertThat(earth.getName(), is("Earth"));
        earth.getParent(); // This should throw a PlaceNotFoundException
    }

    @Test
    public void testEquality() throws GeoPlanetException {
        GeoPlanet g = new GeoPlanet(appId);
        Place glasgow = g.getPlace("Glasgow");

        GeoPlanet g2 = new GeoPlanet(appId);
        Place glasgow2 = g2.getPlace("Glasgow");

        assertThat(glasgow, is(glasgow2));
        assertThat(glasgow.getCentroid(), is(glasgow2.getCentroid()));
        assertThat(glasgow.getCountry(), is(glasgow2.getCountry()));
    }

    @Test
    public void testPlaceEqualities() throws GeoPlanetException {
        GeoPlanet g = new GeoPlanet(appId, "it");
        Place milano = g.getPlace("Milano, Italia");
        Place milan = client.getPlace("Milan, Italy");

        // It's the same place
        assertThat(milano.getWoeId(), is(milan.getWoeId()));
        assertThat(milano, is(milan));
        assertThat(milano.hashCode(), is(milan.hashCode()));
        assertThat(milano.getPlaceType(), is(milan.getPlaceType()));
        assertThat(milano.getPlaceType().hashCode(), is(milan.getPlaceType().hashCode()));
    }

    @Test
    public void testGetCountry() throws GeoPlanetException {
        Place edinburgh = client.getPlace("Edinburgh");
        assertThat(edinburgh.getCountry().getName(), is("United Kingdom")); // FIXME: Yes!

        Place europe = client.getPlace("Europe");
        assertThat(europe.getCountry(), is(nullValue()));
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
     *
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
        assertThat(places.get(0), is(edinburgh));
    }

    @Test
    public void testPopulationOrder() throws GeoPlanetException {
        Place edinburgh = client.getPlace("Edinburgh");
        Place london = client.getPlace("London");
        assert Place.POPULATION_ORDER.compare(edinburgh, london) < 0;
        assert Place.POPULATION_ORDER.compare(london, edinburgh) > 0;
        List<Place> places = Arrays.asList(london, edinburgh);
        Collections.sort(places, Place.POPULATION_ORDER);
        assertThat(places.get(0), is(edinburgh));
    }

    @Test
    public void testWildcard() throws GeoPlanetException {
        Place london = client.getPlace("London, UK");
        List<Place> londons = client.getPlaces("Lond*").get();
        assertThat(londons, hasItem(london));
    }

    @Test
    public void testPlaceWithSpaceInName() throws GeoPlanetException {
        Place mk = client.getPlace("Milton Keynes, UK");
        assertThat(mk.getWoeId(), is(29062L));

        mk = client.getPlace("Milton Keynes");
        assertThat(mk.getWoeId(), is(29062L));
        assertThat(client.getPlaces("Milto*, UK").get(), hasItem(mk));
    }

    @Test
    public void testWeirdPlaceNames() throws GeoPlanetException {
        {
            Place p = client.getPlace("Acock's Green");
            assertThat(p.getWoeId(), is(2413668L));
        }
        {
            Place p = client.getPlace("Goring-on-sea");
            assertThat(p.getWoeId(), is(21382L));
        }
    }

    @Test
    public void testExtraneousSpaceIsStripped() throws GeoPlanetException {
        Place mk = client.getPlace("      Milton Keynes");
        assertThat(mk.getWoeId(), is(29062L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadFocus() throws GeoPlanetException {
        Place mk = client.getPlace("Milton Keynes,");
        assertThat(mk.getWoeId(), is(29062L));
    }

}
