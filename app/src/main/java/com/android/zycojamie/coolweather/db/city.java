package com.android.zycojamie.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zckya on 2017/4/19.
 */

public class city extends DataSupport{
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceCode;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getCityName(){
        return cityName;
    }
    public void setCityName(String cityName){
        this.cityName=cityName;
    }
    public int getCityCode(){
        return cityCode;
    }
    public void setCityCode(int cityCode){
        this.cityCode=cityCode;
    }
    public void setProvinceCode(int provinceCode){
        this.provinceCode=provinceCode;
    }
    public int getProvinceCode(){
        return provinceCode;
    }
}
