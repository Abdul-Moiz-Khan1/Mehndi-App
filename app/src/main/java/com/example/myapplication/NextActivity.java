package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ImageTextAdapter;
import com.example.myapplication.Adapters.ImageTextModel;
import com.example.myapplication.Adapters.MehndiImage;
import com.example.myapplication.Adapters.MehndiImageAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NextActivity extends AppCompatActivity {

    //my new temp vars
    FirebaseStorage storage;
    StorageReference storageReference;
    private List<Uri> images_uri = new ArrayList<>();
    int pos;


    private ImageView capturedImageView;
    private ImageView overlayImageView;
    private ImageView rotateIcon;
    private ImageView backArrow;
    private Bitmap capturedImage;
    private Bitmap overlayImage;
    private PointF lastTouch = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int ROTATE = 3;
    private int mode = NONE;
    private float oldDist = 1f;
    private PointF mid = new PointF();
    private float startAngle = 0f;


    private RecyclerView recyclerView;
    private ImageTextAdapter adapter;
    private List<ImageTextModel> dataList;
    private RecyclerView recyclerView2;
    private MehndiImageAdapter adapter2;
    private List<MehndiImage> dataList2;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        //my temp work

        storage = FirebaseStorage.getInstance();

        capturedImageView = findViewById(R.id.capturedImageView);
        overlayImageView = findViewById(R.id.overlayImageView);
        rotateIcon = findViewById(R.id.rotateIcon);
        backArrow = findViewById(R.id.backArrow);

        recyclerView2 = findViewById(R.id.recyclerView2);
        dataList2 = new ArrayList<>();

        recyclerView2.setVisibility(View.GONE);
        adapter2 = new MehndiImageAdapter(this, dataList2);
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();

        dataList.add(new ImageTextModel(R.drawable.tatto, "Tatto"));
        dataList.add(new ImageTextModel(R.drawable.alphabet, "Alphabets"));
        adapter = new ImageTextAdapter(this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);



        // Handle item click to switch to second RecyclerView
        adapter.setOnItemClickListener(new ImageTextAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(int position) {

                //my work
                recyclerView.setVisibility(View.GONE);
                recyclerView2.setAdapter(adapter2);
                recyclerView2.setVisibility(View.VISIBLE);
                backArrow.setVisibility(View.VISIBLE);
//                setitems(dataList.get(position).getText().toLowerCase());
                dataList2.clear();
                images_uri.clear();
                try {
                    storageReference = storage.getReference().child(dataList.get(position).getText().toLowerCase());
                    storageReference.listAll().addOnSuccessListener(listResult -> {
                        for (StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(uri -> {
                                dataList2.add(new MehndiImage(NextActivity.this, uri.toString(), item.getName(), dataList.get(position).getText().toLowerCase()));
                                images_uri.add(uri);
                                Log.d("image added", uri.toString());
                                Log.d("image added", String.valueOf(dataList2.size()));
                                adapter2.notifyDataSetChanged();
                            }).addOnFailureListener(e -> {
                                Log.e("Error while Downloading", "Cannot download", e);
                            });
                        }
                    });

                } catch (Exception e) {
                    Log.e("Refrence Error", " Cannot Access Firebase", e);
                }

            }
        });

        adapter2.setOnItemClickListener2(new MehndiImageAdapter.OnItemClickListener2() {
            @Override
            public void onItemClick2(int position) {
                Log.d("in view cccc", "more to com");
          Toast.makeText(NextActivity.this, "into click viw", Toast.LENGTH_SHORT).show();

            }
        });

        // Handle back arrow click to switch back to first RecyclerView
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView2.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                backArrow.setVisibility(View.GONE);
            }
        });

        rotateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });

        Uri imageUri = getIntent().getParcelableExtra("capturedImageUri");
        if (imageUri != null) {
            capturedImage = BitmapFactory.decodeFile(new File(imageUri.getPath()).getAbsolutePath());
            capturedImageView.setImageBitmap(capturedImage);
        } else {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }

//        set_overlay_image();
    }

    private void set_overlay_image() {
        try {

//            InputStream inputStream = getAssets().open("Alphabetic design/1.webp");
            InputStream inputStream = getContentResolver().openInputStream(images_uri.get(pos));
            overlayImage = BitmapFactory.decodeStream(inputStream);
            overlayImageView.setImageBitmap(overlayImage);

            overlayImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ImageView view = (ImageView) v;
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            savedMatrix.set(matrix);
                            lastTouch.set(event.getX(), event.getY());
                            mode = DRAG;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            if (event.getPointerCount() == 2) {
                                startAngle = angleBetweenTwoFingers(event);
                                mode = ROTATE;
                            }
                            oldDist = spacing(event);
                            if (oldDist > 10f) {
                                savedMatrix.set(matrix);
                                midPoint(mid, event);
                                mode = ZOOM;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {
                                matrix.set(savedMatrix);
                                matrix.postTranslate(event.getX() - lastTouch.x, event.getY() - lastTouch.y);
                            } else if (mode == ZOOM) {
                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    matrix.set(savedMatrix);
                                    float scale = newDist / oldDist;
                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                }
                            } else if (mode == ROTATE) {
                                float currentAngle = angleBetweenTwoFingers(event);
                                float rotation = currentAngle - startAngle;
                                matrix.postRotate(rotation, view.getWidth() / 2, view.getHeight() / 2);
                                startAngle = currentAngle;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            break;
                        case MotionEvent.ACTION_POINTER_2_UP:
                            mode = NONE;
                            break;
                    }
                    view.setImageMatrix(matrix);
                    return true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading overlay image", Toast.LENGTH_SHORT).show();
        }
    }


    private void setitems(String folder) {
        dataList2.clear();
        images_uri.clear();
        try {
            storageReference = storage.getReference().child(folder);
            storageReference.listAll().addOnSuccessListener(listResult -> {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(uri -> {
                        dataList2.add(new MehndiImage(this, uri.toString(), item.getName(), folder));
                        images_uri.add(uri);
                        Log.d("image added", uri.toString());
                        Log.d("image added", String.valueOf(dataList2.size()));
                        adapter2.notifyDataSetChanged();
                    }).addOnFailureListener(e -> {
                        Log.e("Error while Downloading", "Cannot download", e);
                    });
                }
            });

        } catch (Exception e) {
            Log.e("Refrence Error", " Cannot Access Firebase", e);
        }

    }
    private String getFolderNameFromText(String text) {
        switch (text) {
            case "Latest Designs":
                return "latest design";
            case "Arabic Designs":
                return "Arabic design";
            case "Bengali Designs":
                return "Bengali design";
            case "Bridal Designs":
                return "Bridal design";
            case "Kids Designs":
                return "kids design";
            case "Leg Designs":
                return "legs design";
            case "Alphabetic Designs":
                return "Alphabetic design";
            case "Goltiki Designs":
                return "Goltiki design";
            case "Pakistani Designs":
                return "Pakistani Design";
            case "Indian Designs":
                return "Indian design";
            case "Backhand Designs":
                return "Backhand design";
            case "Finger Designs":
                return "finger design";
            case "Foot Designs":
                return "foot design";

            default:
                return "";
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float angleBetweenTwoFingers(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    public void rotateImage() {
        if (overlayImageView != null) {
            matrix.postRotate(45, overlayImageView.getWidth() / 2, overlayImageView.getHeight() / 2);
            overlayImageView.setImageMatrix(matrix);
        }
    }

}
