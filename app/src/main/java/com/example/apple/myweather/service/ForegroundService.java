package com.example.apple.myweather.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.apple.myweather.R;
import com.example.apple.myweather.WeatherActivity;
import com.example.apple.myweather.gson.Weather;
import com.example.apple.myweather.util.Utility;

public class ForegroundService extends Service {
    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
    }

    private boolean isActivityTop(Class cls,Context context){
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }
    private void showNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String dateTime = weather.basic.update.updateTime;
            String minTmp = weather.forecastList.get(0).temperature.min + "℃";
            String maxTmp = weather.forecastList.get(0).temperature.max + "℃";
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;

            Intent intent = new Intent(this, WeatherActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(),R.layout.notification);
            remoteViews.setTextViewText(R.id.notification_nowdegree,degree);
            remoteViews.setTextViewText(R.id.notification_weatherdegree,minTmp + " — " + maxTmp);
            remoteViews.setTextViewText(R.id.notification_weatherinfo,weatherInfo);
            remoteViews.setImageViewResource(R.id.notification_weatherImg,R.drawable.sun);

            Notification notification = new NotificationCompat.Builder(this)

                    .setCustomBigContentView(remoteViews)
//                    .setContentTitle(dateTime)
//                    .setContentText(degree + "  " + weatherInfo   )
//                    .setSubText(minTmp + " — " + maxTmp)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_MAX)

                    .build();
            startForeground(1, notification);
        }
    }
}
