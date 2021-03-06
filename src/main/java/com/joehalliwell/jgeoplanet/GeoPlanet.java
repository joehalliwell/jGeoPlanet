package com.joehalliwell.jgeoplanet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java client library for the Yahoo! GeoPlanet service
 * as described at:
 * <a href="http://developer.yahoo.com/geo/geoplanet/">http://developer.yahoo.com/geo/geoplanet/</a>
 * <p>
 * All applications require a valid application ID. These can be
 * obtained from:
 * <a href="http://developer.yahoo.com/wsregapp/">http://developer.yahoo.com/wsregapp/</a>.
 * The application ID is checked when you construct a GeoPlanet object.
 * This requires network access.
 * </p>
 * <p>
 * Example:
 * <pre>
 * String mySecretAppId = "abc123";
 * WhereOnEarth woe = new WhereOnEarth(mySecretAppId);
 * Place earth = woe.get(1);
 * List&lt;Place&gt; countries = earth.getChildren().type("Country").get();
 * </pre>
 * </p>
 * <p/>
 * <p>
 * Spatial entities provided by Yahoo! GeoPlanet are referenced by
 * a 32-bit identifier: the Where On Earth ID (WOEID). WOEIDs are
 * unique and non-repetitive, and are assigned to all entities within
 * the system. A WOEID, once assigned, is never changed or recycled.
 * If a WOEID is deprecated it is mapped to its successor or parent WOEID,
 * so that requests to the service using a deprecated WOEID are served
 * transparently.
 * </p>
 *
 * @author Joe Halliwell
 */
public class GeoPlanet {

    // URL for application IDs
    public static String appIdUrl = "http://developer.yahoo.com/wsregapp/";
    private final String appId;
    private final String language;
    private final String serviceUri;
    private Map<String, PlaceType> placeTypeNameCache;
    private Map<Integer, PlaceType> placeTypeCodeCache;
    // Using apache.commons.logging which ships with httpclient
    protected Log log = LogFactory.getLog(GeoPlanet.class);

    /**
     * Default serviceURI (the Yahoo! implementation) for convenience constructors.
     */
    public final static String defaultServiceUri = "http://where.yahooapis.com/v1";

    /**
     * Default language for convenience constructors.
     */
    public final static String defaultLanguage = "en";

    /**
     * Convenience constructor for English language GeoPlanet applications.
     *
     * @throws GeoPlanetException
     * @see #GeoPlanet(String, String, String)
     */
    public GeoPlanet(String appId) throws GeoPlanetException {
        this(appId, defaultLanguage);
    }

    /**
     * Create a client for the GeoPlanet service using the specified
     * application ID and language.
     *
     * @param appId    your application ID
     * @param language code for the language to use
     * @throws GeoPlanetException
     * @see #GeoPlanet(String, String, String)
     */
    public GeoPlanet(String appId, String language) throws GeoPlanetException {
        this(appId, language, defaultServiceUri);
    }

    /**
     * Create a client for the GeoPlanet service using the specified
     * application ID, language and service URI.
     * NB All constructors require network access to check the application
     * ID and cache localised place types.
     * The official Yahoo! service URI is http://where.yahooapis.com/v1/
     *
     * @param appId      your application ID
     * @param language   code for the language to use
     * @param serviceUri base URI for GeoPlanet requests
     * @throws GeoPlanetException
     */
    public GeoPlanet(String appId, String language, String serviceUri) throws GeoPlanetException {
        this.appId = appId;
        this.language = language;
        this.serviceUri = serviceUri;
        cachePlaceTypes();
    }

    /**
     * Return the Yahoo! application ID used by this client.
     * Application IDs can be obtained from
     * <a href="http://developer.yahoo.com/wsregapp/">http://developer.yahoo.com/wsregapp/</a>.
     *
     * @return the application ID used by this client
     */
    public String getApplicationId() {
        return appId;
    }

    /**
     * Returns the language used by this client such as "en-gb".
     *
     * @return the language used by this client
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Returns the base URI used by the client e.g. http://where.yahooapis.com/v1
     *
     * @return the base URI used by this client
     */
    public String getServiceUri() {
        return serviceUri;
    }

