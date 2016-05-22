package com.goodweather.app.util;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by niuwa on 2016/5/21.
 */
public class MyStringRequest extends StringRequest {

    public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(method, url, listener, errorListener);
    }

    public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(url, listener, errorListener);
    }

    @Override
    public Response<String> parseNetworkResponse(NetworkResponse response){
        String parsed;
        try {
            parsed = new String(response.data, "UTF-8");
        }catch (UnsupportedEncodingException e){
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
