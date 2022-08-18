package com.example.learnquran.QuranFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.learnquran.Activities.MainActivity;
import com.example.learnquran.R;


public class AllListFragment extends Fragment {
    Button SurahButton,ParaButton,NotesListButton,BookmarksListButton,BackButton,AudioListButton;
    int choice;
    public AllListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_list, container, false);
        Init(view);
        SwitchFragment(1);
        choice = getActivity().getIntent().getIntExtra("NoteScreen", -1);
        if(choice != -1)
            SwitchFragment(3);
        else
            SwitchFragment(1);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        SurahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchFragment(1);
            }
        });
        ParaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchFragment(2);
            }
        });
        NotesListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchFragment(3);
            }
        });
        BookmarksListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchFragment(4);
            }
        });
        AudioListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchFragment(5);
            }
        });
        return view;
    }
    void Init(View view){
        SurahButton = view.findViewById(R.id.SurahBtn);
        ParaButton = view.findViewById(R.id.ParaBtn);
        NotesListButton = view.findViewById(R.id.NotesListBtn);
        BookmarksListButton = view.findViewById(R.id.BookmarkListBtn);
        BackButton = view.findViewById(R.id.BackButton);
        AudioListButton = view.findViewById(R.id.AudioListBtn);
    }
    void SwitchFragment(int choice){
        Fragment fragment = null;
        switch (choice){
            case 1:
               fragment = new SurahNamesFragment();
                break;
            case 2:
                fragment = new ParaNamesFragment();
                break;
            case 3:
                fragment = new NotesListFragment();
                break;
            case 4:
                fragment = new BookmarksListFragment();
                break;
            case 5:
                fragment = new AudioListFragment();
        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.AllListFrameLayout,fragment)
                .commit();
    }
}