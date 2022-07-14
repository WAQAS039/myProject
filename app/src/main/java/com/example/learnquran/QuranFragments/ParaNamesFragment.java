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
import com.example.learnquran.Adopters.ParaNames_adopter;
import com.example.learnquran.Databases.Database;
import com.example.learnquran.Models.ParaName_Model;
import com.example.learnquran.R;

import java.util.ArrayList;

public class ParaNamesFragment extends Fragment {
    ListView ParaNameListView;
    Database database;
    ParaNames_adopter paraNames_adopter;
    ArrayList<ParaName_Model> ParaNamesArrayList;
    int ParaFirstAyahPosition;
    public ParaNamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_para_names, container, false);
        Init(view);
        ParaNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParaName_Model paraName_model = ParaNamesArrayList.get(position);
                int AyahPosition = paraName_model.getParaFirstAyahPosition();
                GotoAyah(AyahPosition);
            }
        });
        return view;
    }
    void Init(View view){
        ParaNameListView = view.findViewById(R.id.ParaListView);
        database = new Database(getContext());
        database.open();
        ParaNamesArrayList = new ArrayList<>();
        ParaNamesArrayList = database.GetAllParaName();
        paraNames_adopter = new ParaNames_adopter(getContext(),ParaNamesArrayList);
        ParaNameListView.setAdapter(paraNames_adopter);
    }

    void GotoAyah(int position){
        Intent intent = new Intent(getContext(), MainActivity.class);
        this.ParaFirstAyahPosition = position;
        intent.putExtra("index2",position);
        startActivity(intent);
    }
}