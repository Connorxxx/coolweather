package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyh on 2017/5/4.
 */

/*
"basic":{
"city":"City Name",
"id":"City Id",
"update":{
"loc":"天气更新时间"
}
}
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }

}