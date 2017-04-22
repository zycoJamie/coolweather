package com.android.zycojamie.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zckya on 2017/4/21.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
