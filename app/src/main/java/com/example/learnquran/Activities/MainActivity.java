package com.example.learnquran.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnquran.QuranFragments.QuranFragment;
import com.example.learnquran.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    Button VideoButton,ListButton,SettingButton;
    AlertDialog networkDialog;
    QuranFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Init();
        ShowQuranFragment();
        ListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AllListActivity.class);
                startActivity(intent);
            }
        });
        VideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable(MainActivity.this)){
                    networkDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this,OnlineMettingActivity.class);
                    startActivity(intent);
                }else{
                    networkDialog.show();
                }
            }
        });
        SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AllListActivity.class);
                intent.putExtra("settingChoice",3);
                startActivity(intent);
            }
        });

    }
    void Init(){
        VideoButton = findViewById(R.id.VideoBtn);
        ListButton = findViewById(R.id.ListBtn);
        SettingButton = findViewById(R.id.SettingBtn);

        // network dialog
        networkDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("No Internet")
                .setMessage("Check your Connection")
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public  void ShowQuranFragment(){
        fragment = new QuranFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.wholeQuranFrameLayout,fragment)
                .commit();
    }

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}