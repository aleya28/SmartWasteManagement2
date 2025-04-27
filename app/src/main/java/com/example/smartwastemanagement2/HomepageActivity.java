package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomepageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        setupBottomNavigation(R.id.nav_home); // nav bar

        // Init Bottom Nav
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home); // highlight Home icon

        // Init Garbage Button
        ImageButton btnGarbage = findViewById(R.id.btnGarbage);
        btnGarbage.setOnClickListener(view -> {
            Intent intent = new Intent(HomepageActivity.this, BookingActivity.class);
            startActivity(intent);
        });

        // Init Find Us Button
        ImageButton btnFindUs = findViewById(R.id.btnFindUs);
        btnFindUs.setOnClickListener(view -> {
            Intent intent = new Intent(HomepageActivity.this, RecyclingCenterActivity.class);
            startActivity(intent);

            // Init Education Button
        });
        ImageButton btnEducation = findViewById(R.id.btnEducation);
        btnEducation.setOnClickListener(view -> {
            Intent intent = new Intent(HomepageActivity.this, EducationActivity.class);
            startActivity(intent);
        });

        //  Init Reward Button
        ImageButton btnReward = findViewById(R.id.btnReward);
        btnReward.setOnClickListener(view -> {
            Intent intent = new Intent(HomepageActivity.this, RewardActivity.class);
            startActivity(intent);
        });


        // Bottom Nav Listener
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                return true;
            } else if (id == R.id.nav_booking) {
                startActivity(new Intent(this, BookingActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
