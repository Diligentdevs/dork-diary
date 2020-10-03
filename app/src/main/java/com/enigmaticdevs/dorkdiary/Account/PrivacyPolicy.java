package com.enigmaticdevs.dorkdiary.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.enigmaticdevs.dorkdiary.R;

public class PrivacyPolicy extends AppCompatActivity {
    WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        web =(WebView)findViewById(R.id.webview);
        web.loadUrl("file:///android_asset/privacypolicy.html");
    }
}