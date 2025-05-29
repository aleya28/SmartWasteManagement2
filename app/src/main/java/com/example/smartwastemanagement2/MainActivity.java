package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 1500; // 1.5 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize session manager
        sessionManager = new SessionManager(this);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Use a handler to delay the transition slightly (splash screen effect)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in using session manager
                Intent intent;
                if (sessionManager.isLoggedIn()) {
                    // User is logged in, go to Homepage
                    intent = new Intent(MainActivity.this, HomepageActivity.class);
                } else {
                    // User is not logged in, go to Login
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}