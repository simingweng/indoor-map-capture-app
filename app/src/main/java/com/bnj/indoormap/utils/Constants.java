/**
 *
 */
package com.bnj.indoormap.utils;

/**
 * @author simingweng
 */
public interface Constants {

    public static interface PrefsKeys {

        public static final String USER_NAME = "com.bnj.indoormap.login.username";

        public static final String USER_TOKEN = "com.bnj.indoormap.login.usertoken";

    }

    public static interface Login {
        /**
         * user token obtained from the last successful sign in
         */
        public static final String USER_NAME_EXTRA_KEY = "com.bnj.indoormap.login.extra" +
                ".USER_NAME_EXTRA_KEY";
        /**
         * user token obtained from the last successful sign in
         */
        public static final String USER_TOKEN_EXTRA_KEY = "com.bnj.indoormap.login.extra" +
                ".USER_TOKEN_EXTRA_KEY";
    }

    public static interface ActivityRequestCode {

        public static final int LOGIN_REQUEST = 1;
        public static final int CAPTURE_IMAGE = 2;
        public static final int GET_EXISTING_IMAGE = 3;
        public static final int GEO_REFERENCE = 4;

    }

    public static interface GoogleAPI {

        public static final String GOOLE_STATIC_MAP_IMAGE = "http://maps.googleapis" +
                ".com/maps/api/staticmap?center=%f,%f&zoom=18&size=540x480&markers=%f," +
                "%f&sensor=true&key=AIzaSyDpryIy62fGHzSSFjnYlsVTXTTWEm1aZ6c";

        public static final String GOOGLE_STREETVIEW_IMAGE = "http://maps.googleapis" +
                ".com/maps/api/streetview?size=540x480&location=%f," +
                "%f&sensor=true&key=AIzaSyDpryIy62fGHzSSFjnYlsVTXTTWEm1aZ6c";
    }

    public static interface PlaceSearch {
        public static final String SEARCH_PLACE_ACTION = "com.bnj.google.map.placesearch.library" +
                ".GET_PLACE";
        public static final String ADDRESS_EXTRA_KEY = "com.bnj.google.map.placesearch.library" +
                ".extra" +
                ".PLACE";
        public static final String PLACE_REFERENCE_EXTRA_KEY = "com.bnj.google.map.placesearch" +
                ".extra" +
                ".REFERENCE";
        public static final String INITIAL_LOCATION_EXTRA_KEY = "com.bnj.google.map.placesearch" +
                ".library.extra.INITIAL_LOCATION";
    }

    public static interface GeoReference {
        public static final String GCPS_EXTRA_KEY = "com.bnj.imagegeoreferencer.extra" +
                ".GCP_COLLECTION";
        public static final String BUILDING_LOCATION_EXTRA_KEY = "com.bnj.imagegeoreferencer" +
                ".extra.BUILDING_LOCATION";
        public static final String ACTION_GEO_REFERENCE = "com.bnj.imagegeoreferencer" +
                ".GEO_REFERENCE";
    }

    public static interface CreateNewFloor {
        public static final String BUILDING_ID_EXTRA_KEY = "com.bnj.indoormap.floor.extra" +
                ".BUILDING_ID";
    }

    public static interface Map {
        public static final String BUILDING_ID_EXTRA_KEY = "com.bnj.indoormap.map.extra" +
                ".BUILDING_ID";
        public static final String FLOOR_ID_EXTRA_KEY = "com.bnj.indoormap.map.extra.FLOOR_ID";
        public static final String BUILDING_LOCATION_EXTRA_KEY = "com.bnj.indoormap.map.extra" +
                ".BUILDING_LOCATION";
    }

}
