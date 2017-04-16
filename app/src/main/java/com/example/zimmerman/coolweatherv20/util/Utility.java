package com.example.zimmerman.coolweatherv20.util;

import android.text.TextUtils;

import com.example.zimmerman.coolweatherv20.db.City;
import com.example.zimmerman.coolweatherv20.db.County;
import com.example.zimmerman.coolweatherv20.db.Province;
import com.example.zimmerman.coolweatherv20.gson.Weather;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zimmerman on 2017/4/13.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            Logger.d("empty!");
            return false;
        }
        try {
            JSONArray allProvince = new JSONArray(response);
            for (int i =0;i<allProvince.length();i++) {
                JSONObject provinceObject = allProvince.getJSONObject(i);
                Province province = new Province();
                province.setProvinceName(provinceObject.getString("name"));
                province.setProvinceCode(provinceObject.getInt("id"));
                province.save();
            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){
        if (TextUtils.isEmpty(response)) return false;
        try {
            JSONArray allCity = new JSONArray(response);
            for (int i =0;i<allCity.length();i++) {
                JSONObject cityObject = allCity.getJSONObject(i);
                City city = new City();
                city.setCityName(cityObject.getString("name"));
                city.setCityCode(cityObject.getInt("id"));
                city.setProvinceCode(provinceId);
                city.save();
            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId) {
        if (TextUtils.isEmpty(response)) return false;
        try {
            JSONArray allCounty = new JSONArray(response);
            for (int i=0;i<allCounty.length();i++) {
                JSONObject object = allCounty.getJSONObject(i);
                County county = new County();
                county.setCityCode(cityId);
                county.setCountyCode(object.getInt("id"));
                county.setCountyName(object.getString("name"));
                county.setWeatherId(object.getString("weather_id"));
                county.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
