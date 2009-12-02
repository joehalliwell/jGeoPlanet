package winterwell.jgeoplanet;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Java client library for the Yahoo! GeoPlanet service
 * as described at: http://developer.yahoo.com/geo/geoplanet/
 * 
 * <p>
 * All applications require a valid Application ID. These can be
 * obtained from: http://developer.yahoo.com/wsregapp/
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
 * 
 * <p>
 * Spatial entities provided by Yahoo! GeoPlanet are referenced by
 * a 32-bit identifier: the Where On Earth ID (WOEID). WOEIDs are
 * unique and non-repetitive, and are assigned to all entities within
 * the system. A WOEID, once assigned, is never changed or recycled.
 * If a WOEID is deprecated it is mapped to its successor or parent WOEID,
 * so that requests to the service using a deprecated WOEID are served
 * transparently. 
 * </p>
 * @author Joe Halliwell <joe@winterwell.com>
 *
 */
public class GeoPlanet {

	private final String appId;
	private final String language;
	private HttpClient httpClient;
	private final String serviceURI;
	
	/**
	 * Default serviceURI (the Yahoo! implementation) for convenience constructors.
	 */
	public final static String defaultServiceURI = "http://where.yahooapis.com/v1";
	
	/**
	 * Default language for convenience constructors.
	 */
	public final static String defaultLanguage = "en";	
	
	/**
	 * Convenience constructor for English language GeoPlanet applications.
	 */
	public GeoPlanet(String appId) {
		this(appId, defaultLanguage);
	}
	
	/**
	 * Create a client for the GeoPlanet service using the specified
	 * application ID and language.
	 * @param appId your application ID
	 * @param language code for the language to use
	 */
	public GeoPlanet(String appId, String language) {
		this(appId, language, defaultServiceURI);
	}
	
	/**
	 * Create a client for the GeoPlanet service using the specified
	 * application ID, language and service URI.
	 * @param appId your application ID
	 * @param language code for the language to use
	 * @param serviceURI base URI for GeoPlanet requests 
	 */
	public GeoPlanet(String appId, String language, String serviceURI) {
		this.appId = appId;	
		this.language = language; // TODO: Validate language
		this.serviceURI = serviceURI;
		this.httpClient = new HttpClient();
	}
	
	
	/**
	 * @return the language used by this client
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * @return the place corresponding to the specified WOE ID
	 * @throws PlaceNotFoundException if the ID is invalid
	 * @throws GeoPlanetException for general errors
	 */
	public Place getPlace(long woeId) throws GeoPlanetException {
		if (woeId < 0) throw new IllegalArgumentException("WOE IDs must be greater than 0");
		JSONObject place = doGet("/place/" + woeId, false);
		try {
			return new Place(this, place.getJSONObject("place"));
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
	 * @throws PlaceNotFoundException if there are no matches for the query
	 * @throws GeoPlanetException on general errors 
	 */
	public Place getPlace(String query) throws GeoPlanetException {
		List<Place> places = getPlaces(query).get(0,1);
		if (places.size() == 0) throw new PlaceNotFoundException();
		return places.get(0);
	}
	
	/**
	 * Returns a {@link PlaceCollection} of places whose names match the query
	 * to some extent.
	 * The query may include an country code to adjust the ordering e.g.
	 * e.g. <code>getPlaces("Edinburgh, UK")</code> vs. <code>getPlaces("Edinburgh, USA")</code>
	 * @return a {@link PlaceCollection} of places matching the query
	 */
	public PlaceCollection getPlaces(String query) {
		return new PlaceCollection(this, query);
	}
	
	JSONObject doGet(String path, boolean shortForm) throws GeoPlanetException, PlaceNotFoundException {
		assert path.startsWith("/");
		StringBuilder uri = new StringBuilder(serviceURI);
		uri.append(path);
		uri.append("?");
		uri.append("appid="); uri.append(appId);
		uri.append("&format=json");
		uri.append("&select="); uri.append(shortForm?"short":"long");
		uri.append("&lang="); uri.append(language);
		try {
			GetMethod get = new GetMethod(URIUtil.encodePathQuery(uri.toString()));
			httpClient.executeMethod(get);
			String response = get.getResponseBodyAsString();
			switch (get.getStatusCode()) {
			case 200:
				break;
			case 400:
				throw new InvalidAppIdException();
			case 404:
				throw new PlaceNotFoundException();
			default:
				throw new GeoPlanetException("Unexpected response from GeoPlanet server: " + get.getStatusLine());
			}
			return new JSONObject(response);
		}
		catch (HttpException e) {
			throw new GeoPlanetException(e);
		} 
		catch (JSONException e) {
			throw new GeoPlanetException(e);
		} 
		catch (IOException e) {
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
