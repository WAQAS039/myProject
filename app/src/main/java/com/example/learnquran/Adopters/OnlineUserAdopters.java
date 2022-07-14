package com.example.learnquran.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;

public class OnlineUserAdopters extends RecyclerView.Adapter<OnlineUserAdopters.MyHolder> {
    ArrayList<OnlineUserModel> users;
    Context context;

    public OnlineUserAdopters(ArrayList<OnlineUserModel> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public OnlineUserAdopters.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.online_users_layour,parent,false);
        return new MyHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OnlineUserAdopters.MyHolder holder, int position) {
        OnlineUserModel model = users.get(position);
        if(model.getUser() != null) {
            holder.user.setText(model.getUser());
            if (model.getImageUri() != null) {
                Glide.with(context)
                        .load(model.getImageUri())
                        .error("error")
                        .into(holder.userPic);
            }
            if (!model.isMic()) {
                holder.mic.setImageResource(R.drawable.mic_off);
            } else {
                holder.mic.setImageResource(R.drawable.mic_on);
            }
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView user;
        ImageView mic,userPic;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user_name);
            mic = itemView.findViewById(R.id.isMic);
            userPic = itemView.findViewById(R.id.ivOnlineUserProfilePic);
        }
    }

    public void remove(int pos){
        users.remove(pos);
        notifyItemRemoved(pos);
    }
}
