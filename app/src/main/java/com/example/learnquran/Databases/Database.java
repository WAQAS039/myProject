package com.example.learnquran.Databases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.learnquran.Models.AyahPosition_Model;
import com.example.learnquran.Models.BookMarkList_Model;
import com.example.learnquran.Models.Notes_Model;
import com.example.learnquran.Models.ParaName_Model;
import com.example.learnquran.Models.QuranText_Model;
import com.example.learnquran.Models.SurahNames_Model;
import com.example.learnquran.Models.Urdu_Translation;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class Database extends SQLiteAssetHelper {
    Context context;
    public static final String name = "Quran.db";
    public static final int version = 1;

    //Surah Name Table
    public static final String SurahId = "Id";
    public static final String SurahName_Table = "Surah";
    public static final String ArabicName = "ArabicName";
    public static final String TransliterationName = "TransliterationName";
    public static final String position = "SuratFirstAyahPosition";

    //Parah Name Table
    public static final String ParaTb = "Parah";
    public static final String ParahNames = "ParahNames";
    public static final String ParahFirstAyahPosition = "ParahFirstAyahPosition";

    //Table BookMarks
    public static final String BookmarksTb = "BookMarksList";
    public static final String BookMarkId = "BookMarkID";
    public static final String AyahPosition = "AyahPosition";
    public static final String SurahName = "SurahName";
    public static final String BookMarkTitle = "BookMarkTitle";

    //Table QuranText
    public static final String ArabicText_Table = "ArabicText";
    public static final String VerseText = "VerseText";
    public static final String SuraId = "SuraId";
    public static final String VerseId = "VerseId";

    //Table QuranTranslation
    public static final String ui_maududi = "ur_maududi";
    public static final String text = "text";
    public static final String Transura = "sura";
    public static final String aya = "aya";

    // Table Notes
    public static final String NoteTb = "Notes";
    public static final String NoteName = "NotesName";
    public static final String AyahText = "AyahText";
    public static final String Note = "Note";
    public static final String SuratName = "SuratName";
    public static final String AyahTranslation = "AyahTranslation";
    public static final String ayah = "AyahText";
    public static final String NotesId = "NotesId";
    SQLiteDatabase database;
    Cursor cursor;
    public Database(Context context) {
        super(context, name, null, version);
        this.context = context;
    }
    public void open(){
        this.database = getWritableDatabase();
    }

    public void close(){
        if (database != null) {
            this.database.close();
        }
    }


    public void AddBookMarks(int position,String Surah,String title){
        ContentValues cv = new ContentValues();
        cv.put(AyahPosition,position);
        cv.put(SurahName,Surah);
        cv.put(BookMarkTitle,title);
        long i = database.insert(BookmarksTb,null,cv);
    }

    public void DeleteBookMark(String id){
        long i = database.delete(BookmarksTb,AyahPosition + " = ?",new String[] {id});
    }
    public void DeleteNotes(String NoteIds){
        long i = database.delete(NoteTb,NotesId + " = ?",new String[] {String.valueOf(NoteIds)});
    }
    @SuppressLint("Range")
    public ArrayList<SurahNames_Model> GetAllSurahName(){
        ArrayList<SurahNames_Model> arrayList = new ArrayList<>();
        String qry = "SELECT * FROM "+ SurahName_Table;
        cursor = database.rawQuery(qry,null);
        if (cursor.moveToFirst()){
            do {
                String Arabic;
                String English;
                int pos = cursor.getInt(cursor.getColumnIndex(position));
                English = cursor.getString(cursor.getColumnIndex(TransliterationName));
                Arabic = cursor.getString(cursor.getColumnIndex(ArabicName));
                arrayList.add(new SurahNames_Model(Arabic,English,pos));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }
    @SuppressLint("Range")
    public String GetSpecificSurahName(int id){
        String Arabic = "";
        String qry = "SELECT * FROM "+ SurahName_Table + " Where Id = " + id;
        cursor = database.rawQuery(qry,null);
        if (cursor.moveToFirst()){
            do {
                Arabic = cursor.getString(cursor.getColumnIndex(ArabicName));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return Arabic;
    }

    @SuppressLint("Range")
    public ArrayList<ParaName_Model> GetAllParaName(){
        ArrayList<ParaName_Model> arrayList = new ArrayList<>();
        String qry = "SELECT * FROM "+ ParaTb;
        cursor = database.rawQuery(qry,null);
        if (cursor.moveToFirst()){
            do {
                int position = cursor.getInt(cursor.getColumnIndex(ParahFirstAyahPosition));
                String names = cursor.getString(cursor.getColumnIndex(ParahNames));
                arrayList.add(new ParaName_Model(names,position));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<QuranText_Model> GetQuranText(){
        String arbitext;
        int SId;
        int VId;
        ArrayList<QuranText_Model> arrayList = new ArrayList<>();
        String qry = "Select * from " + ArabicText_Table;
        cursor = database.rawQuery(qry,null);
        if(cursor.moveToFirst()){
            do {
                SId = cursor.getInt(cursor.getColumnIndex(SuraId));
                VId = cursor.getInt(cursor.getColumnIndex(VerseId));
                arbitext = cursor.getString(cursor.getColumnIndex(VerseText));
                arrayList.add(new QuranText_Model(SId,VId,arbitext));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<Urdu_Translation> GetQuranTran(){
        ArrayList<Urdu_Translation> arrayList = new ArrayList<>();
        String qry = "Select * from " + ui_maududi;
        cursor = database.rawQuery(qry,null);
        if(cursor.moveToFirst()){
            do {
                int s = cursor.getInt(cursor.getColumnIndex(Transura));
                int a = cursor.getInt(cursor.getColumnIndex(aya));
                String translation = cursor.getString(cursor.getColumnIndex(text));
                arrayList.add(new Urdu_Translation(translation,s,a));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<AyahPosition_Model> AyahPositionFromBookmarksList(){
        ArrayList<AyahPosition_Model> arrayList= new ArrayList<>();
        String qry = "select * from " + BookmarksTb;
        cursor = database.rawQuery(qry,null);
        if(cursor.moveToFirst()){
            do {
                int i = cursor.getInt(cursor.getColumnIndex(AyahPosition));
                arrayList.add(new AyahPosition_Model(i));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }
    @SuppressLint("Range")
    public ArrayList<BookMarkList_Model> GetBookMarkList(){
        ArrayList<BookMarkList_Model> arrayList = new ArrayList<>();

        String qry = "select * from " + BookmarksTb   + " order by " + BookMarkId + " Desc";
        cursor = database.rawQuery(qry,null);
        if(cursor.moveToFirst()){
            do {
                 int ayahpos = cursor.getInt(cursor.getColumnIndex(AyahPosition));
                 String title = cursor.getString(cursor.getColumnIndex(BookMarkTitle));
                 String SurahN = cursor.getString(cursor.getColumnIndex(SurahName));
                 arrayList.add(new BookMarkList_Model(title,SurahN,ayahpos));
            }while (cursor.moveToNext());
        }
        return arrayList;
    }

    @SuppressLint("Range")
    public ArrayList<Notes_Model> GetNotesNameAndNotesDetails(){
        ArrayList<Notes_Model> arrayList = new ArrayList<>();
        String qry = "Select * from " + NoteTb;
        cursor = database.rawQuery(qry,null);
        if(cursor.moveToFirst()){
            do {
                int NoteId = cursor.getInt(cursor.getColumnIndex(NotesId));
                String NoteNameData = cursor.getString(cursor.getColumnIndex(NoteName));
                int SurahIdDataForNote = cursor.getInt(cursor.getColumnIndex(SuraId));
                int VerseIdDataForNote = cursor.getInt(cursor.getColumnIndex(VerseId));
                String VerseTextDataForNote = cursor.getString(cursor.getColumnIndex(AyahText));
                String NoteDataForNote = cursor.getString(cursor.getColumnIndex(Note));
                String SurahNameDataForNote = cursor.getString(cursor.getColumnIndex(SuratName));
                arrayList.add(new Notes_Model(NoteId,NoteNameData,SurahIdDataForNote,VerseIdDataForNote,VerseTextDataForNote,NoteDataForNote,SurahNameDataForNote));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public void AddNotes(String notename,int sura,int aya,String text,String note,String suraname){
        ContentValues cv = new ContentValues();
        cv.put(NoteName,notename);
        cv.put(SuraId,sura);
        cv.put(VerseId,aya);
        cv.put(ayah,text);
        cv.put(Note,note);
        cv.put(SuratName,suraname);
        long i = database.insert(NoteTb,null,cv);
        if(i == -1)
            Toast.makeText(context, "Not", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
    }
    public void UpdateNoteEnglishText(String NoteData,int Id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Note,NoteData);
        long i = database.update(NoteTb,contentValues,NotesId + " =?",new String[]{String.valueOf(Id)});
        if(i == -1)
            Toast.makeText(context, "Not Updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
    }

}
