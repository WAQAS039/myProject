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
import com.example.learnquran.R;

import java.util.ArrayList;

public class BookmarkXList_Adopter extends RecyclerView.Adapter<BookmarkXList_Adopter.Holder> {
    Context context;
    ArrayList<BookMarkList_Model> arrayList;

    public BookmarkXList_Adopter(Context context, ArrayList<BookMarkList_Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookmark_list_layout_for_recycleview,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        BookMarkList_Model model = arrayList.get(position);
        holder.title.setText(model.getBookMarkTitle());
        holder.surahName.setText(model.getBookMarkedSurahName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView title,surahName;
        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.BookmarkTitleTextView);
            surahName = itemView.findViewById(R.id.BookmarkedSurahNameTextVIew);
        }
    }

    public void delete(int pos){
        BookMarkList_Model model = arrayList.get(pos);
        int position = model.getAyahPosition();
        Database database = new Database(context);
        database.open();
        String id = String.valueOf(position);
        database.DeleteBookMark(id);
        arrayList.remove(pos);
        this.notifyDataSetChanged();
    }

}
