package winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A Place is a spatial entity associated with a Yahoo! GeoPlanet WOE ID.
 * <p>
 * Yahoo! GeoPlanet provides information for about six million named places globally.
 * Coverage varies from country to country, but includes several hundred
 * thousand unique administrative areas with half a million variant names;
 * several thousand historical administrative areas; over two million unique
 * settlements and suburbs, and millions of unique postal codes covering about
 * 150 countries, plus a significant number of Points of Interest, Colloquial
 * Regions, Airports, Area Codes, Time Zones, and Islands.
 * </p>
 * @author Joe Halliwell <joe@winterwell.com>
 *
 */
public class Place extends GeoPlanetResource {

	private final long woeId;
	private final String name;
	private final PlaceType placeType;
	private final String placeTypeNameVariant;
	private String postal;
	private String locality1;
	private String locality2;
	private AdminRegion admin1;
	private AdminRegion admin2;
	private AdminRegion admin3;
	private Location centroid;
	private Location southWest;
	private Location northEast;
	
	/**
	 * Construct a place from a JSON representation
	 * @param woeid
	 * @throws GeoPlanetException 
	 * @throws JSONException 
	 */
	Place(GeoPlanet client, JSONObject place) throws GeoPlanetException {
		super(client);
		try {
			this.woeId = place.getLong("woeid");
			this.name = place.getString("name");
			
			// Sometimes the placeTypeName is not canonical
			placeTypeNameVariant = place.getString("placeTypeName");
			int placeTypeCode = place.getJSONObject("placeTypeName attrs").getInt("code");
			this.placeType = client.getPlaceType(placeTypeCode);
			if (!placeTypeNameVariant.equals(placeType.getName())) {
				System.out.println("Warning! '" + name + "' " +
					"claims to be '" + placeTypeNameVariant +"' " +
					"but is in fact '" + placeType.getName() + "'");
			}
			// Long fields
			if (!place.has("postal")) return;

			this.postal = place.getString("postal");
			this.locality1 = place.getString("locality1");
			this.locality2 = place.getString("locality2");

			this.admin1 = getAdminRegion(place, 1);
			this.admin2 = getAdminRegion(place, 2);
			this.admin3 = getAdminRegion(place, 3);

			this.centroid = new Location(place.getJSONObject("centroid"));
			JSONObject bbox = place.getJSONObject("boundingBox");
			this.southWest = new Location(bbox.getJSONObject("southWest"));
			this.northEast = new Location(bbox.getJSONObject("northEast"));
		} catch (JSONException e) {
			throw new GeoPlanetException(e);
		}
	}

	private AdminRegion getAdminRegion(JSONObject place, int i) throws JSONException {
		assert i >= 1 && i <= 3;
		String admin = place.getString("admin" + i);
		if (admin.equals("")) return null;
		return new AdminRegion(getClient(), place, "admin" + i);
	}
	
	/**
	 * Places may contain more or less detail depending on how they were
	 * arrived at. It is possible to retrieve the long
	 * @return true, if long form; false otherwise
	 */
	public boolean isLongForm() {
		return (centroid != null);
	}
	
	/**
	 * @return long form version of this place
	 * @throws GeoPlanetException
	 */
	public Place getLongForm() throws GeoPlanetException {
		if (isLongForm()) return this;
		return getClient().getPlace(woeId);
	}
	
	/**
	 * @return the WOE ID of this place
	 */
	public long getWoeId() {
		return woeId;
	}
	
	/**
	 * @return the name of this place e.g. "London"
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the place type e.g. "Country"
	 */
	public PlaceType getPlaceType() {
		return placeType;
	}

	/**
	 * Retrieve a variant name for the place type.
	 * This is usually identical to the name returned by {@link PlaceType#getName()}
	 * but not always. For example "Aland Islands" has a country code, but type name "Province"
	 * 
	 * @return the place type name.
	 */
	public String getPlaceTypeNameVariant() {
		return placeTypeNameVariant;
	}
	
	public String getLocality1() {
		return locality1;
	}

	public String getLocality2() {
		return locality2;
	}

	public Location getCentroid() {
		return centroid;
	}

