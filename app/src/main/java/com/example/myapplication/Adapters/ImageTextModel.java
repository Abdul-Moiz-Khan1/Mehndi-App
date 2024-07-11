package com.example.myapplication.Adapters;

public class ImageTextModel {
    private int imageRes;
    private String text;

    public ImageTextModel(int imageRes, String text) {
        this.imageRes = imageRes;
        this.text = text;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getText() {
        return text;
    }
}
