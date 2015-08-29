package com.winterwell.jgeoplanet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An administrative region such as: State, Country, County, Province, District, Ward.
 * These are not the same as place types.
 *
 * @author Joe Halliwell <joe@winterwell.com>
 */
public class AdminRegion extends GeoPlanetResource {

    private final String name;
    private final String type;
    private final String code;

    AdminRegion(GeoPlanet client, JSONObject place, String name) throws JSONException {
        super(client);
        this.name = place.getString(name);
        String attrName = name + " attrs";
        JSONObject attrs = place.getJSONObject(attrName);
        String code = attrs.getString("code");
        this.code = (code.equals("") ? null : code);
        this.type = attrs.getString("type");
    }

    /**
     * Returns the name of this administrative region.
     *
     * @return the name of this administrative region.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of place this administrative region is.
     *
     * @return The placeType of this administrative region e.g. "Country"
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a short code for the region e.g. "IT" for Italy. This
     * is <em>not</em the same as the (numeric) codes retrieved by {@link PlaceType#getCode()}.
     * May be null if there is no known short code.
     *
     * @return A short code for the region. May be null.
     */
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "AdminRegion [code=" + code + ", name=" + name + ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AdminRegion other = (AdminRegion) obj;
        if (code == null) {
            if (other.code != null) {
                return false;
            }
        } else if (!code.equals(other.code)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}