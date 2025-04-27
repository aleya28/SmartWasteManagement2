package com.example.smartwastemanagement2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RewardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        setupBottomNavigation(R.id.nav_home); // Assuming you want to keep the nav bar
    }
}