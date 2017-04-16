package com.example.zimmerman.coolweatherv20.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zimmerman on 2017/4/14.
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
