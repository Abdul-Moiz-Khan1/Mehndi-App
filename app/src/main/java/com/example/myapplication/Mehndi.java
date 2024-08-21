package com.example.myapplication;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.DatabaseHelper;
import com.example.myapplication.Adapters.ImageAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mehndi extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imagePaths;
    private TextView selectedTextView;
    private DatabaseHelper databaseHelper;
    private String text; // Variable to hold the selected text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mehndi);

        checkpermissions();
        recyclerView = findViewById(R.id.recycler_view);
        selectedTextView = findViewById(R.id.selected_text_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));

        databaseHelper = new DatabaseHelper(this);

        // Get the selected text from intent
        text = getIntent().getStringExtra("text");
        selectedTextView.setText(text); // Set the selected text on TextView

        loadImagesForText(text); // Load images based on the selected text
    }

    private void checkpermissions() {

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1001);
        }
        else {
            return;
        }
    }

    private void loadImagesForText(String text) {
        imagePaths = new ArrayList<>();
        // Your existing logic to load images
        // ...

        AssetManager assetManager = getAssets();
        String folderName = "";

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

        loadImagesFromFolder(folderName);
        // Set up adapter
        imageAdapter = new ImageAdapter(imagePaths);
        imageAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(imageAdapter);
    }

    private void loadImagesFromFolder(String folderName) {
        if (!folderName.isEmpty()) {
            AssetManager assetManager = getAssets();
            try {
                String[] imageFiles = assetManager.list(folderName);
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
        }
    }

    @Override
    public void onImageClick(int position) {
        String imagePath = imagePaths.get(position);
        // Pass the selected image position and text to ViewImagesActivity
        Intent intent = new Intent(this, ViewImagesActivity.class);
        intent.putExtra("text", text);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(int position) {
        String imagePath = imagePaths.get(position);
        // Toggle favorite status
        toggleFavorite(imagePath);
        // Update UI
        imageAdapter.notifyItemChanged(position);
    }

    private void toggleFavorite(String imagePath) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM favorites WHERE imagePath = ?", new String[]{imagePath});
        if (cursor.getCount() > 0) {
            // Image already favorited, remove from favorites
            db.delete("favorites", "imagePath = ?", new String[]{imagePath});
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            // Image not favorited, add to favorites
            ContentValues values = new ContentValues();
            values.put("imagePath", imagePath);
            db.insert("favorites", null, values);
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
            return;
        }else {
            checkpermissions();
        }
    }
}
