package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.CustomAdapter;
import com.example.myapplication.Adapters.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class See_More extends AppCompatActivity {
    private RecyclerView rec_view;
    private CustomAdapter adapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);
        rec_view = findViewById(R.id.rec_view);
        toolbar = findViewById(R.id.toolbar2);

        rec_view.setLayoutManager(new GridLayoutManager(this , 3));

        List<ItemModel> data = new ArrayList<>();
        data.add(new ItemModel(R.drawable.latest_icon, "Latest Designs"));
        data.add(new ItemModel(R.drawable.gol_icon, "Goltiki Designs"));
        data.add(new ItemModel(R.drawable.arabic_icon, "Arabic Designs"));
        data.add(new ItemModel(R.drawable.pakistan_icon, "Pakistani Designs"));
        data.add(new ItemModel(R.drawable.bangal_icon, "Bengali Designs"));
        data.add(new ItemModel(R.drawable.indian_icon, "Indian Designs"));
        data.add(new ItemModel(R.drawable.bridal_icon, "Bridal Designs"));
        data.add(new ItemModel(R.drawable.back_hand_icon, "Backhand Designs"));
        data.add(new ItemModel(R.drawable.kids_icon, "Kids Designs"));
        data.add(new ItemModel(R.drawable.finger_icon, "Finger Designs"));
        data.add(new ItemModel(R.drawable.leg_icon, "Leg Designs"));
        data.add(new ItemModel(R.drawable.foot_icon, "Foot Designs"));
        data.add(new ItemModel(R.drawable.alpha_icon, "Alphabetic Designs"));

        adapter = new CustomAdapter(this, data, new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Intent intent = new Intent(See_More.this, Mehndi.class);
                intent.putExtra("text", text);
                startActivity(intent);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rec_view.setAdapter(adapter);

    }


}