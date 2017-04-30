package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by wyh on 2017/5/1.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;  //县名字
    private String countyWeather;  //县所对应天气id
    private int cityId;  //当前所属市的id

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

    public String getCountyWeather() {
        return countyWeather;
    }

    public void setCountyWeather(String countyWeather) {
        this.countyWeather = countyWeather;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
