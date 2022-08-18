package com.example.learnquran.Models;

import android.graphics.Bitmap;
import android.net.Uri;

public class OnlineUserModel {
    private String user;
    private String fullName;
    private String uId;
    private Uri imageUri;
    private boolean isMic;
    private int role;

    public OnlineUserModel(String user, String fullName, String uId, Uri imageUri, boolean isMic, int role) {
        this.user = user;
        this.fullName = fullName;
        this.uId = uId;
        this.imageUri = imageUri;
        this.isMic = isMic;
        this.role = role;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getuId() {
        return uId;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public boolean isMic() {
        return isMic;
    }

    public String getFullName() {
        return fullName;
    }

    public int getRole() {
        return role;
    }
}
