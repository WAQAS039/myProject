package com.example.learnquran.OnlineVideoFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnquran.Activities.OnlineVideoActivity;
import com.example.learnquran.Models.ImageUriModel;
import com.example.learnquran.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class LoginDashBoardFragment extends Fragment {
    ImageView ProfileDetails;
    TextInputLayout tvChannelName , tvUserName;
    FloatingActionButton btnMic;
    Button btnJoinChannel;
    String userName;
    boolean isMicOn = false;
    ImageUriModel imageUriModel;
    public LoginDashBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_dash_board, container, false);
        Init(view);
        userName = getArguments().getString("userName");
        tvUserName.setEnabled(false);
        tvUserName.getEditText().setText(userName);
        tvChannelName.setEnabled(false);
        tvChannelName.getEditText().setText("Testing");
        // Go to Profile Details
        setProfileDetailsOnClick(view);
        setBtnMicOnClick(view);
        setBtnJoinChannelOnClick();
        getImageDownloadUrl();
        return view;
    }
    void Init(View view){
        ProfileDetails = view.findViewById(R.id.imageViewProfile);
        tvChannelName = view.findViewById(R.id.tvChannelName);
        tvUserName = view.findViewById(R.id.tvUserName);
        btnMic = view.findViewById(R.id.fbtnMic);
        btnJoinChannel = view.findViewById(R.id.btnJoinChannel);
        btnMic.setImageResource(R.drawable.mic_off);
    }

    void setProfileDetailsOnClick(View view){
        ProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userNameDb",userName);
                bundle.putString("imageUri",imageUriModel.getImageUri().toString());
                Navigation.findNavController(view).navigate(R.id.action_loginDashBoardFragment_to_profileDetailsFragment,bundle);
            }
        });
    }

    void setBtnJoinChannelOnClick(){
        btnJoinChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String channelName = tvChannelName.getEditText().getText().toString();
                Intent intent = new Intent(getContext(), OnlineVideoActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("channelName",channelName);
                intent.putExtra("IsMicOn",isMicOn);
                intent.putExtra("imageUri",imageUriModel.getImageUri().toString());
                startActivity(intent);
            }
        });
    }

    void setBtnMicOnClick(View view){
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMicOn){
                    btnMic.setImageResource(R.drawable.mic_on);
                    isMicOn = true;
                }else if(isMicOn){
                    btnMic.setImageResource(R.drawable.mic_off);
                    isMicOn = false;
                }
            }
        });
    }
    void getImageDownloadUrl(){

        StorageReference imagePath = FirebaseStorage
                .getInstance()
                .getReference()
                .child("image/"+userName);
        Task<Uri> imageUrl = imagePath.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext())
                                .load(uri)
                                .error("error")
                                .into(ProfileDetails);
                        imageUriModel = new ImageUriModel(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}