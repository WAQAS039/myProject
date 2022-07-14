package com.example.learnquran.OnlineVideoFragments;

import android.os.Bundle;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpFragment extends Fragment {
    Button signUpButton;
    TextInputLayout fullName,userName,password;
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
        // Firebase
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://userdatabase-e2d30-default-rtdb.firebaseio.com");
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullNameData = fullName.getEditText().getText().toString().trim();
                String userNameText = userName.getEditText().getText().toString().trim();
                String passwordText = password.getEditText().getText().toString().trim();
                if(passwordText.isEmpty() || fullNameData.isEmpty() || userNameText.isEmpty())
                    showToast("Please Fill All the Fields");
                else {
                    SignUpUserModel signUpUserModel = new SignUpUserModel(fullNameData,userNameText,passwordText,"Be Happy!");
                    database.child("users").child(userNameText).setValue(signUpUserModel);
                    Bundle bundle = new Bundle();
                    bundle.putString("addedUserName",userNameText);
                    Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_addProfilePictureFragment,bundle);
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
    void Init(View view){
        signUpButton = view.findViewById(R.id.signupBtn);
        fullName = view.findViewById(R.id.checkFullName);
        userName = view.findViewById(R.id.checkUserName);
        password = view.findViewById(R.id.checkPassword);
        AlreadyUser = view.findViewById(R.id.AlreadyUser);
    }

    void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}