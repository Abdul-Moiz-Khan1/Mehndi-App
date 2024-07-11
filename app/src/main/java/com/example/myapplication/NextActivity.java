package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ImageTextAdapter;
import com.example.myapplication.Adapters.ImageTextModel;
import com.example.myapplication.Adapters.MehndiImage;
import com.example.myapplication.Adapters.MehndiImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NextActivity extends AppCompatActivity {

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

        capturedImageView = findViewById(R.id.capturedImageView);
        overlayImageView = findViewById(R.id.overlayImageView);
        rotateIcon = findViewById(R.id.rotateIcon);
        backArrow = findViewById(R.id.backArrow);

        recyclerView2 = findViewById(R.id.recyclerView2);
        dataList2 = new ArrayList<>();
        adapter2 = new MehndiImageAdapter(this, dataList2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        dataList.add(new ImageTextModel(R.drawable.latest_icon, "Latest Designs"));
        dataList.add(new ImageTextModel(R.drawable.gol_icon, "Goltiki Designs"));
        dataList.add(new ImageTextModel(R.drawable.arabic_icon, "Arabic Designs"));
        dataList.add(new ImageTextModel(R.drawable.pakistan_icon, "Pakistani Designs"));
        dataList.add(new ImageTextModel(R.drawable.bangal_icon, "Bengali Designs"));
        dataList.add(new ImageTextModel(R.drawable.indian_icon, "Indian Designs"));
        dataList.add(new ImageTextModel(R.drawable.bridal_icon, "Bridal Designs"));
        dataList.add(new ImageTextModel(R.drawable.back_hand_icon, "Backhand Designs"));
        dataList.add(new ImageTextModel(R.drawable.kids_icon, "Kids Designs"));
        dataList.add(new ImageTextModel(R.drawable.finger_icon, "Finger Designs"));
        dataList.add(new ImageTextModel(R.drawable.leg_icon, "Leg Designs"));
        dataList.add(new ImageTextModel(R.drawable.foot_icon, "Foot Designs"));
        dataList.add(new ImageTextModel(R.drawable.alpha_icon, "Alphabetic Designs"));

        adapter = new ImageTextAdapter(this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        // Handle item click to switch to second RecyclerView
        adapter.setOnItemClickListener(new ImageTextAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String folderName = dataList.get(position).getText();
                String folderNameInAssets = getFolderNameFromText(folderName);
                recyclerView.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);
                backArrow.setVisibility(View.VISIBLE);
                try {
                    String[] images = getAssets().list(folderNameInAssets);
                    dataList2.clear();
                    for (String imageName : images) {
                        InputStream inputStream = getAssets().open(folderNameInAssets + "/" + imageName);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        dataList2.add(new MehndiImage(NextActivity.this, bitmap, imageName, folderName));
                    }
                    adapter2.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(NextActivity.this, "Error loading designs", Toast.LENGTH_SHORT).show();
                }
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

        // Handle item click in second RecyclerView to load image
        adapter2.setOnItemClickListener(new MehndiImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String imageName = dataList2.get(position).getImageName();
                String folderName = dataList2.get(position).getFolderName();
                if (imageName != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderName + "/" + imageName);
                    storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            overlayImageView.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(NextActivity.this, "Failed to load image from Firebase Storage" + folderName, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(NextActivity.this, "Image name is null", Toast.LENGTH_SHORT).show();
                }
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

        try {
            InputStream inputStream = getAssets().open("Alphabetic design/1.webp");
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
