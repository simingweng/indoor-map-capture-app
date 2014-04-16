/**
 *
 */
package com.bnj.indoortms.api.client.model;

import com.google.api.client.util.Key;

/**
 * @author simingweng
 */
public class User {

    @Key
    private String username;
    @Key
    private String password;
    @Key
    private String token;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

}
