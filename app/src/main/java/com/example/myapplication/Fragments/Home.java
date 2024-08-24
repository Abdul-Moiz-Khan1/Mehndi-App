package com.example.myapplication.Fragments;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.CustomAdapter;
import com.example.myapplication.Adapters.ItemModel;
import com.example.myapplication.Mehndi;
import com.example.myapplication.R;
import com.example.myapplication.See_More;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Home extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView recyclerView_recommendations;
    private CustomAdapter adapter;
    private CustomAdapter recommendataion_adapter;
    private TextView seemore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView_recommendations = view.findViewById(R.id.recommendation_rec_view);
        seemore = view.findViewById(R.id.seemore);
        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity() , See_More.class));
            }
        });

        // Set up grid layout manager with spacing between items
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_layout_spacing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity() ,LinearLayoutManager.HORIZONTAL,false ));
        recyclerView_recommendations.setLayoutManager(new LinearLayoutManager(getActivity() ,LinearLayoutManager.HORIZONTAL,false ));
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

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


        List<ItemModel> recommended_data = new ArrayList<>();
        recommended_data = data;
        Random random = new Random();
        int min=6,max=13;
        recommended_data = new ArrayList<>(recommended_data.subList(3 , (random.nextInt(max-min +1)+min)));
        if(recommended_data.size() < 2){
            recommended_data = data.subList(3,8);
        }
        Collections.shuffle(recommended_data);

        adapter = new CustomAdapter(getActivity(), data, new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Intent intent = new Intent(getActivity(), Mehndi.class);
                intent.putExtra("text", text);
                startActivity(intent);
            }
        });
        recommendataion_adapter = new CustomAdapter(getActivity(), recommended_data, new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Intent intent = new Intent(getActivity(), Mehndi.class);
                intent.putExtra("text", text);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView_recommendations.setAdapter(recommendataion_adapter);

        return view;
    }
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing / 2;
                outRect.right = spacing / 2;
                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = spacing / 2;
                outRect.right = spacing / 2;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

}
