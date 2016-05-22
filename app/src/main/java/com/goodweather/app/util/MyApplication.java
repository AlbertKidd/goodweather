package com.goodweather.app.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by niuwa on 2016/5/21.
 */
public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext(){
        return sContext;
    }
}
