package com.example.learnquran.OnlineVideoFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.learnquran.Activities.OnlineVideoActivity;
import com.example.learnquran.Models.ImageUriModel;
import com.example.learnquran.Models.SignUpUserModel;
import com.example.learnquran.R;
import com.example.learnquran.connection.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc.Constants;


public class LoginDashBoardFragment extends Fragment {
    ImageView ProfileDetails;
    TextInputLayout tvChannelName , tvUserName;
    TextInputEditText validChannel;
    RadioButton cbQari , cbStudent;
    Button btnJoinChannel;
    String userName;
    String fullName;
    int clientRole;
    MaterialTextView tvSelectRole;
    ImageUriModel imageUriModel;
    // Firebase Database
    DatabaseReference databaseReference;
    String fullNameFromDb, passwordFromDb;
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
        // Go to Profile Details
        setProfileDetailsOnClick(view);
        setBtnJoinChannelOnClick();
        getImageDownloadUrl();
        InternetConnection.checkInternet(getContext(),getActivity());
        checkRole();
        getDataFromDatabase();
        return view;
    }
    void Init(View view){
        ProfileDetails = view.findViewById(R.id.imageViewProfile);
        tvChannelName = view.findViewById(R.id.tvChannelName);
        tvUserName = view.findViewById(R.id.tvUserName);
        btnJoinChannel = view.findViewById(R.id.btnJoinChannel);
        imageUriModel = new ImageUriModel();
        validChannel = view.findViewById(R.id.edValidChannel);
        cbQari = view.findViewById(R.id.cbQari);
        cbStudent = view.findViewById(R.id.cbStudent);
        tvSelectRole = view.findViewById(R.id.tvSelectRole);

        // firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    void checkRole(){
        cbQari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientRole = Constants.CLIENT_ROLE_BROADCASTER;
                cbStudent.setChecked(false);
            }
        });
        cbStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientRole = Constants.CLIENT_ROLE_AUDIENCE;
                cbQari.setChecked(false);

            }
        });
    }
    void setProfileDetailsOnClick(View view){
        ProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userNameDb",userName);
                bundle.putString("fullName",fullNameFromDb);
                bundle.putString("password",passwordFromDb);
                bundle.putString("imageUri",imageUriModel.getImageUri().toString());
                Navigation.findNavController(view).navigate(R.id.action_loginDashBoardFragment_to_profileDetailsFragment,bundle);
            }
        });
    }

    void setBtnJoinChannelOnClick(){
        btnJoinChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateChannelName()) {
                    return;
                } else if(!validateRole())
                    return;
                else {
                    getJson();
                }
            }
        });
    }

    void getImageDownloadUrl(){
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            StorageReference imagePath = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child("image/"+userName);
            imagePath.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            Glide.with(getContext())
                                    .load(uri)
                                    .error("error")
                                    .into(ProfileDetails);
                            imageUriModel.setImageUri(uri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("SignUp", "onFailure: " + e );
                           try {
                               progressDialog.dismiss();
                               ProfileDetails.setImageResource(R.drawable.person);
                               imageUriModel.setImageUri(Uri.parse("https://cdn.icon-icons.com/icons2/2506/PNG/512/user_icon_150670.png"));
                           }catch (Exception exception){
                               Log.e("SignUp", "onFailure: " + exception );
                           }
                        }
                    });
    }
    void getJson(){
        String channelName = tvChannelName.getEditText().getText().toString().trim();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Joining...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = "https://agora-node-tokenserver.muhammad-waqas4.repl.co/rtc/"+ channelName +"/publisher/uid/0";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    String token = response.getString("rtcToken");
                    Intent intent = new Intent(getContext(), OnlineVideoActivity.class);
                    intent.putExtra("userName",userName);
                    intent.putExtra("channelName",channelName);
                    intent.putExtra("token",token);
                    intent.putExtra("role",clientRole);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                showToast("Fail to get data..");
            }
        });
        queue.add(jsonObjectRequest);
    }

    boolean validateChannelName(){
        String channelName = validChannel.getText().toString().trim();
        if(channelName.isEmpty()){
            tvChannelName.setError("Required");
            return false;
        }else if(channelName.length() < 4){
            tvChannelName.setError("at least 4 character");
            return false;
        }else if(channelName.length() > 10){
            tvChannelName.setError("channel name should be less than 10 char");
            return false;
        }else
        {
            tvChannelName.setError(null);
            return true;
        }
    }

    boolean validateRole(){
        if(!cbQari.isChecked() && !cbStudent.isChecked()){
            tvSelectRole.setError("Required");
            return false;
        }else{
            tvSelectRole.setError(null);
            return true;
        }
    }
    void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    void getDataFromDatabase(){
        databaseReference.child("users").child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SignUpUserModel signUpUserModelFromDB = snapshot.getValue(SignUpUserModel.class);
                if(signUpUserModelFromDB != null)
                {
                    fullNameFromDb = signUpUserModelFromDB.getFullName();
                    passwordFromDb = signUpUserModelFromDB.getPassword();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}