package com.example.smartwastemanagement2;

import android.os.Bundle;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ganti dengan layout sebenar

        // ✅ Highlight ikon profile dalam nav
        setupBottomNavigation(R.id.nav_profile);
    }
}
