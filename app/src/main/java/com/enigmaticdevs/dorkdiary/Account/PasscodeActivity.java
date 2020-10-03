package com.enigmaticdevs.dorkdiary.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.widget.Toast;

import com.enigmaticdevs.dorkdiary.MainActivity;
import com.enigmaticdevs.dorkdiary.R;
import com.hanks.passcodeview.PasscodeView;

public class PasscodeActivity extends AppCompatActivity {
    PasscodeView passcodeView;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        sharedPreferences = this.getSharedPreferences("com.enigmaticdevs.dorkdiary", MODE_PRIVATE);
        passcodeView = findViewById(R.id.passcode_view);
        passcodeView.setPasscodeLength(4);
        if (!sharedPreferences.getString("passcode", "").isEmpty()) {
            passcodeView.setLocalPasscode(sharedPreferences.getString("passcode", ""));
            passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail() {
                    Toast.makeText(PasscodeActivity.this, "Incorrect Passcode", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String number) {
                    sharedPreferences.edit().putString("passcode","").apply();
                    Intent intent = new Intent(PasscodeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            });

        } else {
            passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail() {
                }

                @Override
                public void onSuccess(String number) {
                    sharedPreferences.edit().putString("passcode", number).apply();
                    Log.d("passcode", number);
                    Intent intent = new Intent(PasscodeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            });
        }
    }
}