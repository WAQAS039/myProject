package com.example.learnquran.Models;

import android.graphics.Bitmap;
import android.net.Uri;

public class OnlineUserModel {
    private String user;
    private String uId;
    private Uri imageUri;
    private boolean isMic;

    public OnlineUserModel(String user, String uId, Uri imageUri, boolean isMic) {
        this.user = user;
        this.uId = uId;
        this.imageUri = imageUri;
        this.isMic = isMic;
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
}
