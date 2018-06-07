package com.example.apple.myweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.example.apple.myweather.R;
import com.example.apple.myweather.gson.Weather;
import com.example.apple.myweather.util.Utility;

public class AutoAlarmService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hours =  60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + hours;
        Intent alarmIntent = new Intent(this,AutoAlarmService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,alarmIntent,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = "";
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String weatherString = prefs.getString("weather", null);
                if (weatherString != null) {
                    Weather weather = Utility.handleWeatherResponse(weatherString);
                    String weatherInfo = weather.now.more.info;
                    String weatherDegree = weather.now.temperature;
                    if(weatherInfo.contains("雨")){
                        message+="今天有雨，请记得带雨伞哦";
                    }else if(weatherInfo.contains("雪")){
                        message="天气寒冷，请注意保暖哦";
                    }else if(Integer.parseInt(weatherDegree)>=36){
                        message="天气炎热，请记得多喝水哦";
                    }else if(Integer.parseInt(weather.now.wind_sc)>=5){
                        message="有大风，请注意安全哦";
                    }else
                        message="天气美好，多出去走走哦";
                }
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                Notification notification = new NotificationCompat.Builder(getBaseContext())
                        .setContentTitle("我的天气温馨提醒您：")
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build();
                manager.notify(10,notification);
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hours = 2 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + hours;
        Intent alarmIntent = new Intent(this,AutoAlarmService.class);
        PendingIntent pi = PendingIntent.getService(this,0,alarmIntent,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this,AutoAlarmService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,alarmIntent,0);
        manager.cancel(pi);
    }
}
