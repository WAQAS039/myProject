package com.example.learnquran.QuranFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnquran.Activities.AllListActivity;
import com.example.learnquran.Adopters.ItemClickSupport;
import com.example.learnquran.Adopters.NotesList_Adopter;
import com.example.learnquran.Databases.Database;
import com.example.learnquran.Models.Notes_Model;
import com.example.learnquran.R;

import java.util.ArrayList;


public class NotesListFragment extends Fragment {
    RecyclerView NotesListRecycleView;
    Database database;
    NotesList_Adopter notesList_adopter;
    ArrayList<Notes_Model> NotesArrayList;
    ItemTouchHelper.SimpleCallback simpleCallback = ShowItemTouchHelper();
    public NotesListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);
        Init(view);
        SetOnClick();
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(NotesListRecycleView);
        return view;
    }
    void Init(View view){
        NotesListRecycleView = view.findViewById(R.id.NotesListRecycleView);
        database = new Database(getContext());
        database.open();
        NotesArrayList = new ArrayList<>();
        NotesArrayList = database.GetNotesNameAndNotesDetails();
        notesList_adopter = new NotesList_Adopter(getContext(), NotesArrayList);
        NotesListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        NotesListRecycleView.setAdapter(notesList_adopter);
    }
    void SetOnClick(){
        ItemClickSupport.addTo(NotesListRecycleView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getContext(), AllListActivity.class);
                intent.putExtra("choice",2);
                Notes_Model notes_model = NotesArrayList.get(position);
                int NotesId = notes_model.getNoteId();
                String NotesName = notes_model.getNotesName();
                int SurahIdDataForDetailScreen = notes_model.getSuraId();
                int VerseIdDataForDetailScreen = notes_model.getVerseId();
                String VerseTextDataForDetailScreen = notes_model.getVerseText();
                String NoteDataForDetailScreen = notes_model.getNote();
                String SurahNameDataForDetailScreen = notes_model.getSurahName();
                intent.putExtra("NotesIdDt",NotesId);
                intent.putExtra("NoteNameDt",NotesName);
                intent.putExtra("SurahIdDt",SurahIdDataForDetailScreen);
                intent.putExtra("VerseIdDt",VerseIdDataForDetailScreen);
                intent.putExtra("VerseTextDt",VerseTextDataForDetailScreen);
                intent.putExtra("NotesDt",NoteDataForDetailScreen);
                intent.putExtra("SurahNameDt",SurahNameDataForDetailScreen);
                startActivity(intent);
            }
        });
    }

    public ItemTouchHelper.SimpleCallback ShowItemTouchHelper(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                notesList_adopter.deleteNotes(viewHolder.getAdapterPosition());
            }
        };
        return simpleCallback;
    }
}