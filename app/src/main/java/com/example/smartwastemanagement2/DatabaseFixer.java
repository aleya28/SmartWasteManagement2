package com.example.smartwastemanagement2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Helper class to fix database issues with old payment timestamps.
 * This allows us to fix existing data without requiring users to reinstall the app.
 */
public class DatabaseFixer {
    private static final String TAG = "DatabaseFixer";
    private final Context context;
    
    public DatabaseFixer(Context context) {
        this.context = context;
    }
    
    /**
     * Run a database fix operation in the background
     */
    public void fixDatabaseIssuesInBackground() {
        new FixDatabaseTask().execute();
    }
    
    /**
     * AsyncTask to perform database fixes in the background
     */
    private class FixDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            DBHelper dbHelper = new DBHelper(context);
            try {
                // Fix issue with missing payment timestamps
                Log.d(TAG, "Starting database fix for payment timestamps...");
                
                int fixedCount = fixPaidSubmissionsWithoutTimestamps(dbHelper);
                
                Log.d(TAG, "Fixed " + fixedCount + " submissions with missing payment timestamps");
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error fixing database issues", e);
                return false;
            } finally {
                // Always close database connections
                dbHelper.close();
            }
        }
    }
    
    /**
     * Fix paid submissions that don't have a timestamp
     * @return The number of records fixed
     */
    private int fixPaidSubmissionsWithoutTimestamps(DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        
        try {
            // Use current time for all fixed timestamps
            long currentTime = System.currentTimeMillis();
            
            // Update all paid submissions that have null or 0 timestamps
            String sql = "UPDATE waste_submissions SET paid_at = ? " +
                        "WHERE status = 'Paid' AND (paid_at IS NULL OR paid_at <= 0)";
            
            db.execSQL(sql, new Object[]{currentTime});
            
            // Get count of affected rows (using a safer method)
            Cursor cursor = db.rawQuery("SELECT changes()", null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }
            
            Log.d(TAG, "Fixed " + count + " submissions with SQL update");
            
            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error fixing paid submissions timestamps", e);
            return 0;
        }
    }
}
