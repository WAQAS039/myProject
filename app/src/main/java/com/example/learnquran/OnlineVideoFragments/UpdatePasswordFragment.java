package com.example.learnquran.OnlineVideoFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnquran.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdatePasswordFragment extends Fragment {
    Button btnUpdate;
    TextInputLayout password,conformPassword;
    String userName;
    DatabaseReference databaseReference;

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        init(view);
        setBtnUpdate(view);
        return view;
    }
    void init(View view){
        btnUpdate = view.findViewById(R.id.btnUpdateNewPassword);
        password = view.findViewById(R.id.tipProvidePassword);
        conformPassword = view.findViewById(R.id.tipProvideRePassword);
        userName = getArguments().getString("user");
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    void setBtnUpdate(View view){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getEditText().getText().toString().trim();
                if(!validatePassword() | !validateRePassword()){
                    return;
                }else{
                    databaseReference.child("users").child(userName).child("password").setValue(pass);
                    Toast.makeText(getContext(), "SuccessFully Updated", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_updatePasswordFragment_to_loginFragment);
                }
            }
        });
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