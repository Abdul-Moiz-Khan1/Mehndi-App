package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragments.Favourite;
import com.example.myapplication.Fragments.Home;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Home homeFragment = new Home();
    private Favourite favouriteFragment = new Favourite();


    private ImageView settingsIcon;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            }  else if (item.getItemId() == R.id.nav_fav) {
                switchFragment(favouriteFragment);
                return true;
            }
            return false;
        }

    };

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        //my temp work
        List<String> imagesurl = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("tatto");
        Log.d("here?" , "under tatto");
        storageReference.listAll().addOnSuccessListener(listResult -> {
            Log.d("itrr?" , "GOING");
            Log.d("lenghth" , String.valueOf(listResult.getItems().size()));

            for(StorageReference item : listResult.getItems()){
                item.getDownloadUrl().addOnSuccessListener(uri->{
                    Log.d("itrr?" , "GOING");
                    imagesurl.add(uri.toString());
                    Log.d("imageurl" , uri.toString());
                }).addOnFailureListener(e->{
                    Log.e("downlading Error" , "Error while downloading url" , e);
                });
            }

        }).addOnFailureListener(e->{
            Log.d("Lisitng Error" , "Error while lisitng all" , e);
        });

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // Find view by ID for the settings icon
        settingsIcon = findViewById(R.id.settingsIcon);

        // Set default fragment
        switchFragment(homeFragment);

        // Set up settings icon click listener
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu(v);
            }
        });


        // Find view by ID for the FAB
        FloatingActionButton fab = findViewById(R.id.fab);

        // Set up FAB click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the Scan activity
                Intent intent = new Intent(MainActivity.this, Scan.class);
                startActivity(intent);
            }
        });
    }

    private void showOptionsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_toolbar);

        // Set up menu item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_option1) {
                    // Handle option 1 click
                    return true;
                } else if (menuItem.getItemId() == R.id.action_option2) {
                    // Handle option 2 click
                    return true;
                }else if (menuItem.getItemId() == R.id.action_option2) {
                    // Handle option 2 click
                    return true;
                }else if (menuItem.getItemId() == R.id.action_option3) {
                    // Handle option 2 click
                    return true;
                }else if (menuItem.getItemId() == R.id.action_option4) {
                    // Handle option 2 click
                    return true;
                }
                // Handle other menu items as needed
                return false;
            }
        });


        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_option1) {
            // Handle option 1 click
            return true;
        } else if (item.getItemId() == R.id.action_option2) {
            // Handle option 2 click
            return true;
        } else {
            // Handle other menu items as needed
            return super.onOptionsItemSelected(item);
        }
    }

}


