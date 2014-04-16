/**
 *
 */
package com.bnj.indoortms.api.client.request;

import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

/**
 * @param <T>
 * @author simingweng
 */
public abstract class BaseRequest<T> extends GoogleHttpClientSpiceRequest<T> {

    protected String host = "http://10.66.96.103:3000";

    public BaseRequest(Class<T> clazz) {
        super(clazz);
    }

    public abstract String getCacheKey();

}