package com.example.smartwastemanagement2;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager handles user session data such as login status and user information.
 */
public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;
    
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    
    // Context
    private Context context;
    
    // Shared pref mode
    private int PRIVATE_MODE = 0;
    
    // Sharedpref file name
    private static final String PREF_NAME = "SmartWastePrefs";
    
    // Shared Preferences Keys
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "userName";
    private static final String KEY_EMAIL = "userEmail";
    
    /**
     * Constructor
     */
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    /**
     * Create login session
     */
    public void createLoginSession(String name, String email) {
        // Store login status
        editor.putBoolean(IS_LOGGED_IN, true);
        
        // Store user data
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        
        // Commit changes
        editor.apply();
    }
    
    /**
     * Get stored session data
     */
    public String getUserName() {
        return pref.getString(KEY_NAME, "User");
    }
    
    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, "");
    }
    
    /**
     * Check login method will check user login status
     * If not logged in, launch Login Activity
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }
    
    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clear all data from Shared Preferences
        editor.clear();
        editor.apply();
    }
}
