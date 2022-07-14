package com.example.learnquran.Adopters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnquran.Databases.Database;
import com.example.learnquran.Models.BookMarkList_Model;
import com.example.learnquran.Models.Notes_Model;
import com.example.learnquran.R;

import java.util.ArrayList;

public class NotesList_Adopter extends RecyclerView.Adapter<NotesList_Adopter.Holder> {
    Context context;
    ArrayList<Notes_Model> arrayList;

    public NotesList_Adopter(Context context, ArrayList<Notes_Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_list_layout_for_recycleview,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Notes_Model model = arrayList.get(position);
        holder.NoteName.setText(model.getNotesName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView NoteName;
        public Holder(@NonNull View itemView) {
            super(itemView);
            NoteName = itemView.findViewById(R.id.NotesNameTextView);
        }
    }

    public void deleteNotes(int pos){
        Notes_Model model = arrayList.get(pos);
        int NotesId = model.getNoteId();
        Database database = new Database(context);
        database.open();
        String id = String.valueOf(NotesId);
        database.DeleteNotes(id);
        arrayList.remove(pos);
        this.notifyDataSetChanged();
    }
}
