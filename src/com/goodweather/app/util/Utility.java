package com.goodweather.app.util;

import com.goodweather.app.db.GoodWeatherDB;
import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;

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
}
