package com.example.zimmerman.coolweatherv20.gson;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zimmerman on 2017/4/14.
 */

public class Now {
    //温度
    public String tmp;
    @SerializedName("cond")
    public Condition condition;

    public Wind wind;
    //相对湿度
    public String hum;
    //体感温度
    @SerializedName("fl")
    public String bodyTmp;

    public class Condition{
        public int code;
        @SerializedName("txt")
        public String info;
    }

    public class Wind {
        public String deg;
        public String dir;
        @SerializedName("sc")
        public String windGrade;
        @SerializedName("spd")
        public String windSpeed;
    }
}
