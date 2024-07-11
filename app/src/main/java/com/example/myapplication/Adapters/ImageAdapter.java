package com.example.myapplication.Adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> imagePaths;
    private Context context;
    private DatabaseHelper databaseHelper;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onImageClick(int position);
        void onFavoriteClick(int position);
    }

    public ImageAdapter(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        databaseHelper = new DatabaseHelper(context);
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView favoriteImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            favoriteImageView = itemView.findViewById(R.id.imageView2);

            // Set OnClickListener for the imageView
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onImageClick(position);
                        }
                    }
                }
            });

            // Set OnClickListener for the favoriteImageView
            favoriteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onFavoriteClick(position);
                        }
                    }
                }
            });
        }

        public void bind(int position) {
            // Load image from assets
            try {
                InputStream inputStream = context.getAssets().open(imagePaths.get(position));
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageView.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Check if the image is in favorites
            String imagePath = imagePaths.get(position);
            if (isFavorite(imagePath)) {
                favoriteImageView.setImageResource(R.drawable.baseline_favorite_red_24);
            } else {
                favoriteImageView.setImageResource(R.drawable.baseline_favorite_24);
            }
        }

        private boolean isFavorite(String imagePath) {
            return databaseHelper.isFavorite(imagePath);
        }
    }
}
