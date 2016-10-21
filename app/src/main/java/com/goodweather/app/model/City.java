package com.goodweather.app.model;

public class City {
	private String cityName;
	private String cityCode;
	private String provinceId;
	
	public String getCityName(){
		return cityName;
	}
	
	public void setCityName(String cityName){
		this.cityName = cityName;
	}
	
	public String getCityCode(){
		return cityCode;
	}
	
	public void setCityCode(String cityCode){
		this.cityCode = cityCode;
	}
	
	public String getProvinceId(){
		return provinceId;
	}
	
	public void setProvinceId(String provinceId){
		this.provinceId = provinceId;
	}
}
