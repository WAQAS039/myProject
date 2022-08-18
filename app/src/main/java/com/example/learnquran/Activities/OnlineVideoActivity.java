package com.example.learnquran.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnquran.Adopters.ItemClickSupport;
import com.example.learnquran.Adopters.OnlineUserAdopters;
import com.example.learnquran.Models.OnlineUserModel;
import com.example.learnquran.Models.SignUpUserModel;
import com.example.learnquran.QuranFragments.QuranFragment;
import com.example.learnquran.R;
import com.google.android.gms.tasks.OnCompleteListener;
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
    ImageView imageMic, imageMeetingInfo, imageSpeaker;
    CardView cvEndCall;

    //Recycle View Position
    int rvPos;
    // Agora Work
    String channelName;
    String localUserName;
    String userFullName;
    boolean isMicOn = false;
    boolean isSpeakerOn = false;
    int clientRole;
    RtcEngine rtcEngine;
    String appId = "5e8fca82263743f5b6b5dd38bf5905a3";
    String token;
    int localUserUId;
    ArrayList<OnlineUserModel> onlineUsersArrayList;
    OnlineUserAdopters onlineUserAdopters;
    RecyclerView rvOnlineUser;
    int selectIndex;
    // Firebase
    DatabaseReference database;
    private ValueEventListener listener;
    private DatabaseReference remoteUserPositionRef;
    boolean isListening = false;
    //Quran fragment
    QuranFragment fragment;
    // bottom Sheet
    BottomSheetDialog bottomSheetDialog;
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
                            String fullName = snapshot.child(id).child("fullName").getValue(String.class);
                            boolean micOnOrOff = snapshot.child(id).child("mic").getValue(Boolean.class) != null ? snapshot.child(id).child("mic").getValue(Boolean.class) : false;
                            int userRole = snapshot.child(id).child("roles").getValue(Integer.class) != null ? snapshot.child(id).child("roles").getValue(Integer.class) : -1;
                            if (userName != null) {
                                onlineUsersArrayList.add(new OnlineUserModel(userName, fullName, id, null, micOnOrOff, userRole));
                                onlineUserAdopters.notifyDataSetChanged();
                                setAdapter();
                                setCountOnlinePeople();
                            }
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
                    OnlineUserModel mUser = onlineUsersArrayList.stream().filter((user) -> user.getuId().equals(id)).findFirst().orElse(null);
                    if (mUser != null) {
                        if (selectIndex > 0) {
                            selectIndex = onlineUsersArrayList.indexOf(mUser);
                            rtcEngine.leaveChannel();
                            database.child("agoraUsers").child(id).setValue(null);
                            onlineUserAdopters.remove(selectIndex);
                            setAdapter();
                            setCountOnlinePeople();
                        }
                    }
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
                    database.child("agoraUsers").child(id).child("fullName").setValue(userFullName);
                    database.child("agoraUsers").child(id).child("mic").setValue(isMicOn);
                    database.child("agoraUsers").child(id).child("roles").setValue(clientRole);
                    // todo on data change 1
                    database.child("agoraUsers").addValueEventListener(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Get Data from Firebase Real time Database
                            String userName = snapshot.child(id).child("Name").getValue(String.class);
                            String fullName = snapshot.child(id).child("fullName").getValue(String.class);
                            boolean micOnOrOff = snapshot.child(id).child("mic").getValue(Boolean.class) != null ? snapshot.child(id).child("mic").getValue(Boolean.class) : false;
                            int userRole = snapshot.child(id).child("roles").getValue(Integer.class) != null ? snapshot.child(id).child("roles").getValue(Integer.class) : -1;
                            // Get User Profile pic
                            StorageReference imagePath = FirebaseStorage.getInstance().getReference().child("image/" + userName);
                            if (userName != null) {
                                onlineUsersArrayList.clear();
                                onlineUsersArrayList.add(new OnlineUserModel(userName, fullName, id, null, micOnOrOff, userRole));
                                onlineUserAdopters.notifyDataSetChanged();
                                setAdapter();
                                setCountOnlinePeople();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_video);
        getSupportActionBar().hide();
        Init();
        setCountPeopleBtnOnClick();
        setFbtnEndCallOnClick();
        setImageMicOnClick();
        setJoinUserBottomSheet();
        setImageMeetingInfoOnClick();
        setCountOnlinePeople();
        LoadQuran();
        checkInternet(this, this);
        setImageSpeaker();
    }

    void Init() {
        // Views
        cvEndCall = findViewById(R.id.cvEndCall);
        countPeopleBtn = findViewById(R.id.btnCountpeople);
        imageMic = findViewById(R.id.btnMic);
        imageMeetingInfo = findViewById(R.id.imageMeetingInfo);
        imageSpeaker = findViewById(R.id.imageSpeaker);
        // Init variables
        channelName = getIntent().getStringExtra("channelName");
        localUserName = getIntent().getStringExtra("userName");
        token = getIntent().getStringExtra("token");
        getUserFullName(localUserName);
        // array list to store online users
        onlineUsersArrayList = new ArrayList<>();
        // firebase
        database = FirebaseDatabase.getInstance().getReference();


        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rvPos = snapshot.child("position").getValue(Integer.class) != -1 ? snapshot.child("position").getValue(Integer.class) : -1;
                fragment.onUpdateRecycleItemPosition(rvPos);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
    }

    void setImageSpeaker() {
        imageSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpeakerOn) {
                    isSpeakerOn = false;
                    rtcEngine.setEnableSpeakerphone(true);
                    imageSpeaker.setImageResource(R.drawable.speaker);
                } else {
                    isSpeakerOn = true;
                    rtcEngine.setEnableSpeakerphone(false);
                    imageSpeaker.setImageResource(R.drawable.speaker_black);
                }
            }
        });
    }

    void checkMicIsOn(boolean isMicOn) {
        if (isMicOn) {
            rtcEngine.muteLocalAudioStream(false);
            imageMic.setImageResource(R.drawable.mic_on);
        } else {
            rtcEngine.muteLocalAudioStream(true);
            imageMic.setImageResource(R.drawable.mic_off);
        }

    }

    void setImageMicOnClick() {
        imageMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localUid = String.valueOf(localUserUId);
                if (!isMicOn) {
                    isMicOn = true;
                    imageMic.setImageResource(R.drawable.mic_on);
                    rtcEngine.muteLocalAudioStream(false);
                    database.child("agoraUsers").child(localUid).child("mic").setValue(isMicOn);
                } else if (isMicOn) {
                    isMicOn = false;
                    imageMic.setImageResource(R.drawable.mic_off);
                    rtcEngine.muteLocalAudioStream(true);
                    database.child("agoraUsers").child(localUid).child("mic").setValue(isMicOn);
                }
            }
        });
    }

    void LoadQuran() {
        Bundle bundle = new Bundle();
        bundle.putString("userNameForQuran", localUserName);
        fragment = new QuranFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loadWholeQuranFrame, fragment)
                .commit();
    }


    //get Recycle View Position
    void getUserNameRecyclePos(String userName) {
        remoteUserPositionRef = database.child("RecyclePos").child(userName);
        remoteUserPositionRef.addValueEventListener(listener);
    }

    void setJoinUserBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_for_online_people);
        bottomSheetDialog.setCancelable(true);
        // init Views
        rvOnlineUser = bottomSheetDialog.findViewById(R.id.rvOnlineUsers);
        setOnClickOnrvOnlineUser();
        // Agora Init
        allPermission();
        initRtc();
        checkMicIsOn(isMicOn);
        setAdapter();
    }

    void setOnClickOnrvOnlineUser() {
        ItemClickSupport.addTo(rvOnlineUser).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                OnlineUserModel mUser = onlineUsersArrayList.get(position);
                if(position == 0 && remoteUserPositionRef != null){
                    remoteUserPositionRef.removeEventListener(listener);
                }else if(position > 0){
                    if(remoteUserPositionRef != null)
                    {
                        remoteUserPositionRef.removeEventListener(listener);
                    }
                    getUserNameRecyclePos(mUser.getUser());
                }
                bottomSheetDialog.dismiss();
            }
        });
    }

    void setCountPeopleBtnOnClick() {
        countPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
    }

    void setFbtnEndCallOnClick() {
        cvEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onlineUsersArrayList != null && onlineUsersArrayList.size() > 0) {
                    leaveUserOnPosition(0);
                    finish();
                }
            }
        });
    }

    // share channel name
    void setImageMeetingInfoOnClick() {
        imageMeetingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "You can Join Using Meeting using This channel name : " + channelName);
                intent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(intent, null);
                startActivity(shareIntent);
            }
        });
    }

    void setCountOnlinePeople() {
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
        int i = rtcEngine.joinChannel(token, channelName, null, 0);
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
        clientRole = getIntent().getIntExtra("role", -1);
        InitializeRtcEngine();
        rtcEngine.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_STANDARD_STEREO, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
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

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void setAdapter() {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        onlineUserAdopters = new OnlineUserAdopters(onlineUsersArrayList, mStorageReference, this);
        rvOnlineUser.setLayoutManager(new LinearLayoutManager(this));
        rvOnlineUser.setAdapter(onlineUserAdopters);
    }

    void getUserFullName(String userName) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SignUpUserModel signUpUserModel = snapshot.child(userName).getValue(SignUpUserModel.class);
                if (signUpUserModel != null) {
                    userFullName = signUpUserModel.getFullName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // check internet connection
    public void checkInternet(Context con, Activity activity) {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
                super.onAvailable(network);
            }

            @Override
            public void onLost(@NonNull Network network) {
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (onlineUsersArrayList != null && onlineUsersArrayList.size() > 0) {
                                leaveUserOnPosition(0);
                                finish();
                            }
                        }
                    });
                }
                super.onLost(network);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
            }
        };
        ConnectivityManager connectivityManager =
                (ConnectivityManager) con.getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }
}