package com.enigmaticdevs.dorkdiary.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.enigmaticdevs.dorkdiary.R;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences=this.getSharedPreferences("com.enigmaticdevs.dorkdiary",MODE_PRIVATE);
        if(sharedPreferences.getString("passcode","").isEmpty()){
             i = new Intent(SplashActivity.this, StartupActivity.class);
        }
        else{
            i = new Intent(SplashActivity.this, CheckPasscode.class);
        }
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over

                startActivity(i);
                finish();
            }
        }, 700);
    }
    }
