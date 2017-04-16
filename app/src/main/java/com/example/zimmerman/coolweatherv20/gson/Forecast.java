package com.example.zimmerman.coolweatherv20.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zimmerman on 2017/4/14.
 */

public class Forecast {
    public Condition cond;
    public String date;
    public Temp tmp;

    //降水概率
    @SerializedName("pop")
    public String rainPossibility;
    @SerializedName("pcpn")
    public String rainQuantity;
    //相对湿度
    public String hum;

    public class Temp{
        public String max;
        public String min;
    }

    public class Condition{
        @SerializedName("code_d")
        public String dayCode;
        @SerializedName("code_n")
        public String nightCode;

        public String txt_d;

        public String txt_n;
    }
}
