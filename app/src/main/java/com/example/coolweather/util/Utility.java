package com.example.coolweather.util;

import android.text.TextUtils;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wyh on 2017/5/1.
 */

public class Utility {


    /**
     * j将返回的JSON数据解析成weather实体类
     *
     * 通过JSONObject和JSONArray将天气数据的主体内容解析出来，即如下内容：
     *      {
     *         "status":"ok",
     *         "basic":{},
     *         "aqi":{},
     *         "now":{},
     *         "suggestion":{},
     *         "daily_forecast":[]
     *      }
     */


    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    解析和处理服务器返回的省级数据
    使用JSONArray和JSONObject将数据解析出来，然后组装成实体对象，再调用save方法将数据存储到数据库中
     */

    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0 ; i < allProvince.length(); i++) {
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
        }
        return false;
    }

    /*
    解析和处理服务器返回的市级数据
     */

    public static boolean handleCityResponse(String response , int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0 ; i < allCities.length() ; i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    解析和处理服务器返回的县级数据
     */

    public static boolean handCountyResponse(String response , int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCointies = new JSONArray(response);
                for (int i = 0 ; i < allCointies.length() ; i++ ) {
                    JSONObject countyObject = allCointies.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
