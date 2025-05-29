package com.example.smartwastemanagement2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EducationActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ImageView btnBack, articleImage1, articleImage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        // Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(EducationActivity.this, HomepageActivity.class));
            finish();
        });

        // Image click open website
        articleImage1 = findViewById(R.id.articleImage1);
        articleImage2 = findViewById(R.id.articleImage2);

        articleImage1.setOnClickListener(v -> {
            String url = "https://www.science-gate.com/IJAAS/Articles/2024/2024-11-12/1021833ijaas202412021.pdf";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        articleImage2.setOnClickListener(v -> {
            String url = "https://bmcpublichealth.biomedcentral.com/articles/10.1186/s12889-021-12274-7";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        // Bottom navigation
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomepageActivity.class));
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
