package com.example.zimmerman.coolweatherv20.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Zimmerman on 2017/4/14.
 */

public class Weather {
    public String status;
    public Now now;
    public Suggestion suggestion;
    public Aqi aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
