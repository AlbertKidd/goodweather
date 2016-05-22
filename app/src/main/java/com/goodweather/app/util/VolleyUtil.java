package com.goodweather.app.util;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by niuwa on 2016/5/22.
 */
public class VolleyUtil {
    private static RequestQueue mRequestQueue = Volley.newRequestQueue(MyApplication.getContext());
    public static RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
}
