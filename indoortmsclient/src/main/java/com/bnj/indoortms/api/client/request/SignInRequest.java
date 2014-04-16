/**
 *
 */
package com.bnj.indoortms.api.client.request;

import com.bnj.indoortms.api.client.model.User;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;

/**
 * @author simingweng
 */
public class SignInRequest extends BaseRequest<User> {

    private static final String url = "/api/user_token";
    private User user;

    public SignInRequest(String username, String password) {
        super(User.class);
        user = new User();
        user.setUsername(username);
        user.setPassword(password);
    }

    @Override
    public User loadDataFromNetwork() throws Exception {
        HttpRequest request = getHttpRequestFactory().buildPostRequest(
                new GenericUrl(host + url),
                new JsonHttpContent(GsonFactory.getDefaultInstance(), user));
        request.setParser(GsonFactory.getDefaultInstance()
                .createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

    @Override
    public String getCacheKey() {
        return null;
    }

}
