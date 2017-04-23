package com.android.zycojamie.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.android.zycojamie.coolweather.gson.Weather;
import com.android.zycojamie.coolweather.util.HttpUtil;
import com.android.zycojamie.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public int onStartCommand(Intent intent,int flags,int startId){
        updateWeather();
        updateBingPic();
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int hour=8*60*60*1000;
        long time= SystemClock.elapsedRealtime()+hour;
        Intent intent1=new Intent(this,AutoUpdateService.class);
        PendingIntent pendingIntent=PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,pendingIntent);
        return super.onStartCommand(intent,flags,startId);

    }
    public void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bdd054fa1edf4d9b859b34d3426a0010";
            HttpUtil.sendOkHttpRequest(weatherUrl,new okhttp3.Callback(){
                public void onResponse(Call call, Response response)throws IOException {
                    String weatherString=response.body().string();
                    Weather weather=Utility.handleWeatherResponse(weatherString);
                    if(weather!=null && "ok".equals(weather.status)){
                        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                        SharedPreferences.Editor editor=prefs.edit();
                        editor.putString("weather",weatherString);
                        editor.apply();
                    }
                }
                public void onFailure(Call call,IOException e){
                    e.printStackTrace();
                }
            });
        }
    }
    public void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic,new okhttp3.Callback(){
            public void onResponse(Call call,Response response)throws IOException{
                String bingPic=response.body().string();
                SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                SharedPreferences.Editor editor=pref.edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
            public void onFailure(Call call,IOException e){
                e.printStackTrace();
            }
        });
    }
}
