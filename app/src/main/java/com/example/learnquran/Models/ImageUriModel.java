package com.example.learnquran.Models;

import android.net.Uri;

public class ImageUriModel {
    Uri imageUri;

    public ImageUriModel(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
