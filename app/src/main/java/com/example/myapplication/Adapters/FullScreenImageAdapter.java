package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.example.myapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                download_image(imagePaths.get(position));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage(imagePaths.get(position));
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

    private void shareImage(String filePath) {
        try {
            // Extract the file name from the path
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

            // Create the output file path
            File file = new File(Environment.getExternalStorageDirectory(), fileName);

            if (!file.exists()) {
                download_image(filePath);
            }

            Uri fileUri = FileProvider.getUriForFile(context,   context.getApplicationContext().getPackageName() + ".provider", file);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Share Image"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Sharing Failed", Toast.LENGTH_SHORT).show();
        }

    }

    private void download_image(String filePath) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            // Use filePath to open the image
            in = assetManager.open(filePath);

            // Extract the file name from the path
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

            // Create the output file before writing
            File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            Toast.makeText(context, "Image Downloaded: " + outFile.getPath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

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