	public Location getSouthWest() {
		return southWest;
	}

	public Location getNorthEast() {
		return northEast;
	}

	public String getPostal() {
		return postal;
	}
		
	/**
	 * One of the primary administrative areas within a country.
	 * Place type names associated with this place type include: State, Province,
	 * Prefecture, Country, Region, Federal District. 
	 * @return the first admin area. May be null.
	 */
	public AdminRegion getAdmin1() {
		return admin1;
	}
	
	/**
	 * One of the secondary administrative areas within a country. Place type
	 * names associated with this place type include: County, Province, Parish,
	 * Department, District. 
	 * @return the second admin area. May be null.
	 */	
	public AdminRegion getAdmin2() {
		return admin2;
	}
	
	/**
	 * One of the tertiary administrative areas within a country. Place type
	 * names associated with this place type include: Commune, Municipality,
	 * District, Ward. 
	 * @return the third admin area. May be null.
	 */
	public AdminRegion getAdmin3() {
		return admin3;
	}
	
	/**
	 * Get the parent of this place: its direct superior in the hierarchy.
	 * For example, California (WOEID 2347563) is a child of the United
	 * States (WOEID 23424977), and conversely the United States is the
	 * parent of California. In this version of GeoPlanet, places have 
	 * only one parent.
	 * @return the parent of this place
	 * @throws GeoPlanetException
	 */
	public Place getParent() throws GeoPlanetException {
		StringBuilder uri = new StringBuilder("/place/");
		uri.append(woeId);
		uri.append("/parent");
		JSONObject parent = getClient().doGet(uri.toString(), false);
		try {
			return new Place(getClient(), parent.getJSONObject("place"));
		} catch (JSONException e) {
			throw new GeoPlanetException(e);
		}
	}

	/**
	 * The direct inferiors to a given place. Children can be of different
	 * place types, so the children of California (WOEID 2347563) include its
	 * 58 counties, as well as its colloquial entities (High Sierra, Wine
	 * Country, Central Valley, etc.), and Zones (MSA Redding, MSA Salinas, etc.). 
	 * @return the children of this place
	 */
	public PlaceCollection getChildren() {
		return new PlaceCollection(this, "children");
	}
	
	/**
	 * Places adjacent to a given place. For example, California (WOEID 2347563)
	 * is adjacent to Nevada (WOEID 2347587), Oregon (WOEID 2347596), Arizona
	 * (WOEID 2347561), and Baja California in Mexico (WOEID 2346265); these are
	 * all neighbors of California. 
	 * @return the neighbors of this place
	 */
	public PlaceCollection getNeighbors() {
		return new PlaceCollection(this, "neighbors");
	}
	
	/**
	 * Places that share the same parent and have the same place type.
	 * For example, California has 50 siblings: the other 49 states,
	 * plus the District of Columbia.
	 * @return the siblings of this place.
	 */
	public PlaceCollection getSiblings() {
		return new PlaceCollection(this, "siblings");
	}
	
	/**
	 * Places in the chain of parents for a given place. These are ordered
	 * from smallest to largest.
	 * For example, San Jose (WOEID 2488042) is a child of Santa Clara County
	 * (WOEID 12587712), which in turn is a child of California (WOEID 2347563),
	 * which is in turn a child of the United States (WOEID 23424977).
	 * Santa Clara County, California, and the United States are all
	 * ancestors of San Jose
	 * @return the ancestors of this place
	 */
	public PlaceCollection getAncestors() {
		return new PlaceCollection(this, "ancestors");
	}
	
	/**
	 * Returns a collection of places that have a place as a child or
	 * descendant (child of a child, etc). The resources in the collection
	 * are short representations of each place (unless a long representation
	 * is specifically requested). 
	 * @return the belongtos of this place
	 */
	public PlaceCollection getBelongTos() {
		return new PlaceCollection(this, "belongtos");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (woeId ^ (woeId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Place other = (Place) obj;
		if (woeId != other.woeId)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Place [name=" + name + ", " +
					  "placeTypeName=" + placeTypeNameVariant + ", " + 
					  "placeType=" + placeType + ", " +
					  "woeId=" + woeId + "]";
	}


}
