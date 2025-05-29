package com.example.smartwastemanagement2;

import android.app.Application;
import android.util.Log;

/**
 * Application class that handles global initialization
 */
public class SmartWasteApp extends Application {
    private static final String TAG = "SmartWasteApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "Application starting up");
        
        // Initialize database fixer
        DatabaseFixer fixer = new DatabaseFixer(this);
        fixer.fixDatabaseIssuesInBackground();
        
        Log.d(TAG, "Database fix started in background");
    }
}
