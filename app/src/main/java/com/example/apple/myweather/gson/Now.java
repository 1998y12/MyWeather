package com.example.apple.myweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple on 2018/6/3.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;

        @SerializedName("code")
        public String infocode;
    }

    public String fl;
    public String hum;
    public String pcpn;
    public String pres;
    public String vis;
    public String wind_deg;
    public String wind_dir;
    public String wind_sc;
    public String wind_spd;
}
