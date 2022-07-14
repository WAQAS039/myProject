package com.example.learnquran.Adopters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnquran.Models.QuranText_Model;
import com.example.learnquran.Models.Urdu_Translation;
import com.example.learnquran.R;

import java.util.ArrayList;


public class QuranText_Adopter extends RecyclerView.Adapter<QuranText_Adopter.Holder> {
    Context context;
    ArrayList<QuranText_Model> arrayList;
    ArrayList<Urdu_Translation> urdu_translations;
    Holder holder;
    QuranText_Model quranText_model;
    Urdu_Translation urdu_translation;
    int index = -1;
    int recPos;
    boolean ShowQuranText,ShowTrans;
    int ArabicFOnt,TransFont;

    public QuranText_Adopter(Context context, ArrayList<QuranText_Model> arrayList, ArrayList<Urdu_Translation> urdu_translations) {
        this.context = context;
        this.arrayList = arrayList;
        this.urdu_translations = urdu_translations;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quran_text_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        quranText_model = arrayList.get(position);
        urdu_translation = urdu_translations.get(position);
        String id1 = String.valueOf(quranText_model.getSurahID());
        String id2 = convertToArabic(quranText_model.getVerseId());
        String Verseid = String.valueOf(quranText_model.getVerseId());
        if(!ShowQuranText){
            holder.txt.setText("");
            holder.Id1.setText("");
            holder.Id2.setText("");
        }else if(ShowQuranText){
            holder.txt.setText(quranText_model.getArabicText() + "﴿" +id2+"﴾");
            holder.Id1.setText("(" + id1 + ".");
            holder.Id2.setText(Verseid + ")");

        }
        holder.txt.setTextSize(ArabicFOnt);
        if(!ShowTrans){
            holder.translation.setText("");
            holder.TsId.setText("");
            holder.TvId.setText("");
        }else if(ShowTrans){
            holder.translation.setText(urdu_translation.getTranslation());
            holder.TsId.setText("(" + String.valueOf(urdu_translation.getSura()) + ".");
            holder.TvId.setText(String.valueOf(urdu_translation.getAya()) + ")");

        }
        holder.translation.setTextSize(TransFont);
        if(index == position) {
            holder.txt.setTextColor(Color.parseColor("#FFA500"));
        } else {
            holder.txt.setTextColor(Color.parseColor("#000000"));
        }
        this.holder = holder;
        this.recPos = position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    class Holder extends RecyclerView.ViewHolder {
        TextView txt,translation,Id1,Id2,TsId,TvId;
        public Holder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.QuranArabicTextView);
            Id1 = itemView.findViewById(R.id.SurahidTextView);
            Id2 = itemView.findViewById(R.id.VerseIdTextView);
            translation = itemView.findViewById(R.id.UrduTranslationTextVIew);
            TsId = itemView.findViewById(R.id.UrduTranslationSurahIdTextVIew);
            TvId = itemView.findViewById(R.id.UrduTranslationVerseIdTextView);
        }
    }
    public void selectedPosition(int position){
        if(position == RecyclerView.NO_POSITION) return;
        notifyItemChanged(index);
        notifyItemChanged(index);
        index = position;
    }

    public int GetPos(){
        return holder.getAdapterPosition();
    }

    public String convertToArabic(int value)
    {
        String newValue = (((((((((((value+"")
                .replaceAll("1", "١")).replaceAll("2", "٢"))
                .replaceAll("3", "٣")).replaceAll("4", "٤"))
                .replaceAll("5", "٥")).replaceAll("6", "٦"))
                .replaceAll("7", "٧")).replaceAll("8", "٨"))
                .replaceAll("9", "٩")).replaceAll("0", "٠"));
        return newValue;
    }

    // FOr Setting
    public void HideTextOrTranslation(boolean ArabicShow,boolean TransShow,int ArabicFont,int TransFont){
        this.ShowQuranText = ArabicShow;
        this.ShowTrans = TransShow;
        this.ArabicFOnt = ArabicFont;
        this.TransFont = TransFont;
    }
}
