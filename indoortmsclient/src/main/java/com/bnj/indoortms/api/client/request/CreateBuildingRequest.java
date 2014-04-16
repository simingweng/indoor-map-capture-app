/**
 *
 */
package com.bnj.indoortms.api.client.request;

import com.bnj.indoortms.api.client.model.Building;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;

/**
 * @author simingweng
 */
public class CreateBuildingRequest extends BaseRequest<Building> {

    private static final String url = "/api/buildings?usertoken=%s";
    private Building newBuilding;
    private String usertoken;

    public CreateBuildingRequest(Building newBuilding, String usertoken) {
        super(Building.class);
        this.newBuilding = newBuilding;
        this.usertoken = usertoken;
    }

    @Override
    public String getCacheKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Building loadDataFromNetwork() throws Exception {
        HttpRequest request = getHttpRequestFactory().buildPostRequest(
                new GenericUrl(host + String.format(url, usertoken)),
                new JsonHttpContent(GsonFactory.getDefaultInstance(),
                        newBuilding));
        request.setParser(GsonFactory.getDefaultInstance()
                .createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

}
