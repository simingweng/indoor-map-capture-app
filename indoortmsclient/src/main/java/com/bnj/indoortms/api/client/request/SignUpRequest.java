/**
 *
 */
package com.bnj.indoortms.api.client.request;

import com.bnj.indoortms.api.client.model.User;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

/**
 * @author simingweng
 */
public class SignUpRequest extends BaseRequest<User> {

    private static final String url = "/api/signup";
    private User newUser;

    public SignUpRequest(String username, String password) {
        super(User.class);
        newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
    }

    @Override
    public User loadDataFromNetwork() throws IOException {
        HttpRequest request = getHttpRequestFactory().buildPostRequest(
                new GenericUrl(host + url),
                new JsonHttpContent(GsonFactory.getDefaultInstance(), newUser));
        request.setParser(GsonFactory.getDefaultInstance()
                .createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

    @Override
    public String getCacheKey() {
        return null;
    }

}
