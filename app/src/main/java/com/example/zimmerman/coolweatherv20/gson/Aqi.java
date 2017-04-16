package com.example.zimmerman.coolweatherv20.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zimmerman on 2017/4/14.
 */

public class Aqi {

    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
        @SerializedName("qlty")
        public String solution;
    }
}
