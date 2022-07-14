package com.example.learnquran.QuranFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.learnquran.Activities.AllListActivity;
import com.example.learnquran.Databases.Database;
import com.example.learnquran.R;

public class NotesDetailsFragment extends Fragment {
    TextView NotesTitleData,SurahIdData,VerseIdData,VerseTextData,SurahNameData;
    EditText NoteEnglishTextData;
    Button BackButton,SaveButton;
    String NotesNameTxt;
    int SurahIdTxt,VerseIdTxt,NotesId;
    String VerseTextTxt;
    String NoteEngTxt;
    String SurahNameTxt;
    Database database;
    public NotesDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_details, container, false);
        Init(view);
        NotesId = getActivity().getIntent().getIntExtra("NotesIdDt",-1);
        NotesNameTxt = getActivity().getIntent().getStringExtra("NoteNameDt");
        SurahIdTxt = getActivity().getIntent().getIntExtra("SurahIdDt", -1);
        VerseIdTxt =  getActivity().getIntent().getIntExtra("VerseIdDt",-1);
        VerseTextTxt = getActivity().getIntent().getStringExtra("VerseTextDt");
        NoteEngTxt = getActivity().getIntent().getStringExtra("NotesDt");
        SurahNameTxt = getActivity().getIntent().getStringExtra("SurahNameDt");
        NotesTitleData.setText(NotesNameTxt);
        SurahIdData.setText("( " + SurahIdTxt + ".");
        VerseIdData.setText(VerseIdTxt + " )");
        VerseTextData.setText(VerseTextTxt);
        SurahNameData.setText(SurahNameTxt);
        NoteEnglishTextData.setText(NoteEngTxt);
        SetOnBackPress();
        return view;
    }

    void Init(View view){
        NotesTitleData = view.findViewById(R.id.NotesTitleNameTextView);
        SurahIdData = view.findViewById(R.id.SurahIDsTextView);
        VerseIdData = view.findViewById(R.id.VerseIDsTextView);
        VerseTextData = view.findViewById(R.id.AyahArabicTextViewForNotes);
        SurahNameData = view.findViewById(R.id.SurahNameTextViewForNotes);
        NoteEnglishTextData = view.findViewById(R.id.EnglishNotesTextEditText);
        BackButton = view.findViewById(R.id.BackBtn);
        SaveButton = view.findViewById(R.id.SaveBtn);
    }

    void SetOnBackPress(){
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllListActivity.class);
                intent.putExtra("NoteScreen",3);
                startActivity(intent);
            }
        });
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = new Database(getContext());
                database.open();
                if(NoteEngTxt.equals(NoteEnglishTextData.getText().toString()))
                {
                    // DO Nothing
                }
                else
                    database.UpdateNoteEnglishText(NoteEnglishTextData.getText().toString(),NotesId);
            }
        });
    }
}