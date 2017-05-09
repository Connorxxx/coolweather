package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyh on 2017/5/4.
 */

   /*
   "now":{
   "tmp":"29",
   "cond":{
   "txt":"阵雨"
   }
   }
    */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
