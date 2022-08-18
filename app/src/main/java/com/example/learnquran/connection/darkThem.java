package com.example.learnquran.connection;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class darkThem extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
