package com.example.smartwastemanagement2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class ProfileActivity extends BaseActivity {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout;
    private LinearLayout changePasswordLayout, informationLayout, aboutAppLayout, termsConditionsLayout, privacyPolicyLayout;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize session manager and database helper
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);

        // Initialize UI elements
        tvUserName = findViewById(R.id.userName);
        tvUserEmail = findViewById(R.id.userEmail);
        btnLogout = findViewById(R.id.btnLogout);
        
        // Find layouts with IDs
        changePasswordLayout = findViewById(R.id.layoutChangePassword);
        informationLayout = findViewById(R.id.layoutInformation);
        aboutAppLayout = findViewById(R.id.layoutAboutApp);
        termsConditionsLayout = findViewById(R.id.layoutTermsConditions);
        privacyPolicyLayout = findViewById(R.id.layoutPrivacyPolicy);
        if (changePasswordLayout == null) {
            // Look for the linear layout containing "Change Password" text
            for (int i = 0; i < ((LinearLayout) findViewById(R.id.settingsContainer)).getChildCount(); i++) {
                View child = ((LinearLayout) findViewById(R.id.settingsContainer)).getChildAt(i);
                if (child instanceof LinearLayout) {
                    LinearLayout layout = (LinearLayout) child;
                    if (layout.getChildAt(0) instanceof TextView) {
                        TextView textView = (TextView) layout.getChildAt(0);
                        if (textView.getText().toString().equals("Change Password")) {
                            changePasswordLayout = layout;
                            break;
                        }
                    }
                }
            }
        }

        // Set up bottom navigation
        setupBottomNavigation(R.id.nav_profile);

        // Display user data from session manager
        tvUserName.setText(sessionManager.getUserName());
        tvUserEmail.setText(sessionManager.getUserEmail());

        // Set up change password click listener
        if (changePasswordLayout != null) {
            changePasswordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangePasswordDialog();
                }
            });
        }
        
        // Set up information click listener
        if (informationLayout != null) {
            informationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Information Activity
                    Intent intent = new Intent(ProfileActivity.this, InformationActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        // Set up about app click listener
        if (aboutAppLayout != null) {
            aboutAppLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open App Info Activity
                    Intent intent = new Intent(ProfileActivity.this, AppInfoActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        // Set up terms and conditions click listener
        if (termsConditionsLayout != null) {
            termsConditionsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Terms and Conditions Activity
                    Intent intent = new Intent(ProfileActivity.this, TermsConditionsActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        // Set up privacy policy click listener
        if (privacyPolicyLayout != null) {
            privacyPolicyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open Privacy Policy Activity
                    Intent intent = new Intent(ProfileActivity.this, PrivacyPolicyActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Set up logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear login session
                sessionManager.logoutUser();

                // Redirect to login screen
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    
    /**
     * Shows dialog for changing password
     */
    private void showChangePasswordDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        
        // Get references to the EditText fields
        final EditText etCurrentPassword = dialogView.findViewById(R.id.editTextCurrentPassword);
        final EditText etNewPassword = dialogView.findViewById(R.id.editTextNewPassword);
        final EditText etConfirmPassword = dialogView.findViewById(R.id.editTextConfirmNewPassword);
        
        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
               .setCancelable(true)
               .setPositiveButton("Change Password", null) // Set listener later to prevent automatic dismiss
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
        
        // Create and show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();
        
        // Set the positive button click listener after showing the dialog
        // This is done to prevent the dialog from automatically dismissing on errors
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get entered text
                String currentPassword = etCurrentPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                
                // Validate inputs
                if (TextUtils.isEmpty(currentPassword)) {
                    etCurrentPassword.setError("Current password is required");
                    return;
                }
                
                if (TextUtils.isEmpty(newPassword)) {
                    etNewPassword.setError("New password is required");
                    return;
                }
                
                if (newPassword.length() < 6) {
                    etNewPassword.setError("Password must be at least 6 characters");
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    etConfirmPassword.setError("Passwords do not match");
                    return;
                }
                
                // Get user email from session
                String userEmail = sessionManager.getUserEmail();
                
                // Update password in database
                boolean success = dbHelper.updatePassword(userEmail, currentPassword, newPassword);
                
                if (success) {
                    Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(ProfileActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
