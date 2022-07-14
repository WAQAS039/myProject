package com.example.learnquran.audioRecodingClasses;

import android.content.Context;
import android.media.MediaRecorder;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder recorder;

    private final File mAudioFile;
    Context context;
    public AudioRecorder(File mAudio, Context context) {
        mAudioFile = mAudio;
        recorder = new MediaRecorder();
        this.context = context;
    }

    public void startRecord() throws IOException {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(mAudioFile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    public void stopRecord() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }
}
