package com.example.learnquran.Adopters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learnquran.Models.ParaName_Model;
import com.example.learnquran.R;

import java.util.ArrayList;

public class ParaNames_adopter extends ArrayAdapter<ParaName_Model> {
    Context context;
    ArrayList<ParaName_Model> arrayList;
    public ParaNames_adopter(Context context,ArrayList<ParaName_Model> arrayLis) {
        super(context, R.layout.para_name_layout_for_listview,arrayLis);
        this.arrayList = arrayLis;
        this.context= context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ParaName_Model paraNameS = arrayList.get(position);
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.para_name_layout_for_listview,parent,false);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        holder.Ref(convertView);
        holder.para.setText(paraNameS.getParaName());
        return convertView;
    }
    class Holder{
        TextView para;
        void Ref(View v){
            para = v.findViewById(R.id.ParaArabicName);
        }
    }
}
