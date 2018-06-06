package com.example.apple.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 2018/6/6.
 */

public class Hour {

    @SerializedName("time")
    public String time_hour;

    public String tmp;

    public String cond_code;

    public String cond_txt;

}
