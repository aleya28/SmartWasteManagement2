package com.example.smartwastemanagement2;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends BaseActivity {
    private SessionManager sessionManager;
    private TextView welcomeText;
    private TextView walletBalance;
    private LinearLayout walletLayout;
    private DBHelper dbHelper;
    private static final double RATE_PER_KG = 0.50; // RM 0.50 per kg, same as in PaymentActivity
    
    // Search related variables
    private EditText searchBar;
    private View searchResultsOverlay;
    private RecyclerView searchResultsList;
    private TextView noResultsMessage;
    private TextView closeSearch;
    private List<Service> servicesList;
    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize SessionManager and DBHelper
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);

        // Initialize welcome text
        welcomeText = findViewById(R.id.welcomeText);
        String userName = sessionManager.getUserName();
        welcomeText.setText("Welcome\n" + userName);
        
        // Initialize wallet balance
        walletBalance = findViewById(R.id.walletBalance);
        walletLayout = findViewById(R.id.walletLayout);
        updateWalletBalance();
        
        // Set click listener on wallet to view detailed earnings
        walletLayout.setOnClickListener(v -> {
            // Show feedback to user
            Toast.makeText(this, "Viewing your earnings details", Toast.LENGTH_SHORT).show();
            
            // Navigate to Booking activity with flag to show earnings section
            Intent intent = new Intent(HomepageActivity.this, BookingActivity.class);
            intent.putExtra("showEarnings", true);
            startActivity(intent);
        });
        
        // Setup search functionality
        setupSearchFunctionality();

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
    
    /**
     * Calculate and update wallet balance based on paid waste submissions
     */
    private void updateWalletBalance() {
        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            walletBalance.setText("RM0.00");
            return;
        }
        
        // Get paid submissions to calculate total earnings
        List<WasteSubmission> paidSubmissions = dbHelper.getPaidWasteSubmissions(userEmail);
        double totalEarnings = 0.0;
        
        if (paidSubmissions != null && !paidSubmissions.isEmpty()) {
            for (WasteSubmission submission : paidSubmissions) {
                // Calculate amount for this submission
                double amount = submission.getWeight() * RATE_PER_KG;
                totalEarnings += amount;
            }
        }
        
        // Format the total with 2 decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        walletBalance.setText("RM" + df.format(totalEarnings));
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Update the wallet balance when returning to this screen
        // This ensures the balance reflects any new payments
        updateWalletBalance();
    }

    /**
     * Set up the search functionality including the search bar and results overlay.
     */
    private void setupSearchFunctionality() {
        // Initialize search bar
        searchBar = findViewById(R.id.searchBar);
        
        // Inflate search results overlay
        searchResultsOverlay = getLayoutInflater().inflate(R.layout.search_results_overlay, null);
        
        // Add overlay to the root view with proper layout parameters
        ViewGroup rootView = findViewById(android.R.id.content);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(searchResultsOverlay, params);
        
        // Initially hide the overlay
        searchResultsOverlay.setVisibility(View.GONE);
        
        // Initialize search UI components
        searchResultsList = searchResultsOverlay.findViewById(R.id.searchResultsList);
        noResultsMessage = searchResultsOverlay.findViewById(R.id.noResultsMessage);
        closeSearch = searchResultsOverlay.findViewById(R.id.closeSearch);
        
        // Initialize services list
        initServicesList();
        
        // Set up RecyclerView
        searchResultsList.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new ServiceAdapter(this, servicesList);
        searchResultsList.setAdapter(serviceAdapter);
        
        // Set up search action
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch(searchBar.getText().toString());
                return true;
            }
            return false;
        });
        
        // Also set up focus change listener to show search results when search bar is clicked
        searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !searchBar.getText().toString().isEmpty()) {
                performSearch(searchBar.getText().toString());
            }
        });
        
        // Setup close button
        closeSearch.setOnClickListener(v -> hideSearchResults());
    }
    
    /**
     * Initialize the list of services available in the app.
     */
    private void initServicesList() {
        servicesList = new ArrayList<>();
        
        // Add all services with their details
        servicesList.add(new Service(1, "Waste Collection", 
                "Schedule waste collection from your location", 
                R.drawable.garbage, 
                "com.example.smartwastemanagement2.BookingActivity"));
        
        servicesList.add(new Service(2, "Recycling Centers", 
                "Find recycling centers near you", 
                R.drawable.recycling, 
                "com.example.smartwastemanagement2.RecyclingCenterActivity"));
        
        servicesList.add(new Service(3, "Waste Education", 
                "Learn about waste management", 
                R.drawable.education, 
                "com.example.smartwastemanagement2.EducationActivity"));
        
        servicesList.add(new Service(4, "Chat Support", 
                "Chat with our support team", 
                R.drawable.chat, 
                "com.example.smartwastemanagement2.ChatActivity"));
        
        servicesList.add(new Service(5, "Payment History", 
                "View your payment history", 
                R.drawable.wallet_icon, 
                "com.example.smartwastemanagement2.PaymentActivity"));
        
        servicesList.add(new Service(6, "Profile Settings", 
                "Update your profile information", 
                R.drawable.home_page_profile, 
                "com.example.smartwastemanagement2.ProfileActivity"));
        
        servicesList.add(new Service(7, "Privacy Policy", 
                "Read our privacy policy", 
                android.R.drawable.ic_menu_info_details, 
                "com.example.smartwastemanagement2.PrivacyPolicyActivity"));
    }
    
    /**
     * Perform search based on the query string.
     * 
     * @param query The search query
     */
    private void performSearch(String query) {
        if (query.trim().isEmpty()) {
            hideSearchResults();
            return;
        }
        
        // Filter services based on query
        serviceAdapter.filter(query);
        
        // Show search results or no results message
        showSearchResults();
        
        if (serviceAdapter.getItemCount() > 0) {
            searchResultsList.setVisibility(View.VISIBLE);
            noResultsMessage.setVisibility(View.GONE);
        } else {
            searchResultsList.setVisibility(View.GONE);
            noResultsMessage.setVisibility(View.VISIBLE);
        }
        
        // Hide keyboard
        hideKeyboard();
    }
    
    /**
     * Hide the keyboard.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }
    
    /**
     * Show the search results overlay.
     */
    private void showSearchResults() {
        searchResultsOverlay.setVisibility(View.VISIBLE);
        
        // Ensure wallet balance remains visible
        if (walletLayout != null) {
            walletLayout.bringToFront();
        }
    }
    
    /**
     * Hide the search results overlay.
     */
    private void hideSearchResults() {
        searchResultsOverlay.setVisibility(View.GONE);
        searchBar.clearFocus();
    }
    
    @Override
    public void onBackPressed() {
        // If search results are visible, hide them instead of exiting the activity
        if (searchResultsOverlay.getVisibility() == View.VISIBLE) {
            hideSearchResults();
        } else {
            super.onBackPressed();
        }
    }
}
