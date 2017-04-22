package com.android.zycojamie.coolweather.util;

import android.text.TextUtils;

import com.android.zycojamie.coolweather.db.city;
import com.android.zycojamie.coolweather.db.county;
import com.android.zycojamie.coolweather.db.province;
import com.android.zycojamie.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zckya on 2017/4/19.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject jsonObject=allProvinces.getJSONObject(i);
                    province provinces=new province();
                    provinces.setProvinceName(jsonObject.getString("name"));
                    provinces.setProvinceCode(jsonObject.getInt("id"));
                    provinces.save();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject jsonObject=allCities.getJSONObject(i);
                    city cities=new city();
                    cities.setCityName(jsonObject.getString("name"));
                    cities.setCityCode(jsonObject.getInt("id"));
                    cities.setProvinceCode(provinceId);
                    cities.save();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties=new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject jsonObject=allCounties.getJSONObject(i);
                    county counties=new county();
                    counties.setCountyName(jsonObject.getString("name"));
                    counties.setWeatherId(jsonObject.getString("weather_id"));
                    counties.setCityId(cityId);
                    counties.save();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
