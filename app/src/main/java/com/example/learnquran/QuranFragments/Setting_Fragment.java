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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.example.learnquran.Activities.MainActivity;
import com.example.learnquran.R;

public class Setting_Fragment extends Fragment {
    Switch arabicSwitch, urduSwitch;
    SeekBar arabicSeekBar, translationSeekBar;
    Button btnSaveSetting;
    Boolean showArabic, showTranslation;
    int arabicFontSize, translationFontSize;
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
        arabicSwitch.setEnabled(true);
        eitherArabicOrTrans();
        arabicSwitch.setChecked(showArabic);
        urduSwitch.setChecked(showTranslation);
        arabicSeekBar.setProgress(arabicFontSize);
        translationSeekBar.setProgress(translationFontSize);
        arabicSeekBar.setMin(20);
        translationSeekBar.setMin(20);
        arabicSeekBar.setMax(100);
        translationSeekBar.setMax(100);
        btnSaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arabicSwitch.isChecked()) {
                    saveArabicStatus(true);
                }
                else {
                    saveArabicStatus(false);
                }
                if(urduSwitch.isChecked()) {
                    saveTransStatus(true);
                }
                else {
                    saveTransStatus(false);
                }
                getArabicStatus();
                getTransStatus();
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.putExtra("ShowArabic", showArabic);
                intent.putExtra("ShowTrans", showTranslation);
                intent.putExtra("ArabicSize", arabicFontSize);
                intent.putExtra("TransSize", translationFontSize);
                startActivity(intent);
            }
        });
        return view;
    }
    void Init(View view){
        arabicSwitch = view.findViewById(R.id.arabicTextSwitch);
        urduSwitch = view.findViewById(R.id.urduTextSwitch);
        btnSaveSetting = view.findViewById(R.id.saveSettingButton);
        arabicSeekBar = view.findViewById(R.id.ArabicseekBar);
        translationSeekBar = view.findViewById(R.id.TranslationseekBar);
    }

    public void getArabicStatus(){
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("Arabic", MODE_PRIVATE);
        showArabic = sharedPrefs.getBoolean("ShowArabic",true);
        arabicFontSize = sharedPrefs.getInt("ArabicFontSize",20);
    }
    public void getTransStatus(){
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("Translation", MODE_PRIVATE);
        showTranslation = sharedPrefs.getBoolean("ShowTranslation",true);
        translationFontSize = sharedPrefs.getInt("TranslationFontSize",20);
    }

    public  void saveArabicStatus(boolean IsCheckedForArabic){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Arabic", MODE_PRIVATE).edit();
        editor.putBoolean("ShowArabic", IsCheckedForArabic);
        editor.putInt("ArabicFontSize", arabicSeekBar.getProgress());
        editor.apply();
    }
    public  void saveTransStatus(boolean check){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Translation", MODE_PRIVATE).edit();
        editor.putBoolean("ShowTranslation",check);
        editor.putInt("TranslationFontSize", translationSeekBar.getProgress());
        editor.apply();
    }

    void eitherArabicOrTrans(){
        arabicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!urduSwitch.isChecked()){
                    arabicSwitch.setChecked(true);
                }
                urduSwitch.setEnabled(b);
            }
        });
        urduSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!arabicSwitch.isChecked()){
                    urduSwitch.setChecked(true);
                }
                arabicSwitch.setEnabled(b);
            }
        });
    }

}