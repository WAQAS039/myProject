package com.example.learnquran.QuranFragments;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnquran.Adopters.ItemClickSupport;
import com.example.learnquran.R;
import com.example.learnquran.audioRecodingClasses.AudioRecAdopter;
import com.example.learnquran.audioRecodingClasses.AudioRecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AudioListFragment extends Fragment {
    private File mDirectory;
    AudioRecAdopter audioRecAdopter;
    ArrayList<String> audioArrayList;
    AudioRecorder audioRecorder;
    RecyclerView rvAudioRec;
    boolean isPause = false;
    ItemTouchHelper.SimpleCallback simpleCallback = ShowItemTouchHelper();

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_list, container, false);
        rvAudioRec = view.findViewById(R.id.rvAudoRecords);
        initAudioRec();
        try {
            createRecorderFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rvAudioRec);
        setAudioRecAdopter();
        setOnClickOnAudio();
        return view;
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
        File mNewFile = new File(mDirectory, System.currentTimeMillis() + ".mp3");
        if (!mNewFile.exists())
            mDirectory.createNewFile();
        return mNewFile;
    }

    // Init the recorder and other variable for audio rec
    void initAudioRec() {
        try {
            mDirectory = new File(getActivity().getFilesDir() + File.separator + "MyRecords");
            File mAudioFile = createRecorderFile();
            audioRecorder = new AudioRecorder(mAudioFile, getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setAudioRecAdopter() {
        audioRecAdopter = new AudioRecAdopter(getContext(), audioArrayList);
        rvAudioRec.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAudioRec.setAdapter(audioRecAdopter);
    }

    void setOnClickOnAudio() {
        ItemClickSupport.addTo(rvAudioRec).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                showToast(position + "");
                String fileName = audioArrayList.get(position);
                onPrepareMedia(fileName);
            }
        });
    }

    private void onPrepareMedia(String fileName) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        Dialog playAudioDialog = new Dialog(getContext());
        playAudioDialog.setContentView(R.layout.audio_player_dialog);
        playAudioDialog.setCancelable(false);
        // View inside Dialog
        TextView tvPlayerTitle;
        Chronometer chronometerTimer;
        Button pause,stop;
        tvPlayerTitle = playAudioDialog.findViewById(R.id.tvPlayerTitle);
        chronometerTimer = playAudioDialog.findViewById(R.id.mediaPlayerTimer);
        pause = playAudioDialog.findViewById(R.id.btnPause);
        stop = playAudioDialog.findViewById(R.id.btnStop);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPause) {
                    isPause = true;
                    pause.setText("Play");
                    tvPlayerTitle.setText("Pause....");
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        chronometerTimer.stop();
                    }
                }else if(isPause){
                    isPause = false;
                    pause.setText("Pause");
                    tvPlayerTitle.setText("Playing....");
                    chronometerTimer.start();
                    mediaPlayer.start();
                    setOnCompleteOnMediaPlayer(mediaPlayer,chronometerTimer,playAudioDialog);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometerTimer.stop();
                mediaPlayer.stop();
                mediaPlayer.release();
                playAudioDialog.dismiss();
            }
        });
        playAudioDialog.show();
        try {
            chronometerTimer.start();
            chronometerTimer.setBase(SystemClock.elapsedRealtime());
            mediaPlayer.setDataSource(getContext(), Uri.fromFile(new File(mDirectory + "/" + fileName)));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            showToast("Failed to initialize media...");
        }
        setOnCompleteOnMediaPlayer(mediaPlayer,chronometerTimer,playAudioDialog);
    }

    void setOnCompleteOnMediaPlayer(MediaPlayer mediaPlayer,Chronometer chronometer,Dialog dialog){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                chronometer.stop();
                mediaPlayer.stop();
                mediaPlayer.release();
                dialog.dismiss();
            }
        });
    }

    void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public ItemTouchHelper.SimpleCallback ShowItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String fileName = audioArrayList.get(viewHolder.getAdapterPosition());
                String filePath = mDirectory + "/" + fileName;
                File file = new File(filePath);
                boolean deleted = file.delete();
                if (!deleted)
                    showToast("Not deleted");
                audioRecAdopter.delete(viewHolder.getAdapterPosition());
            }
        };
        return simpleCallback;
    }
}