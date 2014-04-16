/**
 *
 */
package com.bnj.indoortms.api.client.request;

import com.bnj.indoortms.api.client.model.Building;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.gson.GsonFactory;

/**
 * @author simingweng
 */
public class GetBuildingsRequest extends BaseRequest<Building[]> {

    private static final String url = "/api/buildings?usertoken=%s";
    private String username;
    private String usertoken;

    public GetBuildingsRequest(String username, String usertoken) {
        super(Building[].class);
        this.username = username;
        this.usertoken = usertoken;
    }

    @Override
    public Building[] loadDataFromNetwork() throws Exception {
        HttpRequest request = getHttpRequestFactory().buildGetRequest(
                new GenericUrl(host + String.format(url, usertoken)));
        request.setParser(GsonFactory.getDefaultInstance()
                .createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

    @Override
    public String getCacheKey() {
        return "buildings." + username;
    }

}
