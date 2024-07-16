package com.example.myapplication.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;

public class MehndiImageAdapter extends RecyclerView.Adapter<MehndiImageAdapter.ViewHolder> {


    private List<MehndiImage> dataList;
    private Context context;
    private OnItemClickListener Listener;

    public MehndiImageAdapter(Context context, List<MehndiImage> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.Listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mehndi_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int pos = position;
        Log.d("imaged set ?? ", "obv");
        MehndiImage item = dataList.get(position);
        Glide.with(context)
                .load(item.getUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.imageView);
//        holder.imageView.setImageBitmap(item.getBitmap());

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (Listener != null) {
                    Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                    Listener.onItemClick(position);
                } else {
                    Toast.makeText(context, "listener null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_mehndi);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}
