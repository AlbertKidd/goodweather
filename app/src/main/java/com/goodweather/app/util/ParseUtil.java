package com.goodweather.app.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.goodweather.app.db.GoodWeatherDB;
import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseUtil {

    private static RequestQueue mRequestQueue;

	//parse and handle province data back from server
	public synchronized static boolean handleProvincesResponse(GoodWeatherDB goodWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces.length > 0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.SetProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					goodWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	//parse and handle city data back from server
	public synchronized static boolean handleCitiesResponse(GoodWeatherDB goodWeatherDB, String response, String provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");
			if(allCities != null && allCities.length > 0){
				for(String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					goodWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	//parse and handle county data back from server
	public synchronized static boolean handleCountiesResponse(GoodWeatherDB goodWeatherDB, String response, String cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c : allCounties){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					goodWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

    public synchronized static String handleCountyCodeResponse(String response){
        if(!TextUtils.isEmpty(response)){
            String[] array = response.split("\\|");
            return array[1];
        }else
            return null;
    }
	
	//parse json data from server,and save it to db
	public static void handleWeatherResponse(Context context, JSONObject response){


		try{
			JSONObject forecast = response.getJSONObject("forecast");
            //String city = forecast.getString("city");
			String weatherCode = forecast.getString("cityid");
			String weather1 = forecast.getString("weather1");
            String weather2 = forecast.getString("weather2");
            String weather3 = forecast.getString("weather3");
            String temp1 = forecast.getString("temp1");
            String temp2 = forecast.getString("temp2");
            String temp3 = forecast.getString("temp3");

            JSONObject realTime = response.getJSONObject("realtime");
			String time = realTime.getString("time");
            String temp = realTime.getString("temp");
            String weather = realTime.getString("weather");

            JSONObject aqi = response.getJSONObject("aqi");
            String pm25 = aqi.getString("pm25");
            String pm10 = aqi.getString("pm10");

			SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(context).edit();

            editor.putBoolean("city_selected", true);
            //editor.putString("city", city);
            editor.putString("weatherCode", weatherCode);
            editor.putString("weather1", weather1);
            editor.putString("weather2", weather2);
            editor.putString("weather3", weather3);
            editor.putString("temp1", temp1);
            editor.putString("temp2", temp2);
            editor.putString("temp3", temp3);
            editor.putString("time", time);
            editor.putString("temp", temp);
            editor.putString("weather", weather);
            editor.putString("pm25", pm25);
            editor.putString("pm10", pm10);
            editor.apply();
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

}
