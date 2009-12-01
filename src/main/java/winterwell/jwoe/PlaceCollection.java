package winterwell.jwoe;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A collection of {@link Place} objects.
 * <p>
 * A {@link PlaceCollection} represents a <em>potential</em> collection of places
 * rather than the collection of places themselves.
 * </p>
 * <p>
 * Use {@link #get()} and friends to get hold of the actual Places involved.
 * </p>
 * <p>
 * PlaceCollection returns Places in long form by default. Use {@link #shortForm(boolean)}
 * to change this behaviour.
 * </p>
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class PlaceCollection {

	WhereOnEarth client;
	Place base;
	/** Can be a test string to search for, or a relation e.g. children, belongtos **/
	String query;
	String type;
	boolean useShortForm = false;
	int total = -1;
	
	PlaceCollection(WhereOnEarth client, String query) {
		this.client = client;
		this.query = query;
	}
	
	PlaceCollection(Place place, String relation) {
		this.client = place.client;
		this.base = place;
		this.query = relation;
	}
	
	PlaceCollection(PlaceCollection other) {
		this.client = other.client;
		this.base = other.base;
		this.query = other.query;
		this.useShortForm = other.useShortForm;
		this.total = other.total;
	}
	
	/**
	 * Set the type filter.
	 * 
	 * <p>
	 * May be null to unset the type. Example usage:
	 * <pre>
	 * Place earth = woe.getPlace(1);
	 * List<Place> countries = earth.getChildren().type("Country").get();
	 * </pre>
	 * </p>
	 * 
	 * <p>
	 * Valid place types include "County", "Region", "Town" and "Ward"
	 * </p>
	 * @param placeTypeName The place type to filter on. May be null to unset the type.
	 */
	public PlaceCollection type(String placeTypeName) {
		// TODO: Validate the type
		if (placeTypeName.equals(this.type)) return this;
		PlaceCollection variant;
		variant = new PlaceCollection(this);
		variant.type = placeTypeName;
		variant.total = -1;
		return variant;

	}

	public String getType() {
		return this.type;
	}
	
	/**
	 * Return short form places from this query. The default is to use long form.
	 * @see #isShortForm()
	 * @param useShortForm
	 */
	public PlaceCollection shortForm(boolean useShortForm) {
		if (useShortForm == this.useShortForm) return this;
		PlaceCollection variant;
		variant = new PlaceCollection(this);
		variant.useShortForm = useShortForm;
		return variant;
	}
	/**
	 * @return true if this place collection will return short form Places; false otherwise
	 */
	public boolean isShortForm() {
		return useShortForm;
	}
	
	private void appendType(StringBuilder sb) {
		assert type != null;
		sb.append(".type(");
		sb.append(type);
		sb.append(")");
	}
	
	private void appendQuery(StringBuilder sb) {
		assert query != null;
		sb.append(".q("); 
		sb.append(query);
		sb.append(")");
	}
	
	/**
	 * Returns the total number of places in this collection if a get() has occurred, or -1
	 * to indicate that no get has occurred.
	 * @return the total number of place in this collection if known, or -1
	 */
	public int size() {
		return total;
	}
	
	/**
	 * Get a list of (some of) the places contained in this collection.
	 * Requires network access. Returns an empty list if no results were found.
	 * @param start The first result to get indexed from 0
	 * @param count The maximum number of results to return. Zero (0) returns all results.
	 * @throws WhereOnEarthException for general errors
	 */
	public List<Place> get(int start, int count) throws WhereOnEarthException {
		if (start < 0) throw new IllegalArgumentException("start parameter must be >= 0");
		if (count < 0) throw new IllegalArgumentException("count parameter must be >= 0");
		assert count >=0 ;
		StringBuilder uri = new StringBuilder();
		if (base == null) {
			uri = new StringBuilder("/places");
			if (type != null && query != null) {
				uri.append("$and(");
				appendQuery(uri);
				uri.append(",");
				appendType(uri);
				uri.append(")");
			}
			else if (query != null) {
				appendQuery(uri);
			}
			else if (type != null) {
				appendType(uri);
			}
		}
		else {
			uri = new StringBuilder("/place/");
			uri.append(base.getWoeId());
			uri.append("/");
			uri.append(query); // A relation in this case...
			if (type != null) {
				appendType(uri);
			}
		}
		// Matrix parameters
		uri.append(";start="); uri.append(start);
		uri.append(";count="); uri.append(count);
		// Results
		JSONObject tmp;
		tmp = client.doGet(uri.toString(), useShortForm);
		return processResults(tmp);
	}
	
	/**
	 * Get all places in this collection.
	 * Cosmetic method calling <code>get(0,0)</code>.
	 * Requires network access.
	 * @throws WhereOnEarthException for general errors
	 */
	public List<Place> get() throws WhereOnEarthException {
		return get(0, 0);
	}
	
	/**
	 * Get a specific place from this collection.
	 * Cosmetic method calling <code>get(index, 1).get(0)</code>
	 * Not usually used with index != 0. Requires network access.
	 * @throws WhereOnEarthException for general errors
	 * @throws ArrayIndexOutOfBoundsException for invalid indices
	 */
	public Place get(int index) throws WhereOnEarthException {
		return get(index, 1).get(0);
	}
	
	private List<Place> processResults(JSONObject tmp) throws WhereOnEarthException {
		try {
			tmp = tmp.getJSONObject("places");
			total = tmp.getInt("total");
			if (total == 0) return new ArrayList<Place>(0);
			
			int start = tmp.getInt("start");
			int count = tmp.getInt("count");
			assert start >= 0;
			assert (count >= 0);
			assert (start + count) <= total;
			List<Place> results = new ArrayList<Place>(count);
			JSONArray array = tmp.getJSONArray("place");
			for (int i = 0; i<count; i++) {
				results.add(new Place(client, array.getJSONObject(i)));
			}
			return results;
		} catch (JSONException e) {
			throw new WhereOnEarthException(e);
		}
	}
}
