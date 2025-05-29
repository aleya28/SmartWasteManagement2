package com.example.smartwastemanagement2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddWasteActivity extends AppCompatActivity {

    private RadioGroup radioGroupWasteType;
    private EditText editTextWeight, editTextDate, editTextTime, editTextAddress, editTextNotes;
    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_waste);

        // Initialize DBHelper and SessionManager
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);
        calendar = Calendar.getInstance();

        // Initialize views
        radioGroupWasteType = findViewById(R.id.radioGroupWasteType);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextNotes = findViewById(R.id.editTextNotes);
        Button buttonSubmit = findViewById(R.id.buttonSubmitWaste);
        Button buttonCancel = findViewById(R.id.buttonCancelWaste);

        // Set up date picker dialog
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up time picker dialog
        editTextTime.setOnClickListener(v -> showTimePickerDialog());

        // Set up submit button
        buttonSubmit.setOnClickListener(v -> submitWaste());

        // Set up cancel button
        buttonCancel.setOnClickListener(v -> finish());
    }

    /**
     * Show date picker dialog
     */
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateField();
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set min date to current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    /**
     * Show time picker dialog
     */
    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeField();
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );

        timePickerDialog.show();
    }

    /**
     * Update date field with selected date
     */
    private void updateDateField() {
        String dateFormat = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        editTextDate.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Update time field with selected time
     */
    private void updateTimeField() {
        String timeFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.US);
        editTextTime.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Submit waste data
     */
    private void submitWaste() {
        // Validate input fields
        if (!validateInputs()) {
            return;
        }

        // Get selected waste type
        int selectedId = radioGroupWasteType.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String wasteType = radioButton.getText().toString();

        // Get other form values
        float weight = Float.parseFloat(editTextWeight.getText().toString());
        String pickupDate = editTextDate.getText().toString();
        String pickupTime = editTextTime.getText().toString();
        String address = editTextAddress.getText().toString();
        String notes = editTextNotes.getText().toString();
        String userEmail = sessionManager.getUserEmail();

        // Create waste submission object
        WasteSubmission submission = new WasteSubmission(
                wasteType,
                weight,
                pickupDate,
                pickupTime,
                address,
                notes,
                userEmail
        );

        // Add to database
        long id = dbHelper.addWasteSubmission(submission);

        if (id != -1) {
            Toast.makeText(this, "Waste submission added successfully! Reference ID: " + submission.getReferenceId(), Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to submit waste request. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Validate input fields
     * @return boolean indicating if all inputs are valid
     */
    private boolean validateInputs() {
        boolean isValid = true;

        // Check if waste type is selected
        if (radioGroupWasteType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a waste type", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check weight
        if (TextUtils.isEmpty(editTextWeight.getText())) {
            editTextWeight.setError("Weight is required");
            isValid = false;
        } else {
            try {
                float weight = Float.parseFloat(editTextWeight.getText().toString());
                if (weight <= 0) {
                    editTextWeight.setError("Weight must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                editTextWeight.setError("Invalid weight format");
                isValid = false;
            }
        }

        // Check date
        if (TextUtils.isEmpty(editTextDate.getText())) {
            editTextDate.setError("Pickup date is required");
            isValid = false;
        }

        // Check time
        if (TextUtils.isEmpty(editTextTime.getText())) {
            editTextTime.setError("Pickup time is required");
            isValid = false;
        }

        // Check address
        if (TextUtils.isEmpty(editTextAddress.getText())) {
            editTextAddress.setError("Pickup address is required");
            isValid = false;
        }

        return isValid;
    }
}
