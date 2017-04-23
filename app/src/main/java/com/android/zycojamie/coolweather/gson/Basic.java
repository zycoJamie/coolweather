package com.android.zycojamie.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zckya on 2017/4/21.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
