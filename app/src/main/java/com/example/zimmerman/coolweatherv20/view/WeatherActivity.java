package com.example.zimmerman.coolweatherv20.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zimmerman.coolweatherv20.MyApplication;
import com.example.zimmerman.coolweatherv20.R;
import com.example.zimmerman.coolweatherv20.gson.Forecast;
import com.example.zimmerman.coolweatherv20.gson.Weather;
import com.example.zimmerman.coolweatherv20.util.HttpUtil;
import com.example.zimmerman.coolweatherv20.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Zimmerman on 2017/4/16.
 */

public class WeatherActivity extends AppCompatActivity {

    private TextView cityTitle;

    private TextView updateTime;

    private ScrollView scrollView;

    //目前天气
    private TextView presentTemprature;
    private TextView bodyTemprature;
    private TextView weatherInfo;

    //未来天气
    private LinearLayout futureLayout;

    //空气指数
    private TextView aqiValue;
    private TextView pm25;

    //生活建议
    private TextView comfortBar;
    private TextView sportBar;
    private TextView shineBar;
    private TextView dressBar;
    private TextView fluBar;

    private ImageView backImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);
        init();

        setBingBackground();
        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pres.getString("weather",null);
        if (weatherString!=null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            scrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    public void setBingBackground() {
        int today = Calendar.getInstance().get(Calendar.DATE);
        //本来计划根据日期号来更换背景的，因无法缓存，暂时不换
        Glide.with(this).load(MyApplication.BING_PIC_URL).into(backImg);
    }

    //根据天气ID查询天气信息，并存入SharedPreference 缓存
    public void requestWeather(String weatherId) {
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="+weatherId+"&key="+ MyApplication.API_KEY;
        HttpUtil.sendHttpdRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //将信息填入各个天气控件
    public void showWeatherInfo(Weather weather){
        cityTitle.setText(weather.basic.cityName);
        //改变日期显示格式
        SimpleDateFormat formatFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format1To = new SimpleDateFormat("HH:mm");
        try {
            Date d = formatFrom.parse(weather.basic.update.updateTime);
            updateTime.setText(format1To.format(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        presentTemprature.setText(weather.now.tmp+"℃");
        bodyTemprature.setText("体感温度："+weather.now.bodyTmp+"℃");
        weatherInfo.setText(weather.now.condition.info);

        for (Forecast forecast:weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.item_forecast_constrain,null);
            TextView date = (TextView) view.findViewById(R.id.item_forecast_date);;
            TextView info = (TextView) view.findViewById(R.id.item_forecast_info);
            TextView minTmp = (TextView) view.findViewById(R.id.item_forecast_tmp_min);
            TextView maxTmp = (TextView) view.findViewById(R.id.item_forecast_tmp_max);
            TextView rainRate = (TextView) view.findViewById(R.id.item_forecast_rain_rate);

            date.setText(forecast.date);
            info.setText(forecast.cond.txt_d);
            minTmp.setText(forecast.tmp.min);
            maxTmp.setText(forecast.tmp.max);
            rainRate.setText("降水概率："+forecast.rainPossibility+"%");

            futureLayout.addView(view);
        }
        aqiValue.setText(weather.aqi.city.aqi);
        pm25.setText(weather.aqi.city.pm25);

        comfortBar.setText("舒适度："+weather.suggestion.comf.txt);
        sportBar.setText("运动指数："+weather.suggestion.sport.txt);
        shineBar.setText("防晒指数："+weather.suggestion.uv.txt);
        dressBar.setText("穿衣指南："+weather.suggestion.drsg.txt);
        fluBar.setText("感冒指数："+weather.suggestion.flu.txt);
        scrollView.setVisibility(View.VISIBLE);

    }

    private void init() {
        backImg = (ImageView) findViewById(R.id.bing_pic_background);
        scrollView = (ScrollView) findViewById(R.id.weather_scrollview);
        cityTitle = (TextView) findViewById(R.id.weather_title_text);
        updateTime = (TextView) findViewById(R.id.weather_info_update_time);

        presentTemprature = (TextView) findViewById(R.id.present_weather_tmp);
        bodyTemprature = (TextView) findViewById(R.id.present_weather_body_tmp);
        weatherInfo = (TextView) findViewById(R.id.present_weather_info);

        futureLayout = (LinearLayout) findViewById(R.id.daily_future_forecast);

        aqiValue = (TextView) findViewById(R.id.aqi_value);
        pm25 = (TextView) findViewById(R.id.pm25_value);

        comfortBar = (TextView) findViewById(R.id.comfor_degree);
        sportBar = (TextView) findViewById(R.id.sport_degree);
        shineBar = (TextView) findViewById(R.id.shine_degree);
        dressBar = (TextView) findViewById(R.id.dress_degree);
        fluBar = (TextView) findViewById(R.id.flu_degree);


    }


}
