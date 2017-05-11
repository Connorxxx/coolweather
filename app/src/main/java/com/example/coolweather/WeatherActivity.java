package com.example.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.gson.Basic;
import com.example.coolweather.db.County;
import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
//import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
//import com.example.coolweather.gson.Weather;
public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;  //天气滚动布局实例
    private TextView titleCity;   //城市标题
    private TextView titleUpdateTime;   //更新时间
    private TextView degreeText;   //气温
    private TextView weatherInfoText;  //天气概况
    private LinearLayout forecastLayout;   //预报布局
    private TextView aqiText;  //AQI指数
    private TextView pm25Text;  //PM2.5指数
    private TextView comfortText;   //舒适度
    private TextView carWashText;   //洗车建议
    private TextView sportText;   //运动建议
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;  //用于记录城市天气的id
    public DrawerLayout drawerLayout;
    private Button navButton;



    /*
    先获取控件的实例
    先尝试从本地缓存中读取天气数据
    没缓存就会从Intent中读取天气id并调用requestWeather方法来从服务器请求天气数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        如果SDK大于等于21
        调用getWindow().getDecorView();拿到当前活动的DecorView
        再调用DecorView的setSystemUiVisibility方法改变系统UI显示
         */
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //这两个参数说明系统活动布局会显示在状态栏上面
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//下拉颜色


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null) {
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;  //定义变量，用于记录城市天气的id
            showWeatherInfo(weather);
        }else {
            //无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");//weather
            weatherLayout.setVisibility(View.INVISIBLE);//请求数据时将ScrollView隐藏
            requestWeather(mWeatherId);
        }
        //设置下拉监听器，调用requestWeather方法请求天气id
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);//打开滑动菜单
            }
        });

        //加载必应每日一图
        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
    }

    /**
     * 根据天气id请求服务器中城市天气信息
     * requestWeather方法中使用参数传入的天气id和申请好的APIKey拼装出一个接口地址
     * 接着 调用HttpUtil.sendOkHttpRequest方法向该地址发出请求，服务器会将相应的城市天气信息以JSON格式返回
     * 之后在onResponse回调中先调用Utility.handleWeatherResponse方法将将返回的JSON数据转换成weather对象
     * 再将当前线程切回主线程，进行判断，如果服务器返回的status状态时ok，说明请求成功，将返回的数据缓存到SharedPreferences-
     * -，并调用showWeatherInfo方法来进行内容显示
     */

    public void requestWeather(final String weatherId){

        String weatherUri = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=062c32f560c447a8a9b2e15c7135af35";
        HttpUtil.sendOkHttpRequest(weatherUri, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                        }
                        //结束下拉，隐藏下拉进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });


            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
        loadBingPic();
    }






    /**
     * 处理并展示Weather实体类中的数据
     * 从Weather对象中获取数据，然后在控件中显示出来
     * 在未来几天天气预报部分，使用for循环来处理每天的天气信息
     * 在循环中 动态加载forecast_item.xml文件布局设置相应数据，然后添加到父布局当中
     */

    private void showWeatherInfo(Weather weather){

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout,false);
            TextView dateText= (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);

        }
        String comfor = "舒适度：：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfor);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);//处理完之后将 ScrollView 重新变为可见
        //启动AutoUpdateService服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /*
    加载必应每日一图
    使用HttpUtil.sendOkHttpRequest方法获取并应每日一图链接
    然后将链接缓存到SharedPreferences中，然后将线程切回主线程使用Glide加载图片

     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);

                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }


        });
    }
}
