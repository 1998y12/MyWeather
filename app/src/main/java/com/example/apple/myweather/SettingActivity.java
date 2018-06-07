package com.example.apple.myweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    MyButton myButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        myButton = (MyButton)findViewById(R.id.switch_button_update);
        myButton.setOnMbClickListener(new MyButton.OnMClickListener() {
            @Override
            public void onClick(boolean isRight) {
                if(isRight){
                    Toast.makeText(SettingActivity.this,"is Right",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(SettingActivity.this,"is Left",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
