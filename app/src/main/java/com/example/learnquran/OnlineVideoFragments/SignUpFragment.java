package com.example.learnquran.OnlineVideoFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnquran.Models.SignUpUserModel;
import com.example.learnquran.R;
import com.example.learnquran.connection.InternetConnection;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUpFragment extends Fragment {
    Button signUpButton;
    TextInputLayout userName, password, conformPassword;
    TextView AlreadyUser;
    DatabaseReference database;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Init(view);
        InternetConnection.checkInternet(getContext(), getActivity());
        // Firebase
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://userdatabase-e2d30-default-rtdb.firebaseio.com");
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameText = userName.getEditText().getText().toString().trim();
                String passwordText = password.getEditText().getText().toString().trim();
                if (!validateUserName() | !validatePassword() | !validateRePassword()) {
                    return;
                } else {
                    database.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(userNameText)) {
                                userName.setError("Already Exists");
                            } else {
                                userName.setError(null);
                                Bundle bundle = new Bundle();
                                bundle.putString("userName", userNameText);
                                bundle.putString("password",passwordText);
                                Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_addProfilePictureFragment, bundle);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        AlreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment2);
            }
        });
        return view;
    }

    void Init(View view) {
        signUpButton = view.findViewById(R.id.btnSignupBtn);
        userName = view.findViewById(R.id.edCheckUserName);
        password = view.findViewById(R.id.edCheckPassword);
        conformPassword = view.findViewById(R.id.edCheckConformPassword);
        AlreadyUser = view.findViewById(R.id.tvAlreadyUser);
    }

    boolean validateUserName() {
        String userNameVal = userName.getEditText().getText().toString().trim();
        if (userNameVal.isEmpty()) {
            userName.setError("Require");
            return false;
        } else if (userNameVal.length() >= 15) {
            userName.setError("user name should not long");
            return false;
        } else if (userNameVal.contains(".") || userNameVal.contains(",")) {
            userName.setError("userName should not contain ...");
            return false;
        } else if (!userNameVal.startsWith("@")) {
            userName.setError("username should start from @example123");
            return false;
        } else {
            userName.setError(null);
            return true;
        }
    }

    boolean validatePassword() {
        String passwordVal = password.getEditText().getText().toString().trim();
        String number = "(.*[0-9].*)";
        String specialChar = "(.*[@,#,$,%].*$)";
        String upperCaseLetter = "(.*[A-Z].*)";
        if (passwordVal.isEmpty()) {
            password.setError("Require");
            return false;
        } else if (!passwordVal.matches(number)) {
            password.setError("should have at least one number");
            return false;
        } else if (!passwordVal.matches(specialChar)) {
            password.setError("should have at least one special character");
            return false;
        } else if (!passwordVal.matches(upperCaseLetter)) {
            password.setError("should have at least one uppercase letter");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    boolean validateRePassword(){
        String conformPasswordVal = conformPassword.getEditText().getText().toString().trim();
        String passwordVal = password.getEditText().getText().toString().trim();
        if (conformPasswordVal.isEmpty()) {
            conformPassword.setError("Require");
            return false;
        }else if(!conformPasswordVal.equals(passwordVal)){
            conformPassword.setError("Password Not match");
            return false;
        }else{
            conformPassword.setError(null);
            return true;
        }
    }
}