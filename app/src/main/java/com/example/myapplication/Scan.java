package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Scan extends AppCompatActivity {
    private PreviewView previewView;
    private ProgressDialog mProgressDialog;
    private ImageButton capture, toggleFlash, flipCamera;
    private LottieAnimationView animationView;
    private int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        startCamera(cameraFacing);
                    }
                }
            });
    private ImageLabeler labeler;
    private Camera camera;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        previewView = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);
        toggleFlash = findViewById(R.id.toggleFlash);
        flipCamera = findViewById(R.id.flipCamera);
        animationView = findViewById(R.id.animationView);

        animationView.playAnimation();

        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        if (ContextCompat.checkSelfPermission(Scan.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                } else {
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                }
                startCamera(cameraFacing);
            }
        });

        toggleFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFlashIcon();
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Scan.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                animationView.pauseAnimation();
                animationView.setVisibility(View.GONE);
                mProgressDialog = new ProgressDialog(Scan.this);
                mProgressDialog.setTitle("Please Wait...");
                mProgressDialog.setMessage("Detecting...");
                mProgressDialog.show();

                takePicture();
            }
        });
    }

    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture() {
        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                Bitmap bitmap = imageProxyToBitmap(image);
                image.close();
                doInference(bitmap);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                Log.e("ImageCapture", "Error capturing image: " + exception.getMessage());
            }
        });
    }

    private void doInference(Bitmap input) {
        InputImage image = InputImage.fromBitmap(input, 0);

        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        boolean moveToNextActivity = false;
                        boolean isHandNailSkinFlesh = false;
                        boolean isFootNailSkinFlesh = false;

                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            if (text.equalsIgnoreCase("Hand") || text.equalsIgnoreCase("Nail") ||
                                    text.equalsIgnoreCase("Skin") || text.equalsIgnoreCase("Flesh")) {
                                isHandNailSkinFlesh = true;
                            }
                            if (text.equalsIgnoreCase("Foot") || text.equalsIgnoreCase("Nail") ||
                                    text.equalsIgnoreCase("Skin") || text.equalsIgnoreCase("Flesh")) {
                                isFootNailSkinFlesh = true;
                            }
                        }

                        if (isHandNailSkinFlesh || isFootNailSkinFlesh) {
                            moveToNextActivity = true;
                        }

                        if (moveToNextActivity) {
                            // Correct the orientation of the bitmap if needed
                            Matrix matrix = new Matrix();
                            matrix.postRotate(getRotationAngle(input.getWidth(), input.getHeight()));
                            Bitmap rotatedBitmap = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), matrix, true);

                            // Save the corrected bitmap to a file
                            File file = saveBitmapToFile(rotatedBitmap);
                            if (file != null) {
                                dismissDialogAndMoveToNextActivity(file);
                            } else {
                                Toast.makeText(Scan.this, "Error saving image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mProgressDialog.dismiss();
                            animationView.setVisibility(View.VISIBLE);
                            animationView.playAnimation();
                            Toast.makeText(Scan.this, "Combination not detected", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ML Kit", "Image labeling failed: " + e.getMessage());
                        Toast.makeText(Scan.this, "Image labeling failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void dismissDialogAndMoveToNextActivity(File file) {
        mProgressDialog.dismiss();

        if (camera != null && camera.getCameraInfo().hasFlashUnit() && camera.getCameraInfo().getTorchState().getValue() == 1) {
            camera.getCameraControl().enableTorch(false);
        }

        if (!isFinishing()) {
            Intent intent = new Intent(Scan.this, NextActivity.class);
            intent.putExtra("capturedImageUri", Uri.fromFile(file));
            startActivity(intent);
            finish();
        }
    }

    private int getRotationAngle(int width, int height) {
        int rotationAngle = 0;
        if (width > height) {
            rotationAngle = 90;
        }
        return rotationAngle;
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "captured_image.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void setFlashIcon() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                camera.getCameraControl().enableTorch(true);
                toggleFlash.setImageResource(R.drawable.baseline_flash_off_24);
            } else {
                camera.getCameraControl().enableTorch(false);
                toggleFlash.setImageResource(R.drawable.baseline_flash_on_24);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Scan.this, "Flash is not available currently", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }
}