package com.example.zimmerman.coolweatherv20.gson;

/**
 * Created by Zimmerman on 2017/4/14.
 */

public class Suggestion {

    public Comfort comf;
    public Dress drsg;
    public Flu flu;
    public Shine uv;
    public Travel trav;
    public Sport sport;

    //舒适指数
    public class Comfort {
        //简述
        public String brf;

        public String txt;
    }
    public class Travel{
        //简述
        public String brf;

        public String txt;
    }
    public class Sport{
        //简述
        public String brf;

        public String txt;
    }
    //穿衣指数
    public class Dress{
        //简述
        public String brf;

        public String txt;
    }

    //感冒指数
    public class Flu{
        //简述
        public String brf;

        public String txt;
    }

    //防晒指数
    public class Shine{
        //简述
        public String brf;

        public String txt;
    }
}
