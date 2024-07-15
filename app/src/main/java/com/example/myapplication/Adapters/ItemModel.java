package com.example.myapplication.Adapters;



// ItemModel.java
public class ItemModel {
    private int imageResource;
    private String text;

    public ItemModel(int imageResource, String text) {
        this.imageResource = imageResource;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {
        return text;
    }
}
