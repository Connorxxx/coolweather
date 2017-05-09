package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyh on 2017/5/4.
 */


/**
 * "daily_forecast:[
 * {
 *     "date":"2016-8-8",
 *     "cond":{
 *     "txt_d":"阵雨"
 *     },
 *     "tmp":{
 *         "max":"34"
 *         "min":"24"
 *     },
 *     {
 *         "date":"2016-8-9",
 *         "cond":{
 *         "txt_d":"多云"
 *         },
 *         "tmp":{
 *             "max":"35"
 *             "min":"29"
 *         }
 *     },
 *     ...
 * }
 * ]
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
