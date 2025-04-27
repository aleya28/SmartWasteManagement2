package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class BookingActivity extends BaseActivity {

    Button btnAddWaste, btnViewDetails, btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Setup Bottom Nav
        setupBottomNavigation(R.id.nav_booking);

        // handle window insets (safe area)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init Buttons
        btnAddWaste = findViewById(R.id.buttonAddWaste);
        btnViewDetails = findViewById(R.id.buttonViewDetails);
        btnPay = findViewById(R.id.buttonPay);

        // Actions
        btnAddWaste.setOnClickListener(v -> {
            Toast.makeText(this, "Add Waste clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, AddWasteActivity.class));
        });

        btnViewDetails.setOnClickListener(v -> {
            Toast.makeText(this, "Viewing Details...", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, SubmissionDetailsActivity.class));
        });

        btnPay.setOnClickListener(v -> {
            Toast.makeText(this, "Proceed to Payment", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, PaymentActivity.class));
        });
    }
}
