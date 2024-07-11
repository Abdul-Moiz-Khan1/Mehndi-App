package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Adapters.FullScreenImageAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewImagesActivity extends AppCompatActivity {
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);

        viewPager = findViewById(R.id.viewPager);

        TextView titleTextView = findViewById(R.id.titleTextView);
        ImageView backIcon = findViewById(R.id.backIcon);

        // Get the text passed through intent
        Intent intent = getIntent();
        String text = intent.getStringExtra("text");

        // Set the text to the TextView
        titleTextView.setText(text);

        // Load images from the specified folder based on the text
        String folderName = getFolderNameForText(text);
        List<String> imagePaths = loadImagesForFolder(folderName);

        // Set up ViewPager with the loaded images
        FullScreenImageAdapter adapter = new FullScreenImageAdapter(this, imagePaths);
        viewPager.setAdapter(adapter);

        // Set the current item in ViewPager
        int position = getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position);

        // Initialize ScaleGestureDetector
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Disable default touch event handling to prevent interference
        viewPager.setOnTouchListener((view, motionEvent) -> {
            scaleGestureDetector.onTouchEvent(motionEvent);
            return false;
        });


    }


    private String getFolderNameForText(String text) {
        String folderName = ""; // Default folder name
        if (text.equals("Latest Designs")) {
            folderName = "latest design";
        } else if (text.equals("Arabic Designs")) {
            folderName = "Arabic design";
        } else if (text.equals("Bengali Designs")) {
            folderName = "Bengali design";
        } else if (text.equals("Bridal Designs")) {
            folderName = "Bridal design";
        } else if (text.equals("Kids Designs")) {
            folderName = "kids design";
        } else if (text.equals("Leg Designs")) {
            folderName = "legs design";
        } else if (text.equals("Alphabetic Designs")) {
            folderName = "Alphabetic design";
        } else if (text.equals("Goltiki Designs")) {
            folderName = "Goltiki design";
        } else if (text.equals("Pakistani Designs")) {
            folderName = "Pakistani Design";
        } else if (text.equals("Indian Designs")) {
            folderName = "Indian design";
        } else if (text.equals("Backhand Designs")) {
            folderName = "Backhand design";
        } else if (text.equals("Finger Designs")) {
            folderName = "finger design";
        } else if (text.equals("Foot Designs")) {
            folderName = "foot design";
        }
        return folderName;
    }

    private List<String> loadImagesForFolder(String folderName) {
        List<String> imagePaths = new ArrayList<>();
        try {
            String[] imageFiles = getAssets().list(folderName);
            if (imageFiles != null) {
                for (String file : imageFiles) {
                    // Construct full path
                    String path = folderName + "/" + file;
                    imagePaths.add(path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePaths;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            // Limit the scale factor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            viewPager.setScaleX(mScaleFactor);
            viewPager.setScaleY(mScaleFactor);
            return true;
        }
    }
}
