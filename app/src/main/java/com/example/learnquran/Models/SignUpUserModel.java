package com.example.learnquran.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SignUpUserModel {
    private String fullName;
    private String userName;
    private String password;
    public SignUpUserModel(){
    }

    public SignUpUserModel(String fullName, String userName, String password) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
