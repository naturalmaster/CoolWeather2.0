package com.example.zimmerman.coolweatherv20;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orhanobut.logger.Logger;
import com.pgyersdk.crash.PgyCrashManager;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Zimmerman on 2017/4/13.
 */

public class MyApplication extends Application {

    public final static String API_KEY="b984c903b2714921bfae1f58b4420457";
    public final static String BING_PIC_URL = "https://bing.ioliu.cn/v1/rand?w=800&h=480";

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt("day",Calendar.getInstance().get(Calendar.DATE));
        editor.apply();
        //LitePal初始化
        LitePal.initialize(this);
        //Logger初始化
        Logger.init("asd").methodCount(3);
        //蒲公英初始化
        PgyCrashManager.register(this);
        context=this;
    }

    public static Context getContext() {
        return context;
    }
}
