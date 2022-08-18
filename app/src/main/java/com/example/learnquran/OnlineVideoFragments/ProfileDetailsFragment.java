package com.example.learnquran.OnlineVideoFragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.learnquran.Activities.OnlineMettingActivity;
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


public class ProfileDetailsFragment extends Fragment {
    ImageView logout,imageProfilePic;
    DatabaseReference databaseReference;
    String userName , profileImageUri , fullName, password;
    CardView cvChangeImage;
    StorageReference storageReference;
    TextInputLayout tipFullName, tipPassword;
    TextInputEditText edFullName,edPassword;
    Button btnUpdate;
    public ProfileDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);
        Init(view);
        logoutUser(view);
        loadImageFromUrl();
        changeImage();
        updateProfile(view);
        return view;
    }
    void Init(View view){
        logout = view.findViewById(R.id.btnLogout);
        imageProfilePic = view.findViewById(R.id.imageProfilePic);
        cvChangeImage = view.findViewById(R.id.cvChangeImage);
        storageReference = FirebaseStorage.getInstance().getReference();
        tipFullName = view.findViewById(R.id.tipFullName);
        tipPassword = view.findViewById(R.id.tipPassword);
        edFullName = view.findViewById(R.id.edSecFullName);
        edPassword = view.findViewById(R.id.edPassword);
        btnUpdate = view.findViewById(R.id.btnUpdateProfileDetails);
        userName = getArguments().getString("userNameDb");
        fullName = getArguments().getString("fullName");
        password = getArguments().getString("password");
        profileImageUri = getArguments().getString("imageUri");

        //set the Data
        edFullName.setText(fullName);
        edPassword.setText(password);

        InternetConnection.checkInternet(getContext(),getActivity());
        // firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    void updateProfile(View view){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullName.equals(edFullName.getText().toString()))
                    tipFullName.setError("Same Name");
                else
                {
                    tipFullName.setError(null);
                    isFullNameChange(view);
                }
                if( password.equals(edPassword.getText().toString()))
                    tipPassword.setError("Same Password");
                else
                {
                    tipPassword.setError(null);
                    isPasswordChange(view);
                }

            }
        });
    }
    void logoutUser(View view){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("logoutUser","logout");
                Navigation.findNavController(view).navigate(R.id.action_profileDetailsFragment_to_loginFragment,bundle);
            }
        });
    }
    void loadImageFromUrl(){
        Glide.with(getContext())
                .load(Uri.parse(profileImageUri))
                .error("error")
                .into(imageProfilePic);
    }


    //Image Uploading
    void changeImage(){
        cvChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start( requireContext() , ProfileDetailsFragment.this);
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
                imageProfilePic.setImageURI(imageUri);
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



    //Validations

    void isFullNameChange(View view){
        if (!fullName.equals(edFullName.getText().toString())) {
            if (!validateFullName()) {
                return;
            }else {
                databaseReference.child("users").child(userName).child("fullName").setValue(edFullName.getText().toString());
                Navigation.findNavController(view).popBackStack();
                showToast("Name Updated");
            }
        }
    }
    void isPasswordChange(View view){
        if (!password.equals(edPassword.getText().toString())) {
            if (!validatePassword()) {
                return;
            } else {
                databaseReference.child("users").child(userName).child("password").setValue(edPassword.getText().toString());
                Navigation.findNavController(view).popBackStack();
                showToast("Password updated");
            }
        }
    }

    boolean validateFullName() {
        String fullNameVal = edFullName.getText().toString().trim();
        if (fullNameVal.isEmpty()) {
            tipFullName.setError("can Not be Empty");
            return false;
        } else if (fullNameVal.length() >= 15) {
            tipFullName.setError("full name is too long");
            return false;
        }else {
            tipFullName.setError(null);
            return true;
        }
    }

    boolean validatePassword() {
        String password = edPassword.getText().toString().trim();
        String number = "(.*[0-9].*)";
        String specialChar = "(.*[@,#,$,%].*$)";
        String upperCaseLetter = "(.*[A-Z].*)";
        if (password.isEmpty()) {
            tipPassword.setError("can Not be Empty");
            return false;
        } else if (!password.matches(number)) {
            tipPassword.setError("should have at least one number");
            return false;
        } else if (!password.matches(specialChar)) {
            tipPassword.setError("should have at least one special character");
            return false;
        } else if (!password.matches(upperCaseLetter)) {
            tipPassword.setError("should have at least one uppercase letter");
            return false;
        } else {
            tipPassword.setError(null);
            return true;
        }
    }
    void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}