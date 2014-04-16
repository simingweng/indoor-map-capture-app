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

    public static interface ExtraKeys {
        /**
         * user token obtained from the last successful sign in
         */
        public static final String USER_NAME = "com.bnj.indoormap.login.extra.USER_NAME";
        /**
         * user token obtained from the last successful sign in
         */
        public static final String USER_TOKEN = "com.bnj.indoormap.login.extra.USER_TOKEN";
    }

    public static interface ActivityRequestCode {

        public static final int LOGIN_REQUEST = 1;

    }

}
