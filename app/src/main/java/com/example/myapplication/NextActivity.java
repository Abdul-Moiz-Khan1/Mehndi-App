package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NextActivity extends AppCompatActivity {

    //my new temp vars
    FirebaseStorage storage;
    StorageReference storageReference;
    private List<Uri> images_uri = new ArrayList<>();
    int pos;
    InputStream inputStream;
    ProgressDialog progressDialog;
    Boolean fetched;
    private ImageView flip_vertical;
    private ImageView flip_horizontal;
    private ImageView delete_design;
    private Boolean flipped_vert = false;
    private Boolean flipped_hor = false;

    private ImageView capturedImageView;
    private ImageView overlayImageView;
    private ImageView rotateIcon;
    private ImageView backArrow;
    private ImageView cancel;
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
    private float currentRotation = 0f;
    private float accumulatedRotation = 0f;


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

        flip_vertical = findViewById(R.id.horizontal);
        flip_horizontal = findViewById(R.id.vertical);
        flip_vertical.setOnClickListener(view -> {flip_vertically();});
        flip_horizontal.setOnClickListener(view -> {flip_horizontally();});

        capturedImageView = findViewById(R.id.capturedImageView);
        overlayImageView = findViewById(R.id.overlayImageView);
        rotateIcon = findViewById(R.id.rotateIcon);
        backArrow = findViewById(R.id.backArrow);
        cancel = findViewById(R.id.cancel);
        delete_design = findViewById(R.id.delete_design);

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

        delete_design.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(overlayImageView.getDrawable()==null){
                    Toast.makeText(NextActivity.this,"Please select some design" ,Toast.LENGTH_SHORT).show();
                }else{
                    overlayImageView.setImageDrawable(null);
                }

            }
        });

        // Handle item click to switch to second RecyclerView
        adapter.setOnItemClickListener(new ImageTextAdapter.OnItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemClick(int position) {

                //my work
                progressDialog = new ProgressDialog(NextActivity.this);
                showProgress();
                recyclerView.setVisibility(View.GONE);
                recyclerView2.setAdapter(adapter2);
                recyclerView2.setVisibility(View.VISIBLE);
                backArrow.setVisibility(View.VISIBLE);

                setitems(dataList.get(position).getText().toLowerCase());
            }
        });

        adapter2.setOnItemClickListener2(new MehndiImageAdapter.OnItemClickListener2() {
            @Override
            public void onItemClick2(int position) {
                pos = position;
                set_overlay_image();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                if(overlayImageView.getDrawable()==null){
                    Toast.makeText(NextActivity.this,"Please select some design" ,Toast.LENGTH_SHORT).show();
                }else{
                    rotateImage();
                }
            }
        });

        Uri imageUri = getIntent().getParcelableExtra("capturedImageUri");
        if (imageUri != null) {
            capturedImage = BitmapFactory.decodeFile(new File(imageUri.getPath()).getAbsolutePath());
            capturedImageView.setImageBitmap(capturedImage);
        } else {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void flip_horizontally() {
        if(overlayImageView.getDrawable()==null){
            Toast.makeText(NextActivity.this,"Please select some design" ,Toast.LENGTH_SHORT).show();
        }else{
            if(flipped_hor){
                overlayImageView.setRotationY(0);
                flipped_hor = false;
            }
            else{
                overlayImageView.setRotationY(180);
                flipped_hor = true;
            }
        }
    }
    private void flip_vertically() {
        if(overlayImageView.getDrawable()==null){
            Toast.makeText(NextActivity.this,"Please select some design" ,Toast.LENGTH_SHORT).show();
        }else{
            if(flipped_vert){
                overlayImageView.setRotationX(0);
                flipped_vert = false;
            }else {
                overlayImageView.setRotationX(180);
                flipped_vert = true;
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void set_overlay_image() {
        fetched = false;
            String imagename = dataList2.get(pos).getImageName();
            storageReference.child(imagename).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                   inputStream = new ByteArrayInputStream(bytes);
                    overlayImage = BitmapFactory.decodeStream(inputStream);
                    overlayImageView.setImageBitmap(overlayImage);
                   fetched = true;
                }
            }).addOnFailureListener(e->{
                Log.e("Error" , "Error while loading from firebase",e);
            });

        try {

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
                            oldDist = spacing(event);
                            if (event.getPointerCount() == 2) {
                                savedMatrix.set(matrix);
                                midPoint(mid, event);
                                startAngle = angleBetweenTwoFingers(event);
                                mode = ZOOM;
                            }
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {
                                matrix.set(savedMatrix);
                                matrix.postTranslate(event.getX() - lastTouch.x, event.getY() - lastTouch.y);
                            } else if (mode == ZOOM || mode == ROTATE) {
                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    matrix.set(savedMatrix);
                                    float scale = newDist / oldDist;
                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                }

                                if (event.getPointerCount() == 2) {
                                    float currentAngle = angleBetweenTwoFingers(event);
                                    float deltaRotation = currentAngle - startAngle;

                                    // Apply rotation only if the change is significant
                                    if (Math.abs(deltaRotation) > 2) {
                                        // Adjust the threshold if needed
                                        accumulatedRotation += deltaRotation;
                                        matrix.set(savedMatrix);
                                        matrix.postRotate(accumulatedRotation, mid.x, mid.y);
                                        startAngle = currentAngle; // Update startAngle only when rotation is applied
                                    }
//                                    startAngle = currentAngle;
                                }
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            // Save the matrix state, which includes the latest rotation and scale
                            savedMatrix.set(matrix);
                            mode = NONE;
                            break;
                    }

                    view.setImageMatrix(matrix);
                    return true;
//                    ImageView view = (ImageView) v;
//
//                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                        case MotionEvent.ACTION_DOWN:
//                            savedMatrix.set(matrix);
//                            lastTouch.set(event.getX(), event.getY());
//                            mode = DRAG;
//                            break;
//
//                        case MotionEvent.ACTION_POINTER_DOWN:
//                            oldDist = spacing(event);
//                            if (event.getPointerCount() == 2) {
//                                savedMatrix.set(matrix);
//                                midPoint(mid, event);
//                                startAngle = angleBetweenTwoFingers(event);
//                                mode = ZOOM;
//                            }
//                            break;
//
//                        case MotionEvent.ACTION_MOVE:
//                            if (mode == DRAG) {
//                                matrix.set(savedMatrix);
//                                matrix.postTranslate(event.getX() - lastTouch.x, event.getY() - lastTouch.y);
//                            } else if (mode == ZOOM || mode == ROTATE) {
//                                float newDist = spacing(event);
//                                if (newDist > 10f) {
//                                    matrix.set(savedMatrix);
//                                    float scale = newDist / oldDist;
//                                    matrix.postScale(scale, scale, mid.x, mid.y);
//                                }
//
//                                if (event.getPointerCount() == 2) {
//                                    float currentAngle = angleBetweenTwoFingers(event);
//                                    float deltaRotation = currentAngle - startAngle;
//                                    if (Math.abs(deltaRotation) > 1) { // Threshold to avoid jitter
//                                        accumulatedRotation += deltaRotation;
//                                        matrix.set(savedMatrix);
//                                        matrix.postRotate(accumulatedRotation, mid.x, mid.y);
//                                    }
//                                    startAngle = currentAngle; // Update startAngle for next move event
//                                }
//                            }
//                            break;
//
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_POINTER_UP:
//                            savedMatrix.set(matrix); // Save the matrix state including rotation and scale
//                            mode = NONE;
//                            break;
//                    }
//
//                    view.setImageMatrix(matrix);
//                    return true;

//                    ImageView view = (ImageView) v;
//                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                        case MotionEvent.ACTION_DOWN:
//                            savedMatrix.set(matrix);
//                            lastTouch.set(event.getX(), event.getY());
//                            mode = DRAG;
//                            break;
//                        case MotionEvent.ACTION_POINTER_DOWN:
//                            if (event.getPointerCount() == 2) {
//                                startAngle = angleBetweenTwoFingers(event);
//                                mode = ROTATE;
//                            }
//                            oldDist = spacing(event);
//                            if (oldDist > 10f) {
//                                savedMatrix.set(matrix);
//                                midPoint(mid, event);
//                                mode = ZOOM;
//                            }
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            if (mode == DRAG) {
//                                matrix.set(savedMatrix);
//                                matrix.postTranslate(event.getX() - lastTouch.x, event.getY() - lastTouch.y);
//                            } else if (mode == ZOOM) {
//                                float newDist = spacing(event);
//                                if (newDist > 10f) {
//                                    matrix.set(savedMatrix);
//                                    float scale = newDist / oldDist;
//                                    matrix.postScale(scale, scale, mid.x, mid.y);
//                                }
//                            } else if (mode == ROTATE) {
//                                float currentAngle = angleBetweenTwoFingers(event);
//                                float rotation = currentAngle - startAngle;
//                                matrix.postRotate(rotation, view.getWidth() / 2, view.getHeight() / 2);
//                                startAngle = currentAngle;
//                            }
//                            break;
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_POINTER_UP:
//                            mode = NONE;
//                            break;
//                        case MotionEvent.ACTION_POINTER_2_UP:
//                            mode = NONE;
//                            break;
//                    }
//                    view.setImageMatrix(matrix);
//                    return true;

                }
            });
        } catch (Exception e){
            Toast.makeText(this,e.getMessage() , Toast.LENGTH_SHORT).show();
            Log.e("Error" , "Error while overlaying Image" , e);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
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
                        Log.d("image added", item.getName());
                        Log.d("image added", String.valueOf(dataList2.size()));
                        adapter2.notifyDataSetChanged();
                    }).addOnFailureListener(e -> {
                        Log.e("Error while Downloading", "Cannot download", e);
                    });
                }
                progressDialog.dismiss();
            });
        } catch (Exception e) {
            Log.e("Refrence Error", " Cannot Access Firebase", e);
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

    private void showProgress() {
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("loading Designs");
        progressDialog.show();
    }

}
