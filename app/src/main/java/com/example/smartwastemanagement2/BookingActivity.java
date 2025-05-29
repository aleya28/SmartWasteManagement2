package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends BaseActivity {
    private static final int REQUEST_ADD_WASTE = 100;
    private static final int REQUEST_PAYMENT = 101;
    private Button btnAddWaste, btnViewDetails, btnPay, btnDelete;
    private TextView textUserName, textSubmissionDetails, textViewViewAll;
    private TextView textTotalEarnings, textPendingPayment, textLastPaymentAmount, textLastPaymentDate;
    private LinearLayout layoutSubmissionButtons;
    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private List<WasteSubmission> userSubmissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);        // Initialize database helper
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);

        // Fix any paid submissions that might be missing timestamps
        dbHelper.updatePaidSubmissionsWithoutTimestamps();

        // Setup Bottom Nav
        setupBottomNavigation(R.id.nav_booking);

        // handle window insets (safe area)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });        // Set user name from session
        textUserName = findViewById(R.id.textUserName);
        textUserName.setText(sessionManager.getUserName());
        // Init TextView and Layout
        textSubmissionDetails = findViewById(R.id.textSubmissionDetails);
        layoutSubmissionButtons = findViewById(R.id.layoutSubmissionButtons);
        textViewViewAll = findViewById(R.id.textViewViewAll);
