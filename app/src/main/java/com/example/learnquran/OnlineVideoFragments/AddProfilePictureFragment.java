package com.example.learnquran.OnlineVideoFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learnquran.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddProfilePictureFragment extends Fragment {
    Button addImageAndAbout;
    CardView cvChangeProfileImage;
    ImageView ivProfilePic;
    TextInputLayout tvAbout;
    DatabaseReference databaseReference;
    String userName;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imagePath;
    public AddProfilePictureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_profile_picture, container, false);
        Init(view);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        addImageAndAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aboutData = tvAbout.getEditText().getText().toString();
                databaseReference.child("users").child(userName).child("about").setValue(aboutData);
                Navigation.findNavController(view).navigate(R.id.action_addProfilePictureFragment_to_loginFragment);
            }
        });
        setCvChangeProfileImageOnClick();
        return view;
    }
    void Init(View view){
        addImageAndAbout = view.findViewById(R.id.addProfilepicture);
        tvAbout = view.findViewById(R.id.tvAddAbout);
        cvChangeProfileImage = view.findViewById(R.id.cvChangeImage);
        ivProfilePic = view.findViewById(R.id.ivAddNewProfilePic);
        userName = getArguments().getString("addedUserName");
        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://userdatabase-e2d30-default-rtdb.firebaseio.com");
    }

    // Upload the Profile Picture to Firebase
    void setCvChangeProfileImageOnClick(){
        cvChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                someActivityResultLauncher.launch(intent);
            }
        });
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imagePath = data.getData();
                            ivProfilePic.setImageURI(imagePath);

                            ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setTitle("Uploading Image...");
                            progressDialog.show();
                            StorageReference storageRef = storageReference.child("image/"+ userName);
                            storageRef.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    showToast("Success");
                                    progressDialog.dismiss();
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            showToast(String.valueOf(uri));
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast("Fail");
                                    progressDialog.dismiss();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double percentage = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    progressDialog.setMessage("Percentage: " + percentage + "%");
                                }
                            });
                        }
                    }
                }
            });

    void uploadImage(){

    }


    void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}