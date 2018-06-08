package com.example.apple.myweather;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.apple.myweather.service.AutoUpdateService;

public class SettingActivity extends AppCompatActivity {

    //0在右边，1在左边
    public static int flag = 0;
    public static int flag1 = 0;

    MyButton myButton_refresh;
    MyButton myButton_alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = PreferenceManager.getDefaultSharedPreferences(this).getInt("updatebutton",0);
        flag1  = PreferenceManager.getDefaultSharedPreferences(this).getInt("alarmbutton",0);
        setContentView(R.layout.setting);
        final Intent intent = new Intent();
        myButton_refresh = (MyButton)findViewById(R.id.switch_button_update);
        myButton_alarm = (MyButton)findViewById(R.id.switch_button_autoalrm);
        if(flag == 1){
            myButton_refresh.goLeft();
        }
        if(flag1 == 1){
            myButton_alarm.goLeft();
        }

        myButton_refresh.setOnMbClickListener(new MyButton.OnMClickListener() {
            @Override
            public void onClick(boolean isRight) {
                if(isRight){
                   // Toast.makeText(SettingActivity.this,"is Right",Toast.LENGTH_SHORT).show();
                    Toast.makeText(SettingActivity.this, "开启后台天气自动更新", Toast.LENGTH_SHORT).show();
                    intent.putExtra("updatebutton_return","isRight");
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putInt("updatebutton",0);
                    editor.apply();
                }else {
                    Toast.makeText(SettingActivity.this, "关闭后台天气自动更新", Toast.LENGTH_SHORT).show();
                   // Toast.makeText(SettingActivity.this, "is Left", Toast.LENGTH_SHORT).show();
                    intent.putExtra("updatebutton_return", "isLeft");
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putInt("updatebutton", 1);
                    editor.apply();
                }
            }
        });

        myButton_alarm.setOnMbClickListener(new MyButton.OnMClickListener() {
            @Override
            public void onClick(boolean isRight) {
                if(isRight){
                    Toast.makeText(SettingActivity.this, "开启消息通知", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(SettingActivity.this,"is Right",Toast.LENGTH_SHORT).show();
                    intent.putExtra("alarmbutton_return","isRight");
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putInt("alarmbutton",0);
                    editor.apply();
                }else {
                    Toast.makeText(SettingActivity.this, "关闭消息通知", Toast.LENGTH_SHORT).show();
                   // Toast.makeText(SettingActivity.this, "is Left", Toast.LENGTH_SHORT).show();
                    intent.putExtra("alarmbutton_return", "isLeft");
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putInt("alarmbutton", 1);
                    editor.apply();
                }
            }
        });
        setResult(RESULT_OK,intent);


    }
}
