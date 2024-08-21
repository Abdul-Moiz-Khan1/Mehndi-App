package com.example.myapplication.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FullScreenImageAdapter extends PagerAdapter {

    private Context context;
    private List<String> imagePaths;
    private ImageView currentImageView;// Added to keep track of the current ImageView
    private ImageButton download;
    private ImageButton share;
    public FullScreenImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_fullscreen_image, container, false);

        ImageView imageView = view.findViewById(R.id.image_full);
        currentImageView = imageView; // Set currentImageView to the newly created ImageView
        download = view.findViewById(R.id.download_button);
        share = view.findViewById(R.id.share_button);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Load image from assets
        try {
            InputStream inputStream = context.getAssets().open(imagePaths.get(position));
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    // Method to get the current ImageView
    public ImageView getCurrentImageView() {
        return currentImageView;
    }
}
