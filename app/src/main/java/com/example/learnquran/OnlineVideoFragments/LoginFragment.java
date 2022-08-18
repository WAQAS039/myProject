package com.example.learnquran.OnlineVideoFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class LoginFragment extends Fragment {
    TextView signUp,forgetPassword;
    Button LoginButton;
    TextInputLayout email, password;
    TextInputEditText edEmail, edPassword;
    DatabaseReference databaseReference;
    String userName;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Init(view);
        getSavedUserName();
        if (!userName.equals("logout")) {
            Bundle bundle = new Bundle();
            bundle.putString("userName", userName);
            Navigation.findNavController(getParentFragment().getView()).navigate(R.id.action_loginFragment_to_loginDashBoardFragment, bundle);
        } else {
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment);
                }
            });
            LoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailFromUser = email.getEditText().getText().toString().trim();
                    String passwordTxt = password.getEditText().getText().toString().trim();
                    if (!validateEmail() | !validatePassword())
                        return;
                    else {
                        databaseReference.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    showToast(task.getException() + "");
                                } else {
                                    DataSnapshot snapshot = task.getResult();
                                    SignUpUserModel signUpUserModel = snapshot.child(emailFromUser).getValue(SignUpUserModel.class);
                                    if (signUpUserModel != null) {
                                        email.setError(null);
                                        String emailFromDb = signUpUserModel.getUserName();
                                        String passwordFromDb = signUpUserModel.getPassword();
                                        if (passwordFromDb.equals(passwordTxt)) {
                                            password.setError(null);
                                            showToast("Login");
                                            Bundle bundle = new Bundle();
                                            bundle.putString("userName", emailFromDb);
                                            saveUserName(emailFromDb);
                                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_loginDashBoardFragment, bundle);
                                        } else {
                                            password.setError("Incorrect Password");
                                        }
                                    } else {
                                        email.setError("User Not Exist");
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
        InternetConnection.checkInternet(getContext(), getActivity());
        setForgetPassword(view);
        return view;
    }


    void Init(View view) {
        signUp = view.findViewById(R.id.signUp);
        LoginButton = view.findViewById(R.id.LoginBtn);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        edEmail = view.findViewById(R.id.validEmail);
        edPassword = view.findViewById(R.id.validPassword);
        forgetPassword = view.findViewById(R.id.forgetPassword);
        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://userdatabase-e2d30-default-rtdb.firebaseio.com");

        if (getArguments() != null) {
            String data = getArguments().getString("logoutUser");
            if (data != null) {
                saveUserName(data);
            }
        }
    }


    void setForgetPassword(View view){
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgetPasswordFragment);
            }
        });
    }
    void saveUserName(String userName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("saveUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saveUserName", userName).apply();
    }

    void getSavedUserName() {
        SharedPreferences preferences = getActivity().getSharedPreferences("saveUser", Context.MODE_PRIVATE);
        userName = preferences.getString("saveUserName", "logout");
    }

    boolean validateEmail() {
        String vEmail = edEmail.getText().toString().trim();
        if (vEmail.isEmpty()) {
            email.setError("Required");
            return false;
        } else if (!vEmail.startsWith("@")) {
            email.setError("Invalid Email");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    boolean validatePassword() {
        String vPass = edPassword.getText().toString().trim();
        if (vPass.isEmpty()) {
            password.setError("Required");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}