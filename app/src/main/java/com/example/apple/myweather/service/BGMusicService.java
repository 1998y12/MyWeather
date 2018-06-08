package com.example.apple.myweather.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.apple.myweather.R;
import com.example.apple.myweather.gson.Weather;
import com.example.apple.myweather.util.Utility;

import java.io.IOException;

public class BGMusicService extends Service {
    public BGMusicService() {
    }

    private MediaPlayer mp;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!mp.isPlaying()) {
            mp.start();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.release();
                return false;
            }
        });
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mp = new MediaPlayer();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if(weatherString!=null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherInfo = weather.now.more.info;
            if(weatherInfo.contains("雨")){
                mp = MediaPlayer.create(this,R.raw.rain);
            }else if(weatherInfo.contains("晴")){
                mp = MediaPlayer.create(this,R.raw.sunny);
            }else if(Integer.parseInt(weather.now.wind_sc)>=3){
                mp = MediaPlayer.create(this,R.raw.wind);
            }else
                mp = MediaPlayer.create(this,R.raw.other);

        }
        else
            mp = MediaPlayer.create(this,R.raw.other);

    }

    @Override
    public void onDestroy() {
        if(mp.isPlaying()){
            mp.stop();
        }
        mp.release();
        super.onDestroy();
    }

}
