package com.example.learnquran.Adopters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learnquran.Models.SurahNames_Model;
import com.example.learnquran.R;

import java.util.ArrayList;


public class Surah_Names_adopter extends ArrayAdapter<SurahNames_Model> {
    Context context;
    ArrayList<SurahNames_Model> arrayList;
    public Surah_Names_adopter(Context context, ArrayList<SurahNames_Model> arrayList) {
        super(context, R.layout.surah_name_layout_for_listview,arrayList);
        this.arrayList= arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SurahNames_Model surahNamesModel = arrayList.get(position);
        Holder holder;
        if(convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.surah_name_layout_for_listview, parent, false);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        holder.Ref(convertView);
        holder.english.setText(surahNamesModel.getEnglishName());
        holder.Arabic.setText(surahNamesModel.getArabicName());
        return convertView;
    }

    class Holder {
        TextView english, Arabic;
        void Ref(View convertView) {
            english = convertView.findViewById(R.id.SurahEnglishName);
            Arabic = convertView.findViewById(R.id.SurahArabicName);
        }
    }
}
