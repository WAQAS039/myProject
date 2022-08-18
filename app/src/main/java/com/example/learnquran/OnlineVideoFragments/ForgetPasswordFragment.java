package com.example.learnquran.OnlineVideoFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnquran.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgetPasswordFragment extends Fragment {
    TextInputLayout tipUserName;
    TextInputEditText edUserName;
    Button btnNext;
    DatabaseReference databaseReference;
    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);
        init(view);
        setBtnNext(view);
        return view;
    }

    void init(View view){
        tipUserName = view.findViewById(R.id.tipNewUserName);
        edUserName = view.findViewById(R.id.edNewUserName);
        btnNext = view.findViewById(R.id.btnCheckUserName);
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    void setBtnNext(View view){
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edUserName.getText().toString();
                if(!validateEmail()){
                    return;
                }else{
                    databaseReference.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot snapshot = task.getResult();
                            if(snapshot.hasChild(user)){
                                tipUserName.setError(null);
                                Bundle bundle = new Bundle();
                                bundle.putString("user",user);
                                Navigation.findNavController(view).navigate(R.id.action_forgetPasswordFragment_to_updatePasswordFragment,bundle);
                            }else{
                                tipUserName.setError("User Not Exist");
                            }
                        }
                    });
                }
            }
        });
    }

    boolean validateEmail() {
        String vEmail = edUserName.getText().toString().trim();
        if (vEmail.isEmpty()) {
            tipUserName.setError("Required");
            return false;
        } else if (!vEmail.startsWith("@")) {
            tipUserName.setError("Invalid Email");
            return false;
        } else {
            tipUserName.setError(null);
            return true;
        }
    }
}