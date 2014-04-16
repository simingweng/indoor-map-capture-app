/**
 *
 */
package com.bnj.indoortms.api.client.model;

import com.google.api.client.util.Key;

/**
 * @author simingweng
 */
public class Building {

    @Key
    private String _id;
    @Key
    private String name;
    @Key
    private String formatted_address;
    @Key
    private String reference;
    @Key
    private Location location;
    @Key
    private Floor[] floors;

    /**
     *
     */
    public Building() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the _id
     */
    public String get_id() {
        return _id;
    }

    /**
     * @param _id the _id to set
     */
    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the formatted_address
     */
    public String getFormatted_address() {
        return formatted_address;
    }

    /**
     * @param formatted_address the formatted_address to set
     */
    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the floors
     */
    public Floor[] getFloors() {
        return floors;
    }

    /**
     * @param floors the floors to set
     */
    public void setFloors(Floor[] floors) {
        this.floors = floors;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_id == null) ? 0 : _id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Building other = (Building) obj;
        if (_id == null) {
            if (other._id != null)
                return false;
        } else if (!_id.equals(other._id))
            return false;
        return true;
    }

}
