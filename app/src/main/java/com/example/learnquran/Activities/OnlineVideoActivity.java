package com.example.learnquran.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnquran.Adopters.OnlineUserAdopters;
import com.example.learnquran.Models.OnlineUserModel;
import com.example.learnquran.QuranFragments.QuranFragment;
import com.example.learnquran.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class OnlineVideoActivity extends AppCompatActivity {
    View countPeopleBtn;
    ImageView imageMic,imageMeetingInfo;
    CardView cvEndCall;

    // Agora Work
    String channelName;
    String localUserName;
    boolean isMicOn;
    RtcEngine rtcEngine;
    String appId = "47493aaa59f24423be437a2def31e015";
    int localUserUId;
    ArrayList<OnlineUserModel> onlineUsersArrayList;
    OnlineUserAdopters onlineUserAdopters;
    RecyclerView rvOnlineUser;
    int selectIndex;

    // Firebase
    DatabaseReference database;

    // bottom Sheet
    BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_video);
        getSupportActionBar().hide();
        Init();
        setCountPeopleBtnOnClick();
        setFbtnEndCallOnClick();
        setImageMicOnClick();
        LoadQuran();
        setJoinUserBottomSheet();
        setImageMeetingInfoOnClick();
        setCountOnlinePeople();
        checkMicIsOn(isMicOn);
    }
    void Init(){
        // Views
        cvEndCall = findViewById(R.id.cvEndCall);
        countPeopleBtn = findViewById(R.id.btnCountpeople);
        imageMic = findViewById(R.id.btnMic);
        imageMeetingInfo = findViewById(R.id.imageMeetingInfo);
        // Init variables
        channelName = getIntent().getStringExtra("channelName");
        localUserName = getIntent().getStringExtra("userName");
        isMicOn = getIntent().getBooleanExtra("isMicOn",false);

        // array list to store online users
        onlineUsersArrayList = new ArrayList<>();
        // firebase
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://userdatabase-e2d30-default-rtdb.firebaseio.com");
    }

    void checkMicIsOn(boolean isMicOn){
        if(isMicOn)
            imageMic.setImageResource(R.drawable.mic_on);
        else
            imageMic.setImageResource(R.drawable.mic_off);
    }

    void setImageMicOnClick(){
        imageMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localUid = String.valueOf(localUserUId);
                if(!isMicOn){
                    isMicOn = true;
                    imageMic.setImageResource(R.drawable.mic_on);
                    database.child("agoraUsers").child(localUid).child("mic").setValue(isMicOn);
                }else if(isMicOn){
                    isMicOn = false;
                    imageMic.setImageResource(R.drawable.mic_off);
                    database.child("agoraUsers").child(localUid).child("mic").setValue(isMicOn);
                }
            }
        });
    }

    void LoadQuran(){
        Fragment fragment = new QuranFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loadWholeQuranFrame,fragment)
                .commit();
    }

    void setJoinUserBottomSheet(){
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_for_online_people);
        bottomSheetDialog.setCancelable(true);
        // init Views
        rvOnlineUser = bottomSheetDialog.findViewById(R.id.rvOnlineUsers);
        // Agora Init
        allPermission();
        initRtc();
        setAdapter();
    }
    void setCountPeopleBtnOnClick(){
        countPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
    }
    void setFbtnEndCallOnClick(){
        cvEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onlineUsersArrayList != null && onlineUsersArrayList.size() > 0) {
                    leaveUserOnPosition(0);
                    bottomSheetDialog.dismiss();
                    finish();
                }
            }
        });
    }

    // share channel name
    void setImageMeetingInfoOnClick(){
        imageMeetingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void setCountOnlinePeople(){
        TextView counter = countPeopleBtn.findViewById(R.id.peopleCounter);
        counter.setText(onlineUsersArrayList.size() + "");
    }

    // Agora Functions

    // Audio and video Permissions
    public void allPermission() {
        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    // Init Agora Rtc Engine
    public void InitializeRtcEngine() {
        try {
            rtcEngine = RtcEngine.create(this, appId, handler);
        } catch (Exception e) {
            showToast("Error");
        }
    }

    // Joining Channel
    private void joinChannel() {
        int i = rtcEngine.joinChannel(null, channelName, null, 0);
        if (i == -1)
            showToast("NotJoined");
        else
            showToast("Joined");
    }

    // setup Video mode
    public void SetUpVideoMode() {
        rtcEngine.enableVideo();
        rtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    // Init Agora Online Video
    void initRtc() {
        InitializeRtcEngine();
        rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        rtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        SetUpVideoMode();
        joinChannel();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBackPressed() {
        leaveUserOnPosition(0);
        finish();
    }

    @Override
    protected void onDestroy() {
        leaveUserOnPosition(0);
        finish();
        super.onDestroy();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void leaveUserOnPosition(@SuppressWarnings("SameParameterValue") int position) {
        if (onlineUsersArrayList != null && onlineUsersArrayList.size() > 0) {
            rtcEngine.leaveChannel();
            database.child("agoraUsers").child(String.valueOf(localUserUId)).setValue(null);
            onlineUserAdopters.remove(position);
            onlineUsersArrayList.clear();
            onlineUserAdopters.notifyDataSetChanged();
            setCountOnlinePeople();
        }
    }
    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Handler
    private final IRtcEngineEventHandler handler = new IRtcEngineEventHandler() {
        // This is call when Remote User Joins
        @Override
        public void onUserJoined(final int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String id = String.valueOf(uid);
                    database.child("agoraUsers").addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Get Remote User Name from Firebase Realtime Database
                            String userName = snapshot.child(id).child("Name").getValue(String.class);
                            boolean micOnOrOff = snapshot.child(id).child("mic").getValue(Boolean.class) != null ? snapshot.child(id).child("mic").getValue(Boolean.class) : false;
                            // Get User Profile pic
                            StorageReference imagePath = FirebaseStorage.getInstance().getReference().child("image/"+userName);
                            if (userName != null) {
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        onlineUsersArrayList.add(new OnlineUserModel(userName,id,uri,micOnOrOff));
                                        onlineUserAdopters.notifyDataSetChanged();
                                        setCountOnlinePeople();
                                    }
                                });
                            }
                            setAdapter();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
        }

        // THis is Call When Remote User Leave
        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(new Runnable() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    String id = String.valueOf(uid);
                    OnlineUserModel mUser = onlineUsersArrayList.stream().filter((user)-> user.getuId().equals(id)).findFirst().orElse(null);
                    if(mUser != null){
                        if(selectIndex > 0){
                            selectIndex = onlineUsersArrayList.indexOf(mUser);
                            rtcEngine.leaveChannel();
                            database.child("agoraUsers").child(id).setValue(null);
                            onlineUserAdopters.remove(selectIndex);
                            setAdapter();
                            setCountOnlinePeople();
                        }
                    }
                    showToast(" Remote User Leave " + uid);
                }
            });
        }

        // THis is call When Local user Join
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    localUserUId = uid;
                    // add local id and local user name in Firebase Real time database
                    String id = String.valueOf(uid);
                    database.child("agoraUsers").child(id).child("Name").setValue(localUserName);
                    database.child("agoraUsers").child(id).child("mic").setValue(isMicOn);

                    // todo on data change 1
                    database.child("agoraUsers").addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Get Data from Firebase Real time Database
                            String userName = snapshot.child(id).child("Name").getValue(String.class);
                            boolean micOnOrOff = snapshot.child(id).child("mic").getValue(Boolean.class) != null ? snapshot.child(id).child("mic").getValue(Boolean.class) : false;
                            // Get User Profile pic
                            StorageReference imagePath = FirebaseStorage.getInstance().getReference().child("image/"+userName);
                            if (userName != null) {
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        onlineUsersArrayList.clear();
                                        onlineUsersArrayList.add(new OnlineUserModel(userName,id,uri,micOnOrOff));
                                        onlineUserAdopters.notifyDataSetChanged();
                                        setCountOnlinePeople();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
        }
    };


    private void setAdapter() {
        onlineUserAdopters = new OnlineUserAdopters(onlineUsersArrayList,this);
        rvOnlineUser.setLayoutManager(new LinearLayoutManager(this));
        rvOnlineUser.setAdapter(onlineUserAdopters);
    }
}