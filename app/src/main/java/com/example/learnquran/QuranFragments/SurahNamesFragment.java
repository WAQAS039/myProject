package com.example.learnquran.QuranFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.learnquran.Activities.MainActivity;
import com.example.learnquran.Adopters.Surah_Names_adopter;
import com.example.learnquran.Databases.Database;
import com.example.learnquran.Models.SurahNames_Model;
import com.example.learnquran.R;

import java.util.ArrayList;

public class SurahNamesFragment extends Fragment {
    ListView SurahNameListView;
    Database database;
    Surah_Names_adopter surah_names_adopter;
    ArrayList<SurahNames_Model> SurahNameArrayList;
    int SurahFirstAyahPosition;
    public SurahNamesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surah_names, container, false);
        Init(view);
        SurahNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SurahNames_Model surahNames_model = SurahNameArrayList.get(position);
                int SurahPos = surahNames_model.getPosition();
                GotoAyah(SurahPos);
            }
        });
        return view;
    }
    void Init(View view){
        SurahNameListView = view.findViewById(R.id.SurahListView);
        database = new Database(getContext());
        database.open();
        SurahNameArrayList = new ArrayList<>();
        SurahNameArrayList = database.GetAllSurahName();
        surah_names_adopter = new Surah_Names_adopter(getContext(),SurahNameArrayList);
        SurahNameListView.setAdapter(surah_names_adopter);
    }

    void GotoAyah(int position){
        Intent intent = new Intent(getContext(), MainActivity.class);
        this.SurahFirstAyahPosition = position;
        intent.putExtra("index",position);
        startActivity(intent);
    }
}