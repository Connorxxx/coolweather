package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wyh on 2017/5/4.
 */


     /**
      * "suggestion":{
      *     "comf":{
      *         "txt":"白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。"
      *     },
      *     "cw":{
      *         "txt":"不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"
      *     },
      *     "sport:"{
      *         "txt":"有降水，且风力较强，气压较低，推荐您在室内进行低强度运动；若坚持户外运动，须注意避雨防风。"
      *     }
      * }
      */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;


    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}
