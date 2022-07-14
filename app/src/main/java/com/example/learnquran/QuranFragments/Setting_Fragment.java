package com.example.learnquran.QuranFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import com.example.learnquran.Activities.MainActivity;
import com.example.learnquran.R;

public class Setting_Fragment extends Fragment {
    Switch ArabicSwitch,UrduSwitch;
    SeekBar ArabicSeekBar,TranslationSeekBar;
    Button SaveSetting;
    Boolean ShowArabic,ShowTranslation;
    int ArabicFontSize,TranslationFontSize;
    public Setting_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_, container, false);
        Init(view);
        getArabicStatus();
        getTransStatus();
        ArabicSwitch.setChecked(ShowArabic);
        UrduSwitch.setChecked(ShowTranslation);
        ArabicSeekBar.setProgress(ArabicFontSize);
        TranslationSeekBar.setProgress(TranslationFontSize);
        ArabicSeekBar.setMin(20);
        TranslationSeekBar.setMin(20);
        ArabicSeekBar.setMax(100);
        TranslationSeekBar.setMax(100);
        SaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ArabicSwitch.isChecked())
                    SaveArabicStatus(true);
                else
                    SaveArabicStatus(false);
                if(UrduSwitch.isChecked())
                    SaveTransStatus(true);
                else
                    SaveTransStatus(false);
                getArabicStatus();
                getTransStatus();
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.putExtra("ShowArabic",ShowArabic);
                intent.putExtra("ShowTrans",ShowTranslation);
                intent.putExtra("ArabicSize",ArabicFontSize);
                intent.putExtra("TransSize",TranslationFontSize);
                startActivity(intent);
            }
        });
        return view;
    }
    void Init(View view){
        ArabicSwitch = view.findViewById(R.id.arabicTextSwitch);
        UrduSwitch = view.findViewById(R.id.urduTextSwitch);
        SaveSetting = view.findViewById(R.id.saveSettingButton);
        ArabicSeekBar = view.findViewById(R.id.ArabicseekBar);
        TranslationSeekBar = view.findViewById(R.id.TranslationseekBar);
    }

    public void getArabicStatus(){
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("Arabic", MODE_PRIVATE);
        ShowArabic = sharedPrefs.getBoolean("ShowArabic",true);
        ArabicFontSize = sharedPrefs.getInt("ArabicFontSize",20);
    }
    public void getTransStatus(){
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("Translation", MODE_PRIVATE);
        ShowTranslation = sharedPrefs.getBoolean("ShowTranslation",true);
        TranslationFontSize = sharedPrefs.getInt("TranslationFontSize",20);
    }

    public  void SaveArabicStatus(boolean IsCheckedForArabic){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Arabic", MODE_PRIVATE).edit();
        editor.putBoolean("ShowArabic", IsCheckedForArabic);
        editor.putInt("ArabicFontSize",ArabicSeekBar.getProgress());
        editor.apply();
        editor.commit();
    }
    public  void SaveTransStatus(boolean check){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Translation", MODE_PRIVATE).edit();
        editor.putBoolean("ShowTranslation",check);
        editor.putInt("TranslationFontSize",TranslationSeekBar.getProgress());
        editor.apply();
        editor.commit();
    }
}