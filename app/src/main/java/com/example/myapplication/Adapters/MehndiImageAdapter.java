package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private OnItemClickListener2 Listener;

    public MehndiImageAdapter(Context context , List<MehndiImage> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    public void setOnItemClickListener2(OnItemClickListener2 listener) {
        this.Listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mehndi_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
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
                    Listener.onItemClick2(position);
                }else {
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

    public interface OnItemClickListener2 {
        void onItemClick2(int position);
    }


}
