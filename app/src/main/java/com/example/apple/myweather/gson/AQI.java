package com.example.apple.myweather.gson;

/**
 * Created by apple on 2018/6/3.
 */

public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
        public String qlty;
    }
}
