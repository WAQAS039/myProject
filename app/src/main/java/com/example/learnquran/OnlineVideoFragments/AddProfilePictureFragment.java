package com.example.learnquran.OnlineVideoFragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
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
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnquran.Models.SignUpUserModel;
import com.example.learnquran.R;
import com.example.learnquran.connection.InternetConnection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddProfilePictureFragment extends Fragment {
    Button btnAddUserFullDetails;
    CardView cvChangeProfileImage;
    ImageView ivProfilePic;
    TextInputLayout tvFullName;
    DatabaseReference databaseReference;
    String userName;
    String password;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    TextInputEditText edFullName;

    public AddProfilePictureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_profile_picture, container, false);
        Init(view);
        InternetConnection.checkInternet(getContext(),getActivity());
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        addOnTextChangeListener();
        btnAddUserFullDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullNameData = tvFullName.getEditText().getText().toString();
                if(!validateFullName()){
                    return;
                }
                else {
                    SignUpUserModel signUpUserModel = new SignUpUserModel(fullNameData, userName, password);
                    databaseReference.child("users").child(userName).setValue(signUpUserModel);
                    Navigation.findNavController(view).navigate(R.id.action_addProfilePictureFragment_to_loginFragment);
                }
            }
        });
        setCvChangeProfileImageOnClick(view);
        return view;
    }
    void Init(View view){
        btnAddUserFullDetails = view.findViewById(R.id.addProfilepicture);
        tvFullName = view.findViewById(R.id.tvInputFullName);
        cvChangeProfileImage = view.findViewById(R.id.cvAddImage);
        ivProfilePic = view.findViewById(R.id.ivAddNewProfilePic);
        edFullName = view.findViewById(R.id.edFullName);
        userName = getArguments().getString("userName");
        password = getArguments().getString("password");
        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://userdatabase-e2d30-default-rtdb.firebaseio.com");
    }

    // getting the Profile Picture
    void setCvChangeProfileImageOnClick(View view){
        cvChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start( requireContext() , AddProfilePictureFragment.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imageUri = result.getUri();
                ivProfilePic.setImageURI(imageUri);
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading Image...");
                progressDialog.show();
                StorageReference storageRef = storageReference.child("image/"+ userName);
                storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        int percentage = (int) (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading: " + percentage + "%");
                    }
                });
            }
        }
    }
    void addOnTextChangeListener(){
        edFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!validateFullName()){
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!validateFullName()){
                    return;
                }
            }
        });
    }

    //Validations
    boolean validateFullName() {
        String fullNameVal = tvFullName.getEditText().getText().toString();
        if (fullNameVal.isEmpty()) {
            tvFullName.setError("Require");
            return false;
        } else if (fullNameVal.length() >= 15) {
            tvFullName.setError("full name is too long");
            return false;
        } else {
            tvFullName.setError(null);
            return true;
        }
    }

    void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}