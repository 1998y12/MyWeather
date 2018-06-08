package com.example.apple.myweather;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apple.myweather.broadcast.NeworkChangeReceiver;
import com.example.apple.myweather.gson.Forecast;
import com.example.apple.myweather.gson.Weather;
import com.example.apple.myweather.service.AutoAlarmService;
import com.example.apple.myweather.service.AutoUpdateService;
import com.example.apple.myweather.service.BGMusicService;
import com.example.apple.myweather.service.ForegroundService;
import com.example.apple.myweather.util.HttpUtil;
import com.example.apple.myweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdatedTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button navButton;

    private NeworkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;

    private TextView notificationNowDegree;
    private TextView notificationWeatherInfo;
    private TextView notificationWeatherDegree;

    private TextView flhum_info;
    private TextView pcpnpres_info;
    private TextView wind_info;
    private TextView vis_info;

    private TextView qltyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdatedTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);

        notificationNowDegree = (TextView)findViewById(R.id.notification_nowdegree);
        notificationWeatherDegree = (TextView)findViewById(R.id.notification_weatherdegree);
        notificationWeatherInfo = (TextView)findViewById(R.id.notification_weatherinfo);

        flhum_info = (TextView)findViewById(R.id.flhum_info);
        pcpnpres_info = (TextView)findViewById(R.id.pcpnpres_info);
        vis_info = (TextView)findViewById(R.id.vis_info);
        wind_info = (TextView)findViewById(R.id.wind_info);

        qltyText = (TextView)findViewById(R.id.qlty);

        //监听网络状态的广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NeworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
             mWeatherId = getIntent().getStringExtra("weather_id");

            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                stopService(new Intent(WeatherActivity.this, ForegroundService.class));
                stopService(new Intent(WeatherActivity.this,BGMusicService.class));
                requestWeather(mWeatherId);
            }
        });
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        startService(new Intent(this, AutoAlarmService.class));


    }

    /**
     * 根据天气id请求天气信息
     */
    public void requestWeather(final String weatherId){

//                  bc0418b57b2d4918819d3974ac1285d9
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=cf8d283b46ef440b973040c7e0e2ae70";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(NeworkChangeReceiver.status == 0)
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败！请检查您的网络设置！",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败！",Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }else{
                            if(NeworkChangeReceiver.status == 0)
                                Toast.makeText(WeatherActivity.this,"获取天气信息失败！请检查您的网络设置！",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(WeatherActivity.this,"获取天气信息失败！",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    /**
     * 处理并show Weather 实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = "最后更新："+weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdatedTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();


        for(Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView)view.findViewById(R.id.date_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxText = (TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView)view.findViewById(R.id.min_text);

            Log.d("MainActivity", forecast.date);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            qltyText.setText(weather.aqi.city.qlty);
        }
        String comfort = "舒适度："+ weather.suggestion.comfort.info;
        String carWash = "洗车指数："+ weather.suggestion.carWash.info;
        String sport = "运动建议："+ weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        String flhum = "体感温度：   " + weather.now.fl + "°" + "\n" + "湿度：  " + weather.now.hum + "%";
        String pcpnpres = "降水量：   " + weather.now.pcpn + "厘米" + "\n" + "气压：  " + weather.now.pres + "百帕";
        String wind = "风向：   " + weather.now.wind_dir + "" + "\n" + "风力：  " + weather.now.wind_sc + ""  + "\n" + "风速：  " + weather.now.wind_spd + "米/秒";
        String vis = "能见度：  "+weather.now.vis + "公里";
        flhum_info.setText(flhum);
        pcpnpres_info.setText(pcpnpres);
        wind_info.setText(wind);
        vis_info.setText(vis);

        weatherLayout.setVisibility(View.VISIBLE);

        startService(new Intent(this, ForegroundService.class));

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

        startService(new Intent(this,BGMusicService.class));



    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){

        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

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
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"This is my text");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,"Share to ..."));
                break;
            case R.id.remove_item:
                startActivityForResult(new Intent(this,SettingActivity.class),1);
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    String updatebutton_data = data.getStringExtra("updatebutton_return");
                    String alarmbutton_data = data.getStringExtra("alarmbutton_return");
                    if(updatebutton_data != null) {
                        if (updatebutton_data.equals("isRight")) {
                            startService(new Intent(this, AutoUpdateService.class));
                        } else if (updatebutton_data.equals("isLeft")) {
                            stopService(new Intent(this, AutoUpdateService.class));
                        }
                    }
                    if(alarmbutton_data != null) {
                        if (alarmbutton_data.equals("isRight")) {
                            startService(new Intent(this, AutoAlarmService.class));
                        } else if (alarmbutton_data.equals("isLeft")) {
                            stopService(new Intent(this, AutoAlarmService.class));

                        }
                    }

                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this,BGMusicService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this,BGMusicService.class));
    }

}
