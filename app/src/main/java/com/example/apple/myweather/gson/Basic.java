package com.example.apple.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 2018/6/3.
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