//        btnDelete = findViewById(R.id.buttonDelete);

        // Check if we should show earnings section (coming from wallet click)
        boolean showEarnings = getIntent().getBooleanExtra("showEarnings", false);

        // Init Earnings TextViews
        textTotalEarnings = findViewById(R.id.textTotalEarnings);
        textPendingPayment = findViewById(R.id.textPendingPayment);
        textLastPaymentAmount = findViewById(R.id.textLastPaymentAmount);
        textLastPaymentDate = findViewById(R.id.textLastPaymentDate);

        // Init Buttons
        btnAddWaste = findViewById(R.id.buttonAddWaste);
        btnViewDetails = findViewById(R.id.buttonViewDetails);
        btnPay = findViewById(R.id.buttonPay);
        btnDelete = findViewById(R.id.buttonDelete);
        // Actions
        btnAddWaste.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, AddWasteActivity.class);
            startActivityForResult(intent, REQUEST_ADD_WASTE);
        });

        btnViewDetails.setOnClickListener(v -> {
            if (userSubmissions != null && !userSubmissions.isEmpty()) {
                // Show details of the latest submission
                WasteSubmission latestSubmission = userSubmissions.get(0);
                Toast.makeText(this, "Reference ID: " + latestSubmission.getReferenceId(), Toast.LENGTH_SHORT).show();
                // In a real app, you might navigate to a details page:
                // Intent intent = new Intent(BookingActivity.this, SubmissionDetailsActivity.class);
                // intent.putExtra("referenceId", latestSubmission.getReferenceId());
                // startActivity(intent);
            } else {
                Toast.makeText(this, "No submissions available", Toast.LENGTH_SHORT).show();
            }
        });
        btnPay.setOnClickListener(v -> {
            if (userSubmissions != null && !userSubmissions.isEmpty()) {
                WasteSubmission latestSubmission = userSubmissions.get(0);

                // Check if the submission is already paid
                if ("Paid".equals(latestSubmission.getStatus())) {
                    Toast.makeText(this, "This submission has already been paid", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Launch the payment activity with the reference ID
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("referenceId", latestSubmission.getReferenceId());
                startActivityForResult(intent, REQUEST_PAYMENT);
            } else {
                Toast.makeText(this, "No submissions available for payment", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete = findViewById(R.id.buttonDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                if (userSubmissions != null && !userSubmissions.isEmpty()) {
                    WasteSubmission latestSubmission = userSubmissions.get(0);
                    boolean deleted = dbHelper.deleteWasteSubmissionByReferenceId(latestSubmission.getReferenceId());
                    if (deleted) {
                        Toast.makeText(this, "Submission deleted successfully", Toast.LENGTH_SHORT).show();
                        loadUserSubmissions();
                        //refresh page
                        finish();
                        startActivity(getIntent());
                    } else {
                        Toast.makeText(this, "Failed to delete submission", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No submission to delete", Toast.LENGTH_SHORT).show();
                }
            });
        }


        // Set up View All click listener
        textViewViewAll.setOnClickListener(v -> {
            if (userSubmissions != null && !userSubmissions.isEmpty()) {
                // Navigate to AllSubmissionsActivity to show all submissions
                Intent intent = new Intent(BookingActivity.this, AllSubmissionsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No submissions available to view", Toast.LENGTH_SHORT).show();
            }
        });        // Load user's waste submissions
        loadUserSubmissions();

        // If coming from wallet icon, scroll to earnings section
        if (showEarnings) {
            // Find the ScrollView and earnings card
            final View scrollView = findViewById(R.id.scrollContent);
            final View earningsCard = findViewById(R.id.earningsCard);

            // Scroll to earnings section after layout is complete
            if (earningsCard != null && scrollView != null) {
                scrollView.post(() -> {
                    int[] location = new int[2];
                    earningsCard.getLocationOnScreen(location);
                    int y = location[1];

                    // Adjust for status bar and other UI elements
                    y -= getResources().getDisplayMetrics().densityDpi / 3;

                    // Scroll to the earnings card
                    if (scrollView instanceof android.widget.ScrollView) {
                        ((android.widget.ScrollView) scrollView).smoothScrollTo(0, y);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_WASTE && resultCode == RESULT_OK) {
            // Reload submissions after adding new waste
            loadUserSubmissions();
        } else if (requestCode == REQUEST_PAYMENT && resultCode == RESULT_OK) {
            // Check if payment was successful
            if (data != null && data.getBooleanExtra("paymentSuccess", false)) {
                String paymentMethod = data.getStringExtra("paymentMethod");
                long paymentTimestamp = data.getLongExtra("paymentTimestamp", System.currentTimeMillis());

                // Create a detailed success message with payment details
                String successMsg = "Payment successful! ";
                if (paymentMethod != null && !paymentMethod.isEmpty()) {
                    successMsg += "Paid via " + paymentMethod + ". ";
                }
                successMsg += "Your waste collection is confirmed.";

                Toast.makeText(this, successMsg, Toast.LENGTH_LONG).show();

                // Reload submissions to update the UI with the new payment status
                loadUserSubmissions();

                // Update the earnings info to reflect the new payment
                updateEarningsInfo();
            }
        }
    }

    /**
     * Load user's waste submissions from database and update UI
     */
    private void loadUserSubmissions() {
        try {
            String userEmail = sessionManager.getUserEmail();
            userSubmissions = dbHelper.getUserWasteSubmissions(userEmail);

            // Update UI with submission data if available
            if (userSubmissions != null && !userSubmissions.isEmpty()) {
                // Get the latest submission (first in the list)
                WasteSubmission latestSubmission = userSubmissions.get(0);

                // Show submission buttons
                layoutSubmissionButtons.setVisibility(View.VISIBLE);

                // Format submission details
                String submissionDetails =
                        "Waste Type: " + latestSubmission.getWasteType() +
                        "\nSubmitted on: " + formatDate(System.currentTimeMillis()) +
                        "\nQuantity: " + latestSubmission.getWeight() + " kg" +
                        "\nPickup Date: " + latestSubmission.getPickupDate() +
                        "\nTime: " + latestSubmission.getPickupTime() +
                        "\nPayment Status: " + latestSubmission.getStatus() +
                        "\nReference ID: " + latestSubmission.getReferenceId();

                // Update the UI
                textSubmissionDetails.setText(submissionDetails);

                // Enable View All link
                textViewViewAll.setEnabled(true);
                textViewViewAll.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

                // For debugging
                Toast.makeText(this, "You have " + userSubmissions.size() + " waste submission(s)", Toast.LENGTH_SHORT).show();
            } else {
                // No submissions yet
                textSubmissionDetails.setText("No submissions yet. Add waste to start recycling!");
                layoutSubmissionButtons.setVisibility(View.GONE);

                // Disable View All link
                textViewViewAll.setEnabled(false);
                textViewViewAll.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            // Update earnings info
            updateEarningsInfo();
        } catch (Exception e) {
            // Handle any database errors gracefully
            e.printStackTrace();
            Toast.makeText(this, "Error loading submissions. Please try again later.", Toast.LENGTH_SHORT).show();

            // Set default state
            textSubmissionDetails.setText("Error loading submissions. Please try again.");
            layoutSubmissionButtons.setVisibility(View.GONE);
        }
    }

    /**
     * Format timestamp to readable date
     */
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Calculate and display earnings data
     */
    private void updateEarningsInfo() {
        double totalEarnings = 0.0;
        double pendingPayment = 0.0;
        double lastPaymentAmount = 0.0;
        String lastPaymentDate = "No payments yet";

        // Get the user's email from session
        String userEmail = sessionManager.getUserEmail();

        // Get all submissions to calculate total earnings and pending payments
        if (userSubmissions != null && !userSubmissions.isEmpty()) {
            // Rate per kg for waste recycling (same as in PaymentActivity)
            final double RATE_PER_KG = 0.50;

            // Calculate totals
            for (WasteSubmission submission : userSubmissions) {
                double amount = submission.getWeight() * RATE_PER_KG;

                // Add to total earnings regardless of status
                totalEarnings += amount;

                // Add to pending payment if not paid
                if (!"Paid".equals(submission.getStatus())) {
                    pendingPayment += amount;
                }
            }
        }

        // Get paid submissions to find the most recent payment
        List<WasteSubmission> paidSubmissions = dbHelper.getPaidWasteSubmissions(userEmail);
        android.util.Log.d("BookingActivity", "Found " + (paidSubmissions != null ? paidSubmissions.size() : 0) + " paid submissions");

        if (paidSubmissions != null && !paidSubmissions.isEmpty()) {
            // The list is already sorted by paidAt in descending order,
            // so the first item is the most recent payment
            WasteSubmission mostRecentPayment = paidSubmissions.get(0);

            // Rate per kg for waste recycling
            final double RATE_PER_KG = 0.50;

            // Calculate the payment amount
            lastPaymentAmount = mostRecentPayment.getWeight() * RATE_PER_KG;
              // Get the payment timestamp
            long paidAt = mostRecentPayment.getPaidAt();
            android.util.Log.d("BookingActivity", "Most recent payment timestamp: " + paidAt +
                              " (" + (paidAt > 0 ? formatDate(paidAt) : "Invalid timestamp") + ")");

            // Format the last payment date - use current time if timestamp is 0
            if (paidAt > 0) {
                lastPaymentDate = "Last Payment on " + formatDate(paidAt);
            } else {
                // If the submission is marked as paid but has no timestamp, use today's date
                lastPaymentDate = "Last Payment on " + formatDate(System.currentTimeMillis());
                android.util.Log.d("BookingActivity", "Using current time for invalid timestamp");
            }
            android.util.Log.d("BookingActivity", "Setting last payment date text: " + lastPaymentDate);
        } else {
            android.util.Log.d("BookingActivity", "No paid submissions found");
        }

        // Format and display the values
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        textTotalEarnings.setText("Rm" + df.format(totalEarnings));
        textPendingPayment.setText("Rm" + df.format(pendingPayment));
        textLastPaymentAmount.setText("Rm" + df.format(lastPaymentAmount));
        textLastPaymentDate.setText(lastPaymentDate);
    }
}
