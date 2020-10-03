package com.enigmaticdevs.dorkdiary.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.enigmaticdevs.dorkdiary.MainActivity;
import com.enigmaticdevs.dorkdiary.R;
import com.hanks.passcodeview.PasscodeView;

public class CheckPasscode extends AppCompatActivity {
    PasscodeView passcodeView;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_passcode);
        sharedPreferences=this.getSharedPreferences("com.enigmaticdevs.dorkdiary",MODE_PRIVATE);
        passcodeView = findViewById(R.id.check_passcode_view);
        passcodeView.setLocalPasscode(sharedPreferences.getString("passcode",""));
        passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
            }

            @Override
            public void onSuccess(String number) {
              Intent intent = new Intent(CheckPasscode.this, MainActivity.class);
              startActivity(intent);
              finishAffinity();
            }
        });
    }
}