package com.example.myapplication.Adapters;

import android.graphics.Bitmap;

import com.example.myapplication.NextActivity;

public class MehndiImage {
    private Bitmap bitmap;
    private String imageName;
    private String folderName;

    public MehndiImage(NextActivity nextActivity, Bitmap bitmap,String imageName,String folderName) {
        this.bitmap = bitmap;
        this.imageName = imageName;
        this.folderName = folderName;

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getImageName() {
        return imageName;
    }
    public String getFolderName() {
        return folderName;
    }
}
