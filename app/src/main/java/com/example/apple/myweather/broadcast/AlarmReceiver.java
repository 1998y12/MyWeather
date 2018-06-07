package com.example.apple.myweather.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.example.apple.myweather.R;
import com.example.apple.myweather.gson.Weather;
import com.example.apple.myweather.service.AutoAlarmService;
import com.example.apple.myweather.util.Utility;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String info = weather.now.more.info;
        }
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String message = "请注意";
        Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle("我的天气提醒您：")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
        manager.notify(10,notification);

        Intent i = new Intent(context, AutoAlarmService.class);
        context.startService(i);
    }
}
