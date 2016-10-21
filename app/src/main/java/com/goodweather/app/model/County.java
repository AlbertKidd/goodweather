package com.goodweather.app.model;

public class County {
	private String countyName;
	private String countyCode;
	private String cityId;
	
	public String getCountyName(){
		return countyName;
	}
	
	public void setCountyName(String countyName){
		this.countyName = countyName;
	}
	
	public String getCountyCode(){
		return countyCode;
	}
	
	public void setCountyCode(String countyCode){
		this.countyCode = countyCode;
	}
	
	public String getCityId(){
		return cityId;
	}
	
	public void setCityId(String cityId){
		this.cityId = cityId;
	}
}
