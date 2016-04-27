package com.goodweather.app.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.goodweather.app.db.GoodWeatherDB;
import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {
	//parse and handle province data back from server
	public synchronized static boolean handleProvincesResponse(GoodWeatherDB goodWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
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
	public synchronized static boolean handleCitiesResponse(GoodWeatherDB goodWeatherDB, String response, int provinceId){
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
	public synchronized static boolean handleCountiesResponse(GoodWeatherDB goodWeatherDB, String response, int cityId){
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
	
	//parse json data from server,and save it to db
	public static void handleWeatherResponse(Context context, String response){
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("retData");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("citycode");
			String temp1 = weatherInfo.getString("l_tmp");
			String temp2 = weatherInfo.getString("h_tmp");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("time");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	//save all weather info from server to sharedPreferences file
	public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyƒÍM‘¬d»’", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
