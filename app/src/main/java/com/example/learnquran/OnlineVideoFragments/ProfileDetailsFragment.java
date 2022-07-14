package com.example.learnquran.OnlineVideoFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnquran.Models.SignUpUserModel;
import com.example.learnquran.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileDetailsFragment extends Fragment {
    ImageView editFullName,editPassword,editAbout,logout,imageProfilePic;
    TextView tvFullName, tvPassword, tvAbout;
    DatabaseReference databaseReference;
    String userName;
    String changeFullName, changeUserName,changePassword,changeAbout;
    public ProfileDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);
        Init(view);
        userName = getArguments().getString("userNameDb");
        // firebase
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://userdatabase-e2d30-default-rtdb.firebaseio.com");
        getDataFromDatabase();
        editFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditDialog("Full Name");
            }
        });
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditDialog("password");
            }
        });
        editAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditDialog("About");
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("logoutUser","logout");
                Navigation.findNavController(view).navigate(R.id.action_profileDetailsFragment_to_loginFragment,bundle);
            }
        });
        getImageDownloadUrl();
        return view;
    }
    void Init(View view){
        editFullName = view.findViewById(R.id.editName);
        editPassword = view.findViewById(R.id.editPassword);
        editAbout = view.findViewById(R.id.editAbout);
        logout = view.findViewById(R.id.btnLogout);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvPassword = view.findViewById(R.id.tvPassword);
        tvAbout = view.findViewById(R.id.tvAboutYou);
        imageProfilePic = view.findViewById(R.id.imageProfilePic);

    }
    @SuppressLint("SetTextI18n")
    void ShowEditDialog(String editFieldName){
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.edit_profile_detail_layout);
        dialog.setCancelable(false);
        TextView Title = dialog.findViewById(R.id.enterYourName);
        EditText data = dialog.findViewById(R.id.change_data_for_profiles);
        Button cancel,save;
        cancel = dialog.findViewById(R.id.cancel_changes_to_profile);
        save = dialog.findViewById(R.id.save_changes_to_profile);
        if(editFieldName.equals("Full Name")){
            Title.setText("Enter Your " + editFieldName);
            data.setText(tvFullName.getText().toString());
        }else if(editFieldName.equals("password")){
            Title.setText("Enter Your " + editFieldName);
            data.setText(tvPassword.getText().toString());
        }else if(editFieldName.equals("About")){
            Title.setText(editFieldName + " Section");
            data.setText(tvAbout.getText().toString());
        }
        save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(editFieldName.equals("Full Name")){
                    changeFullName = data.getText().toString();
                    tvFullName.setText(changeFullName);
                    Toast.makeText(getContext(), "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                    databaseReference.child("users").child(userName).child("fullName").setValue(changeFullName);
                    dialog.dismiss();
                }else if(editFieldName.equals("password")){
                    changePassword = data.getText().toString();
                    tvPassword.setText(changePassword);
                    Toast.makeText(getContext(), "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                    databaseReference.child("users").child(userName).child("password").setValue(changePassword);
                    dialog.dismiss();
                }else if(editFieldName.equals("About")){
                    changeAbout = data.getText().toString();
                    tvAbout.setText(changeAbout);
                    Toast.makeText(getContext(), "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                    databaseReference.child("users").child(userName).child("about").setValue(changeAbout);
                    dialog.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void getDataFromDatabase(){
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SignUpUserModel signUpUserModelFromDB = snapshot.child(userName).getValue(SignUpUserModel.class);
                if(signUpUserModelFromDB != null)
                {
                    tvFullName.setText(signUpUserModelFromDB.getFullName());
                    tvPassword.setText(signUpUserModelFromDB.getPassword());
                    tvAbout.setText(signUpUserModelFromDB.getAbout());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                                .into(imageProfilePic);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}