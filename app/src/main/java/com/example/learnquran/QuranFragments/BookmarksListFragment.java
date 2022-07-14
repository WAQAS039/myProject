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

import com.example.learnquran.Activities.MainActivity;
import com.example.learnquran.Adopters.BookmarkXList_Adopter;
import com.example.learnquran.Adopters.ItemClickSupport;
import com.example.learnquran.Databases.Database;
import com.example.learnquran.Models.BookMarkList_Model;
import com.example.learnquran.R;

import java.util.ArrayList;


public class BookmarksListFragment extends Fragment {
    RecyclerView BookmarkListRecycleView;
    Database database;
    BookmarkXList_Adopter bookmarkXList_adopter;
    ArrayList<BookMarkList_Model> BookmarkArrayList;
    int BookmarkedAyahPosition;
    ItemTouchHelper.SimpleCallback simpleCallback = ShowItemTouchHelper();
    public BookmarksListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks_list, container, false);
        Init(view);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(BookmarkListRecycleView);
        ItemClickSupport.addTo(BookmarkListRecycleView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                BookMarkList_Model bookMarkList_model = BookmarkArrayList.get(position);
                BookmarkedAyahPosition = bookMarkList_model.getAyahPosition();
                GotoAyah(BookmarkedAyahPosition);
            }
        });
        ShowItemTouchHelper();
        return view;
    }
    void Init(View view){
        BookmarkListRecycleView = view.findViewById(R.id.BookmarkListReycleView);
        database = new Database(getContext());
        database.open();
        BookmarkArrayList = new ArrayList<>();
        BookmarkArrayList = database.GetBookMarkList();
        bookmarkXList_adopter = new BookmarkXList_Adopter(getContext(),BookmarkArrayList);
        BookmarkListRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        BookmarkListRecycleView.setAdapter(bookmarkXList_adopter);
    }

    void GotoAyah(int position){
        Intent intent = new Intent(getContext(), MainActivity.class);
        this.BookmarkedAyahPosition = position;
        intent.putExtra("index3",position);
        startActivity(intent);
    }

    public ItemTouchHelper.SimpleCallback ShowItemTouchHelper(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                bookmarkXList_adopter.delete(viewHolder.getAdapterPosition());
            }
        };
        return simpleCallback;
    }
}