package com.example.myapplication.Adapters;

import com.example.myapplication.NextActivity;

public class MehndiImage {
    private String imageName;
    private String folderName;
    private String url;



    public MehndiImage(NextActivity nextActivity, String url, String imageName, String folderName) {

        this.imageName = imageName;
        this.folderName = folderName;
        this.url = url;
    }

    public String getImageName() {
        return imageName;
    }
    public String getFolderName() {
        return folderName;
    }
    public String getUrl() {return url;}


}
