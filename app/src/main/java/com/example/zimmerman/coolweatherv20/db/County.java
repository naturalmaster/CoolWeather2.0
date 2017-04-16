package com.example.zimmerman.coolweatherv20.db;

import android.provider.ContactsContract;

import org.litepal.crud.DataSupport;

/**
 * Created by Zimmerman on 2017/4/13.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private int cityCode;
    private int countyCode;
    private String weatherId;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(int countyCode) {
        this.countyCode = countyCode;
    }

}
