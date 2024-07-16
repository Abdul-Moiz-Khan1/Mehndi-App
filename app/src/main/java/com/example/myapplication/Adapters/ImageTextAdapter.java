package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ImageTextAdapter extends RecyclerView.Adapter<ImageTextAdapter.ViewHolder> {

    private List<ImageTextModel> dataList;
    private Context context;
    private OnItemClickListener listener; // Add listener member variable

    public ImageTextAdapter(Context context, List<ImageTextModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ImageTextModel item = dataList.get(position);
        holder.imageView.setImageResource(item.getImageRes());
        holder.textView.setText(item.getText());

        // Set click listener for the item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_full);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    // Define interface for click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}

