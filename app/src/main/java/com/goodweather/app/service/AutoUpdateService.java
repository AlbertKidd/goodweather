package com.goodweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.goodweather.app.util.ParseUtil;

import org.json.JSONObject;

public class AutoUpdateService extends Service{
	private RequestQueue mRequestQueue;

	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		new Thread(new Runnable(){
			@Override
			public void run(){
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 60*60*1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
	
	//update weather info
	private void updateWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weatherCode", "");
        String address = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=" + weatherCode;
        mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(address, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ParseUtil.handleWeatherResponse(AutoUpdateService.this, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(jsonObjectRequest);
	}
}