    /**
     * @param woeId the WOE ID to look up
     * @return the place corresponding to the specified WOE ID
     * @throws PlaceNotFoundException if the ID is invalid
     * @throws GeoPlanetException     for general errors
     */
    public Place getPlace(long woeId) throws GeoPlanetException {
        try {
            if (woeId < 0) throw new PlaceNotFoundException("WOEID");
            JSONObject place = doGet("/place/" + woeId, false);
            return new Place(this, place.getJSONObject("place"));
        } catch (PlaceNotFoundException e) {
            assert e.getPlaceName().equals("WOEID");
            throw new PlaceNotFoundException(woeId + " (WOE ID)");
        } catch (JSONException e) {
            throw new GeoPlanetException(e);
        }
    }

    /**
     * Returns the first {@link Place} whose name matches the query
     * to some extent.
     * Roughly equivalent to calling <code>getPlaces(String).get(0)</code>
     * except that it throws a <code>PlaceNotFoundException</code> if
     * there were no matching places.
     *
     * @param query the name of the place to locate
     * @return the place, if found
     * @throws PlaceNotFoundException if there are no matches for the query
     * @throws GeoPlanetException     on general errors
     */
    public Place getPlace(String query) throws GeoPlanetException {
        List<Place> places = getPlaces(query).get(0, 1);
        if (places.size() == 0) throw new PlaceNotFoundException(query);
        return places.get(0);
    }

    /**
     * Given a location (latitude and longitude), return a place at that location
     * using the Yahoo Geocode service.
     * http://developer.yahoo.com/geo/placefinder/guide/requests.html#latitude-longitude
     *
     * @param location the location to search for
     * @return a place at the specified location
     * @throws GeoPlanetException on error
     */
    public Place getPlace(Location location) throws GeoPlanetException {
        try {
            StringBuilder sb = new StringBuilder("http://where.yahooapis.com/geocode?");
            sb.append("q=");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append("&flags=QJ");
            sb.append("&gflags=R");
            sb.append("&appid=");
            sb.append(appId);
            JSONObject resp = doHttpGet(sb.toString());
            Long woeId = resp.getJSONObject("ResultSet").getJSONArray("Results").getJSONObject(0).getLong("woeid");
            Place p = getPlace(woeId);
            return p;
        } catch (Exception e) {
            throw new GeoPlanetException(e);
        }
    }

    /**
     * <p>
     * Returns a {@link PlaceCollection} of places whose names match the query
     * to some extent.
     * </p>
     * <p>
     * Query may include a comma-separated "focus" used to adjust the ordering
     * of results.
     * e.g. <code>getPlaces("Edinburgh, UK")</code> vs. <code>getPlaces("Edinburgh, USA")</code>
     * Additional commas are interpreted as part of the place name.
     * </p>
     * <p>
     * For a "startswith" filter, specify the place as a string followed by an asterisk (*),
     * Towns are returned in probability order. A maximum of 200 places
     * can be returned per request.
     * </p>
     *
     * @return a {@link PlaceCollection} of places matching the query
     */
    public PlaceCollection getPlaces(String query) throws GeoPlanetException {
        int lastComma = query.lastIndexOf(",");
        StringBuilder q = new StringBuilder();
        if (lastComma != -1) {
            q.append(prepQueryTerm(query.substring(0, lastComma)));
            if (lastComma < query.length()) {
                q.append(",");
                q.append(prepQueryTerm(query.substring(lastComma + 1)));
            }
        } else {
            q.append(prepQueryTerm(query));
        }
        return new PlaceCollection(this, q.toString());
    }

