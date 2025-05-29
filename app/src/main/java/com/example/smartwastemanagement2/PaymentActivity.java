package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

import android.database.sqlite.SQLiteDatabase;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvWasteType, tvWeight, tvPickupDate, tvPickupTime, tvReferenceId, tvTotalAmount;
    private RadioGroup radioGroupPaymentMethod;
    private Button btnConfirmPayment, btnCancel;
    private DBHelper dbHelper;
    private WasteSubmission currentSubmission;
    private String referenceId;
    private static final double RATE_PER_KG = 0.50; // RM 0.50 per kg

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize database helper
        dbHelper = new DBHelper(this);

        // Initialize views
        tvWasteType = findViewById(R.id.tvWasteType);
        tvWeight = findViewById(R.id.tvWeight);
        tvPickupDate = findViewById(R.id.tvPickupDate);
        tvPickupTime = findViewById(R.id.tvPickupTime);
        tvReferenceId = findViewById(R.id.tvReferenceId);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnCancel = findViewById(R.id.btnCancel);

        // Get reference ID from intent
        referenceId = getIntent().getStringExtra("referenceId");
        if (referenceId == null || referenceId.isEmpty()) {
            Toast.makeText(this, "Invalid submission reference", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load submission details
        loadSubmissionDetails();

        // Set up buttons
        btnConfirmPayment.setOnClickListener(v -> processPayment());
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Load the submission details from the database
     */
    private void loadSubmissionDetails() {
        currentSubmission = dbHelper.getWasteSubmissionByReferenceId(referenceId);
        
        if (currentSubmission == null) {
            Toast.makeText(this, "Submission not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display submission details
        tvWasteType.setText(currentSubmission.getWasteType());
        tvWeight.setText(currentSubmission.getWeight() + " kg");
        tvPickupDate.setText(currentSubmission.getPickupDate());
        tvPickupTime.setText(currentSubmission.getPickupTime());
        tvReferenceId.setText(currentSubmission.getReferenceId());
        
        // Calculate and display the total amount
        double totalAmount = calculateTotalAmount(currentSubmission.getWeight());
        DecimalFormat df = new DecimalFormat("0.00");
        tvTotalAmount.setText("RM " + df.format(totalAmount));
    }

    /**
     * Calculate the total amount based on waste weight
     * @param weight Weight of the waste in kg
     * @return Total amount in RM
     */
    private double calculateTotalAmount(float weight) {
        return weight * RATE_PER_KG;
    }

    /**
     * Process the payment based on selected payment method
     */
    private void processPayment() {
        // Check if a payment method is selected
        int selectedPaymentMethodId = radioGroupPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentMethodId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get the selected payment method
        RadioButton selectedPaymentMethod = findViewById(selectedPaymentMethodId);
        String paymentMethod = selectedPaymentMethod.getText().toString();
        
        // In a real app, you would process the payment with a payment gateway here
        // For now, we'll just update the status to "Paid" and set the payment timestamp
        long paymentTimestamp = System.currentTimeMillis();
        boolean updated = dbHelper.updateWasteSubmissionStatus(referenceId, "Paid");
        
        // Double-check that the timestamp was properly set in case the update method fails
        if (updated) {
            // Verify the updated record has a timestamp
            WasteSubmission updatedSubmission = dbHelper.getWasteSubmissionByReferenceId(referenceId);
            if (updatedSubmission != null && updatedSubmission.getPaidAt() <= 0) {
                // Force set the timestamp if it's still not set
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                try {
                    android.content.ContentValues values = new android.content.ContentValues();
                    values.put("paid_at", paymentTimestamp);
                    db.update("waste_submissions", values, "reference_id = ?", new String[]{referenceId});
                    android.util.Log.d("PaymentActivity", "Force-updated payment timestamp for " + referenceId);
                } catch (Exception e) {
                    android.util.Log.e("PaymentActivity", "Error force-updating timestamp", e);
                } finally {
                    db.close();
                }
            }
            
            Toast.makeText(this, "Payment successful using " + paymentMethod, Toast.LENGTH_LONG).show();
            
            // Return to Booking screen with a result
            Intent resultIntent = new Intent();
            resultIntent.putExtra("paymentSuccess", true);
            resultIntent.putExtra("paymentMethod", paymentMethod);
            resultIntent.putExtra("paymentTimestamp", paymentTimestamp); // Use the same timestamp
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
