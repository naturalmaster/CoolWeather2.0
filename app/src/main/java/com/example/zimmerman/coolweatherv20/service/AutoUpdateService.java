package com.example.zimmerman.coolweatherv20.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.example.zimmerman.coolweatherv20.MyApplication;
import com.example.zimmerman.coolweatherv20.gson.Weather;
import com.example.zimmerman.coolweatherv20.util.HttpUtil;
import com.example.zimmerman.coolweatherv20.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Zimmerman on 2017/4/16.
 */

public class AutoUpdateService extends Service {
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
       updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString !=null) {
            final String weatherId = prefs.getString("weather_id",null);
            String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=" + MyApplication.API_KEY;
            HttpUtil.sendHttpdRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                 e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather!= null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
}
