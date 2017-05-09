package com.example.coolweather.gson;

/**
 * Created by wyh on 2017/5/4.
 */


    /*
    "aqi":{
    "city":{
    "aqi":"44",
    "pm25":"13"
    }
    }
     */

public class AQI {
    public AQICity city;
    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
