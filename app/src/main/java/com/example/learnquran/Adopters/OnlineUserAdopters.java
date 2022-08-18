package com.example.learnquran.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.learnquran.Models.OnlineUserModel;
import com.example.learnquran.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OnlineUserAdopters extends RecyclerView.Adapter<OnlineUserAdopters.MyHolder> {
    ArrayList<OnlineUserModel> users;
    private final StorageReference mStorageReference;
    Context context;

    public OnlineUserAdopters(ArrayList<OnlineUserModel> users, StorageReference mStorageReference, Context context) {
        this.users = users;
        this.mStorageReference = mStorageReference;
        this.context = context;
    }

    @NonNull
    @Override
    public OnlineUserAdopters.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.online_users_layour,parent,false);
        return new MyHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull OnlineUserAdopters.MyHolder holder, int position) {
        OnlineUserModel model = users.get(position);
        if(model.getUser() != null) {
            holder.user.setText(model.getFullName());
            if(position == 0)
                holder.user.setText(model.getFullName() + " (You) ");
            if (!model.isMic()) {
                holder.mic.setImageResource(R.drawable.mic_off);
            } else {
                holder.mic.setImageResource(R.drawable.mic_on);
            }
            if(model.getRole() != -1){
            if(model.getRole() == 1){
                holder.role.setVisibility(View.VISIBLE);
                holder.role.setText("Qari");
            }else{
                holder.role.setVisibility(View.GONE);
            }}
        }
        // Get User Profile pic
        StorageReference imagePath = mStorageReference.child("image/"+model.getUser());
        if (model.getImageUri() == null) {
            if(context != null){
                imagePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(context)
                            .load(uri)
                            .error("error")
                            .into(holder.userPic);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView user,role;
        ImageView mic,userPic;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user_name);
            mic = itemView.findViewById(R.id.isMic);
            userPic = itemView.findViewById(R.id.ivOnlineUserProfilePic);
            role = itemView.findViewById(R.id.clientRole);
        }
    }

    public void remove(int pos){
        users.remove(pos);
        notifyItemRemoved(pos);
    }

}
