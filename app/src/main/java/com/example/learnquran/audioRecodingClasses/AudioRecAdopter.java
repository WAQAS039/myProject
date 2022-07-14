package com.example.learnquran.audioRecodingClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnquran.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class AudioRecAdopter extends RecyclerView.Adapter<AudioRecAdopter.Holder> {
    Context context;
    ArrayList<String> audioArrayList;

    public AudioRecAdopter(Context context, ArrayList<String> audioArrayList) {
        this.context = context;
        this.audioArrayList = audioArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.fileName.setText(audioArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return audioArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        MaterialTextView fileName;
        public Holder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.tvFileName);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void delete(int pos){
        audioArrayList.remove(pos);
        this.notifyDataSetChanged();
    }
}
