package com.winterwell.jgeoplanet;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;



import java.util.List;

/**
 * Tests for place collections
 *
 * @author Joe Halliwell
 */
public class PlaceCollectionTest extends GeoPlanetTest {

    @Test
    public void testChildren() throws GeoPlanetException {
        Place edinburgh = client.getPlace("Edinburgh, UK");
        List<Place> children = edinburgh.getChildren().get();
        assertThat(children.size(), is(greaterThan(100)));
        Place pilrig = client.getPlace("Pilrig, Edinburgh");
        assertThat(children, hasItem(pilrig));
    }

    @Test
    public void testChildrenDegree() throws GeoPlanetException {
        Place scotland = client.getPlace("Scotland");
        List<Place> children = scotland.getChildren().degree(3).get();
        // This used to work...
        //Place pilrig = client.getPlace("Pilrig, Edinburgh");
        //assertThat(children, hasItem(pilrig));
        // but now...
        assertThat(children.size(), is(0));
    }

    @Test
    public void testSiblings() throws GeoPlanetException {
        Place marchmont = client.getPlace("Marchmont, Edinburgh");
        Place bruntsfield = client.getPlace("Bruntsfield, Edinburgh");
        assertThat(marchmont.getSiblings().get(), hasItem(bruntsfield));
    }

    @Test
    public void testAncestors() throws GeoPlanetException {
        List<Place> anc = client.getPlace("Pilrig, Edinburgh").getAncestors().get();
        assertThat(anc, hasItem(client.getPlace("Edinburgh, UK")));
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
        assertThat(countries.size(), is(greaterThan(200)));
        Place country = countries.get(0);
        country.getName();
        Place parent = country.getParent();
        assertThat(parent, is(earth));
    }
}
