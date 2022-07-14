package com.example.learnquran.QuranFragments;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnquran.Adopters.ItemClickSupport;
import com.example.learnquran.Adopters.QuranText_Adopter;
import com.example.learnquran.Databases.Database;
import com.example.learnquran.Models.AyahPosition_Model;
import com.example.learnquran.Models.QuranText_Model;
import com.example.learnquran.Models.Urdu_Translation;
import com.example.learnquran.R;
import com.example.learnquran.audioRecodingClasses.AudioRecAdopter;
import com.example.learnquran.audioRecodingClasses.AudioRecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class QuranFragment extends Fragment {
    Button BookmarkButton,NotesButton,VoiceRecorderButton;
    RecyclerView QuranTextRecycleView;
    QuranText_Adopter quranText_adopter;
    ArrayList<QuranText_Model> quranText_modelArrayList;
    ArrayList<Urdu_Translation> urdu_translationArrayList;
    Database database;
    TextView ShowSurahName;
    LinearLayoutManager manager;
    String SurahName,AyahTextData;
    int SurahIDNumber,VerseIDNumber,RecyclePosition;
    AyahPosition_Model ayahPosition_model;
    ArrayList<AyahPosition_Model> AyahPositionArraylist;
    boolean IsRecording = false;
    boolean ShowQuranText,ShowQuranTrans;
    int ArabicFont,TransFont;
    // for recording permission
    public static final int RequestPermissionCode = 1;
    // variable for Audio rec
    private File mDirectory;
    AudioRecorder audioRecorder;
    ArrayList<String> audioArrayList;
    Chronometer timer;
    public QuranFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quran, container, false);
        Init(view);
        ShowQuranText = getActivity().getIntent().getBooleanExtra("ShowArabic",true);
        ShowQuranTrans = getActivity().getIntent().getBooleanExtra("ShowTrans",true);
        ArabicFont = getActivity().getIntent().getIntExtra("ArabicSize",20);
        TransFont = getActivity().getIntent().getIntExtra("TransSize",20);
        SetQuranText();
        SetOnClickOnQuranText();
        SetOnScrollListener();
        GetLastAyahPosition();
        int SurahIndex = getActivity().getIntent().getIntExtra("index",-1);
        int ParaIndex = getActivity().getIntent().getIntExtra("index2",-1);
        int BookmarkIndex = getActivity().getIntent().getIntExtra("index3",-1);
        if(SurahIndex != -1)
        {
            RecyclePosition = SurahIndex;
            manager.scrollToPosition(SurahIndex);
            quranText_adopter.selectedPosition(SurahIndex);
            CheckBookMarkPosition(SurahIndex);
            SaveLastAyahPosition(SurahIndex);
            GetQuranTextModelData(SurahIndex);
            WhichSurah(SurahIDNumber);

        }
        else if(ParaIndex != -1){
            RecyclePosition = ParaIndex;
            manager.scrollToPosition(ParaIndex);
            quranText_adopter.selectedPosition(ParaIndex);
            CheckBookMarkPosition(ParaIndex);
            SaveLastAyahPosition(ParaIndex);
            GetQuranTextModelData(ParaIndex);
            WhichSurah(SurahIDNumber);
        }
        else if(BookmarkIndex != -1){
            RecyclePosition = BookmarkIndex;
            manager.scrollToPosition(BookmarkIndex);
            quranText_adopter.selectedPosition(BookmarkIndex);
            CheckBookMarkPosition(BookmarkIndex);
            SaveLastAyahPosition(BookmarkIndex);
            GetQuranTextModelData(BookmarkIndex);
            WhichSurah(SurahIDNumber);
        }
        BookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Position from Database
                int pos = -1;
                pos = CheckBookMarkPosition(RecyclePosition);
                String p = String.valueOf(pos);
                if(RecyclePosition == pos){
                    BookmarkButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_empty,0,0,0);
                    OpenDatabase();
                    database.DeleteBookMark(p);
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }else if(RecyclePosition == -1)
                    Toast.makeText(getContext(), "Please Select a Ayah", Toast.LENGTH_SHORT).show();
                else
                {
                    String FullName = "(" + SurahIDNumber+")" + " " +SurahName +" , آیت نمبر" + "("+VerseIDNumber+")";
                    database.AddBookMarks(RecyclePosition,SurahName,FullName);
//                    textView.setText(FullName);
                    BookmarkButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_fil,0,0,0);
                    Toast.makeText(getContext(), "Inserted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        NotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAdd_A_NoteDialog(SurahIDNumber,VerseIDNumber,AyahTextData);
            }
        });
        allPermissions();
        initAudioRec();
        startRec();
        return view;
    }


    void Init(View view){
        QuranTextRecycleView = view.findViewById(R.id.QuranTextRecycleViewInQuranFragment);
        NotesButton = view.findViewById(R.id.NotesButtons);
        BookmarkButton = view.findViewById(R.id.BookmarksButtons);
        ShowSurahName = view.findViewById(R.id.SurahsNamesTextViewTOSHowSurahNamesWhenScroll);
        VoiceRecorderButton = view.findViewById(R.id.VoiceRecorderBtn);
        timer = view.findViewById(R.id.timer);
        timer.setVisibility(View.GONE);
    }
    void OpenDatabase(){
        database = new Database(getContext());
        database.open();
    }
    void SetQuranText(){
        OpenDatabase();
        quranText_modelArrayList = new ArrayList<>();
        urdu_translationArrayList = new ArrayList<>();
        quranText_modelArrayList = database.GetQuranText();
        urdu_translationArrayList = database.GetQuranTran();
        quranText_adopter = new QuranText_Adopter(getContext(),quranText_modelArrayList,urdu_translationArrayList);
        manager = new LinearLayoutManager(getContext());
        QuranTextRecycleView.setLayoutManager(manager);
        QuranTextRecycleView.setAdapter(quranText_adopter);
        // You Line is Used For Setting
        quranText_adopter.HideTextOrTranslation(ShowQuranText,ShowQuranTrans,ArabicFont,TransFont);
//        quranText_adopter.HideTextOrTranslation(true,true,ArabicFont,TransFont);
    }
    void SetOnClickOnQuranText(){
        ItemClickSupport.addTo(QuranTextRecycleView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                quranText_adopter.selectedPosition(position);
                GetQuranTextModelData(position);
//                checking.setText(SurahName + " " + SurahIDNumber + " " + VerseIDNumber + " " + AyahTextData);
                CheckBookMarkPosition(position);
                RecyclePosition = position;
                WhichSurah(SurahIDNumber);
            }
        });
    }

    void WhichSurah(int SurahIdPos){
        OpenDatabase();
        SurahName = database.GetSpecificSurahName(SurahIdPos);
        ShowSurahName.setText(SurahName);
    }
    void ShowAdd_A_NoteDialog(int SurahIdData,int ayahIdData,String VerseTextData){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.addnew_notes_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();
        EditText NotesEnglishTextData, NotesTitleData;
        NotesEnglishTextData = dialog.findViewById(R.id.YourNoteMessageEditText);
        NotesTitleData = dialog.findViewById(R.id.YourNoteNameEditText);
        NotesTitleData.setText("MyNote");
        Button AddNoteButton,NoteCancelButton;
        AddNoteButton = dialog.findViewById(R.id.AddANoteButton);
        NoteCancelButton = dialog.findViewById(R.id.CancelButtonForNotes);
        AddNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MyNoteMessageTextData = NotesEnglishTextData.getText().toString();
                String MyNoteTitleTextData = NotesTitleData.getText().toString();
                OpenDatabase();
                database.AddNotes(MyNoteTitleTextData,SurahIdData,ayahIdData,VerseTextData,MyNoteMessageTextData,SurahName);
                dialog.dismiss();
            }
        });
        NoteCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    // STORE LAST AYAH POSITION IN SHARE PREFERENCE
    void SaveLastAyahPosition(int position){
        SharedPreferences preferences = getActivity().getSharedPreferences("position", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("KEY_LAST_POSITION", position).apply();
    }
    // GET STORE AYAH POSITION FROM SHARE PREFERENCE
    void GetLastAyahPosition(){
        SharedPreferences preferences = getActivity().getSharedPreferences("position", MODE_PRIVATE);
        int LastPosition = preferences.getInt("KEY_LAST_POSITION", 0); // 0 is the default value
        this.RecyclePosition = LastPosition;
        QuranTextRecycleView.scrollToPosition(LastPosition);
        quranText_adopter.selectedPosition(LastPosition);
        CheckBookMarkPosition(LastPosition);
        GetQuranTextModelData(LastPosition);
        GetSurahName(SurahIDNumber);
//        checking.setText(SurahName);
        CheckBookMarkPosition(LastPosition);
        WhichSurah(SurahIDNumber);
    }

    // Get Surah Name
    void GetSurahName(int SurahId){
        OpenDatabase();
        SurahName = database.GetSpecificSurahName(SurahId);
    }

    void GetQuranTextModelData(int position){
        QuranText_Model quranText_model = quranText_modelArrayList.get(position);
        AyahTextData = quranText_model.getArabicText();
        SurahIDNumber = quranText_model.getSurahID();
        VerseIDNumber = quranText_model.getVerseId();
        GetSurahName(SurahIDNumber);
    }

    void SetOnScrollListener(){
        QuranTextRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int LastPosition = manager.findFirstVisibleItemPosition();
//                checking.setText(LastPosition + ".");
                SaveLastAyahPosition(LastPosition);
                GetQuranTextModelData(LastPosition);
                WhichSurah(SurahIDNumber);
            }
        });
    }

    public int CheckBookMarkPosition(int position){
        OpenDatabase();
        AyahPositionArraylist = database.AyahPositionFromBookmarksList();
        int pos = -1;
        for(int i = 0;i<AyahPositionArraylist.size();i++) {
            ayahPosition_model = AyahPositionArraylist.get(i);
            if(RecyclePosition == ayahPosition_model.getAyahPositionFromDatabase())
            {
                pos = ayahPosition_model.getAyahPositionFromDatabase();
                break;
            }
        }
        if(position == pos)
            BookmarkButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_fil,0,0,0);
        else
            BookmarkButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_empty,0,0,0);
        return pos;
    }

    void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
    //Code For Audio Recording

    private void startRec() {
        VoiceRecorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You Need to Code Here
                // When User Will Click this button so recording should start
                if(!IsRecording){
                    try {
                        timer.setVisibility(View.VISIBLE);
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.start();
                        initAudioRec();
                        audioRecorder.startRecord();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("Recording failed...");
                    }
                    Toast.makeText(getContext(), "Recording Started", Toast.LENGTH_SHORT).show();
                    IsRecording = true;
                    VoiceRecorderButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stop,0,0,0);

                }else if(IsRecording){
                    timer.stop();
                    timer.setVisibility(View.GONE);
                    audioRecorder.stopRecord();
                    Toast.makeText(getContext(), "Recording Saved", Toast.LENGTH_SHORT).show();
                    IsRecording = false;
                    VoiceRecorderButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.voice,0,0,0);
                }
            }
        });
    }

    void allPermissions(){
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    // Audio file and its path
    private File createRecorderFile() throws IOException {
        if (!mDirectory.exists()) {
            mDirectory.mkdir();
        }
        audioArrayList = new ArrayList<>();
        //SHow the data in recycle view
        String[] mAudios = mDirectory.list();
        if (mAudios != null && mAudios.length > 0) {
            audioArrayList.addAll(Arrays.asList(mAudios));
        }
        File mNewFile = new File(mDirectory, System.currentTimeMillis()  + ".mp3");
        if (!mNewFile.exists())
            mDirectory.createNewFile();
        return mNewFile;
    }

    // Init the recorder and other variable for audio rec
    void initAudioRec(){
        try {
            mDirectory = new File(getActivity().getFilesDir() + File.separator + "MyRecords");
            File mAudioFile = createRecorderFile();
            audioRecorder = new AudioRecorder(mAudioFile,getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(IsRecording){
            timer.stop();
            timer.setVisibility(View.GONE);
            audioRecorder.stopRecord();
            IsRecording = false;
            VoiceRecorderButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.voice,0,0,0);
        }
    }

}