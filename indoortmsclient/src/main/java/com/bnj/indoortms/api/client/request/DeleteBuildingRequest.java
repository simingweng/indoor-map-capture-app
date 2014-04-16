/**
 *
 */
package com.bnj.indoortms.api.client.request;

import com.bnj.indoortms.api.client.model.Building;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

/**
 * @author simingweng
 */
public class DeleteBuildingRequest extends BaseRequest<Building> {

    private static final String url = "/api/buildings/%s";
    private String buildingId;

    public DeleteBuildingRequest(String buildingId) {
        super(Building.class);
        this.buildingId = buildingId;
    }

    @Override
    public String getCacheKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Building loadDataFromNetwork() throws IOException {
        HttpRequest request = getHttpRequestFactory().buildDeleteRequest(
                new GenericUrl(host + String.format(url, buildingId)));
        request.setParser(GsonFactory.getDefaultInstance()
                .createJsonObjectParser());
        return request.execute().parseAs(getResultType());
    }

}
