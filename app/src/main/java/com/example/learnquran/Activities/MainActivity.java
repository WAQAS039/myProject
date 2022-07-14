package com.example.learnquran.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.learnquran.QuranFragments.QuranFragment;
import com.example.learnquran.R;

public class MainActivity extends AppCompatActivity {
    Button VideoButton,ListButton,SettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Intent intent = new Intent(MainActivity.this,OnlineMettingActivity.class);
                startActivity(intent);
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
    }

    public  void ShowQuranFragment(){
        Fragment fragment = new QuranFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.wholeQuranFrameLayout,fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}