    private String prepQueryTerm(final String input) {
        String output;
        output = input.trim();
        try {
            output = URLEncoder.encode(output, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Can't happen");
        }
        //output = output.replace("*", "%2A");
        output = output.replace(",", "%2E");
        if (output.length() == 0) throw new IllegalArgumentException("Empty query string");
        return "'" + output + "'";
    }

    /**
     * @return a Collection of all known PlaceTypes
     */
    public Collection<PlaceType> getPlaceTypes() {
        return placeTypeNameCache.values();
    }

    /**
     * Look up a PlaceType by name.
     *
     * @return the PlaceType corresponding to the provided name.
     * @throws InvalidPlaceTypeException if the name is invalid
     */
    public PlaceType getPlaceType(String placeTypeName) throws InvalidPlaceTypeException {
        PlaceType type = placeTypeNameCache.get(placeTypeName);
        if (type == null) throw new InvalidPlaceTypeException(placeTypeName);
        return type;
    }

    /**
     * Look up a PlaceType by code.
     *
     * @param placeTypeCode a valid place type code
     * @return the PlaceType corresponding to the provided code.
     * @throws InvalidPlaceTypeException if the code is invalid
     */
    public PlaceType getPlaceType(int placeTypeCode) throws InvalidPlaceTypeException {
        PlaceType type = placeTypeCodeCache.get(placeTypeCode);
        if (type == null) throw new InvalidPlaceTypeException(placeTypeCode + " (code)");
        return type;
    }

    /**
     * Used by the constructor to cache a list of place types.
     *
     * @throws GeoPlanetException
     */
    private synchronized void cachePlaceTypes() throws GeoPlanetException {
        if (placeTypeNameCache != null) return;
        try {
            placeTypeCodeCache = new HashMap<Integer, PlaceType>();
            placeTypeNameCache = new HashMap<String, PlaceType>();
            JSONObject tmp = doGet("/placetypes", false);
            tmp = tmp.getJSONObject("placeTypes");
            JSONArray types = tmp.getJSONArray("placeType");
            for (int i = 0; i < types.length(); i++) {
                PlaceType type = new PlaceType(this, types.getJSONObject(i));
                placeTypeCodeCache.put(type.getCode(), type);
                placeTypeNameCache.put(type.getName(), type);
            }
        } catch (JSONException e) {
            throw new GeoPlanetException(e);
        }
    }

    /**
     * Make a request to the GeoPlanet service.
     * All network access goes through this method.
     *
     * @throws GeoPlanetException     for general errors
     * @throws PlaceNotFoundException for not found exceptions
     */
    JSONObject doGet(String path, boolean shortForm) throws GeoPlanetException, PlaceNotFoundException {
        assert path.startsWith("/");
        StringBuilder uri = new StringBuilder(serviceUri);
        uri.append(path);
        uri.append("?");
        uri.append("format=json");
        uri.append("&select=");
        uri.append(shortForm ? "short" : "long");
        uri.append("&lang=");
        uri.append(language);
        log.trace("Fetching: " + uri + "&appId=REDACTED");
        // Don't log appId
        uri.append("&appid=");
        uri.append(appId);
        return doHttpGet(uri.toString());
    }

    /**
     * Get a JSON object from the specified URI.
     * Factored out of {@link #doGet(String, boolean)} for use with the geocode service.
     *
     * @param uri
     * @return
     * @throws GeoPlanetException
     * @throws PlaceNotFoundException
     */
    private JSONObject doHttpGet(String uri) throws GeoPlanetException, PlaceNotFoundException {
        try {
            GetMethod get = new GetMethod(URIUtil.encodePathQuery(uri.toString()));
            HttpClient httpClient = new HttpClient();
            httpClient.executeMethod(get);
            String response = get.getResponseBodyAsString();
            int responseCode = get.getStatusCode();
            if (responseCode != 200) {
                log.trace(responseCode + " response code from server");
            }
            switch (get.getStatusCode()) {
                case 200:
                    break;
                case 400:
                    throw new InvalidAppIdException(appId);
                case 404:
                    // TODO: Collections throw this sometimes -- missing data?
                    throw new PlaceNotFoundException("WOEID");
                default:
                    throw new GeoPlanetException("Unexpected response from GeoPlanet server: " + get.getStatusLine());
            }
            if (response.equals("null")) {
                // TODO: Never a legitimate response?
                throw new GeoPlanetException("Server responded with \"null\" on " + uri);
            }
            try {
                return new JSONObject(response);
            } catch (JSONException e) {
                log.info("Non-JSON response from server: [" + response + "]");
                throw new GeoPlanetException(e);
            }
        } catch (HttpException e) {
            throw new GeoPlanetException(e);
        } catch (IOException e) {
            throw new GeoPlanetException(e);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "WhereOnEarth client [appId=" + appId + ", lang=" + language + "]";
    }

}
