package com.example.learnquran.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.learnquran.QuranFragments.AllListFragment;
import com.example.learnquran.QuranFragments.NotesDetailsFragment;
import com.example.learnquran.QuranFragments.Setting_Fragment;
import com.example.learnquran.R;

public class AllListActivity extends AppCompatActivity {
    int choice = -1;
    int SettingChoice = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);
        getSupportActionBar().hide();
        choice = getIntent().getIntExtra("choice",-1);
        SettingChoice = getIntent().getIntExtra("settingChoice",-1);
        if(choice == -1 && SettingChoice == -1)
            SwitchFragment(1);
        else if(SettingChoice == 3)
            SwitchFragment(3);
        else
            SwitchFragment(2);

    }
    void SwitchFragment(int choice){
        Fragment fragment = null;
        switch (choice){
            case 1:
                fragment = new AllListFragment();
                break;
            case 2:
                fragment = new NotesDetailsFragment();
                break;
            case 3:
                fragment = new Setting_Fragment();
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.AllListActivityFrameLayout,fragment)
                .commit();
    }

}