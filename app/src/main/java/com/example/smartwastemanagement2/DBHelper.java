package com.example.smartwastemanagement2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    
    // Database Version - Increment this to trigger an upgrade
    private static final int DATABASE_VERSION = 4;
    
    // Database Name
    private static final String DATABASE_NAME = "SmartWasteDB";
    
    // User table name
    private static final String TABLE_USERS = "users";
    
    // User Table Columns names
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    
    // Waste submissions table
    private static final String TABLE_WASTE_SUBMISSIONS = "waste_submissions";
    
    // Chat messages table
    private static final String TABLE_CHAT_MESSAGES = "chat_messages";
    
    // Chat messages columns
    private static final String COLUMN_MESSAGE_ID = "id";
    private static final String COLUMN_MESSAGE_TEXT = "message";
    private static final String COLUMN_MESSAGE_TYPE = "message_type";
    private static final String COLUMN_MESSAGE_TIMESTAMP = "timestamp";
    private static final String COLUMN_MESSAGE_USER_EMAIL = "user_email";
    
    // Waste submissions columns
    private static final String COLUMN_SUBMISSION_ID = "id";
    private static final String COLUMN_WASTE_TYPE = "waste_type";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_PICKUP_DATE = "pickup_date";
    private static final String COLUMN_PICKUP_TIME = "pickup_time";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_USER_EMAIL_FK = "user_email";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_REFERENCE_ID = "reference_id";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_PAID_AT = "paid_at";
    
    // Create user table SQL query
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
            + COLUMN_USER_PASSWORD + " TEXT"
            + ")";
            
    // Create waste submissions table SQL query
    private static final String CREATE_WASTE_SUBMISSIONS_TABLE = "CREATE TABLE " + TABLE_WASTE_SUBMISSIONS + "("
            + COLUMN_SUBMISSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_WASTE_TYPE + " TEXT,"
            + COLUMN_WEIGHT + " REAL,"
            + COLUMN_PICKUP_DATE + " TEXT,"
            + COLUMN_PICKUP_TIME + " TEXT,"
            + COLUMN_ADDRESS + " TEXT,"
            + COLUMN_NOTES + " TEXT,"
            + COLUMN_USER_EMAIL_FK + " TEXT,"
            + COLUMN_STATUS + " TEXT,"
            + COLUMN_REFERENCE_ID + " TEXT,"
            + COLUMN_PAID_AT + " DATETIME,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";
    
    // Create chat messages table SQL query
    private static final String CREATE_CHAT_MESSAGES_TABLE = "CREATE TABLE " + TABLE_CHAT_MESSAGES + "("
            + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MESSAGE_TEXT + " TEXT,"
            + COLUMN_MESSAGE_TYPE + " INTEGER,"
            + COLUMN_MESSAGE_TIMESTAMP + " LONG,"
            + COLUMN_MESSAGE_USER_EMAIL + " TEXT"
            + ")";
    
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_WASTE_SUBMISSIONS_TABLE);
        db.execSQL(CREATE_CHAT_MESSAGES_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement a proper migration strategy to preserve data
        if (oldVersion < 2) {
            // Add the waste_submissions table without affecting existing data
            try {
                db.execSQL(CREATE_WASTE_SUBMISSIONS_TABLE);
            } catch (Exception e) {
                // Table might already exist
                e.printStackTrace();
            }
        }
        
        if (oldVersion < 3) {
            // Add the paid_at column to waste_submissions table
            try {
                db.execSQL("ALTER TABLE " + TABLE_WASTE_SUBMISSIONS + " ADD COLUMN " + COLUMN_PAID_AT + " DATETIME;");
            } catch (Exception e) {
                // Error adding column
                e.printStackTrace();
            }
        }
        
        if (oldVersion < 4) {
            // Add the chat_messages table
            try {
                db.execSQL(CREATE_CHAT_MESSAGES_TABLE);
            } catch (Exception e) {
                // Error creating table
                e.printStackTrace();
            }
        }
        
        // Add future migrations here with version checks
        // if (oldVersion < 4) { ... }
    }
    
    /**
     * Method to create a new user
     */
    public long addUser(String name, String email, String password) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Prepare values to insert
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        
        // Insert row
        long id = db.insert(TABLE_USERS, null, values);
        
        // Close db connection
        db.close();
        
        // Return newly inserted row id
        return id;
    }
    
    /**
     * Method to check if user exists by email
     */
    public boolean checkUser(String email) {
        // Get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Define a projection (columns to return)
        String[] columns = {
                COLUMN_USER_ID
        };
        
        // Selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";
        
        // Selection arguments
        String[] selectionArgs = {email};
        
        // Query user table
        Cursor cursor = db.query(TABLE_USERS, //Table to query
                columns,                       //columns to return
                selection,                     //columns for the WHERE clause
                selectionArgs,                 //The values for the WHERE clause
                null,                          //group the rows
                null,                          //filter by row groups
                null);                         //The sort order
        
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        
        return cursorCount > 0;
    }

    /**
     * Delete a waste submission by its reference ID
     * @param referenceId The reference ID of the submission
     * @return boolean indicating success or failure
     */
    public boolean deleteWasteSubmissionByReferenceId(String referenceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_WASTE_SUBMISSIONS, COLUMN_REFERENCE_ID + " = ?", new String[]{referenceId});
        db.close();
        return rowsAffected > 0;
    }


    /**
     * Method to check user by email and password
     */
    public boolean checkUser(String email, String password) {
        // Get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Define a projection (columns to return)
        String[] columns = {
                COLUMN_USER_ID
        };
        
        // Selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        
        // Selection arguments
        String[] selectionArgs = {email, password};
        
        // Query user table
        Cursor cursor = db.query(TABLE_USERS, //Table to query
                columns,                       //columns to return
                selection,                     //columns for the WHERE clause
                selectionArgs,                 //The values for the WHERE clause
                null,                          //group the rows
                null,                          //filter by row groups
                null);                         //The sort order
        
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        
        return cursorCount > 0;
    }
    
    /**
     * Get user name by email
     */
    public String getUserName(String email) {
        String name = "";
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {COLUMN_USER_NAME};
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        
        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
        }
        
        cursor.close();
        db.close();
        
        return name;
    }
    
    /**
     * Update user password
     * @param email User's email
     * @param oldPassword Current password for verification
     * @param newPassword New password to set
     * @return boolean indicating success or failure
     */
    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        // First verify the current password
        if (!checkUser(email, oldPassword)) {
            return false; // Current password is incorrect
        }
        
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Prepare values to update
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, newPassword);
        
        // Update the row
        int rowsAffected = db.update(TABLE_USERS, 
                                    values, 
                                    COLUMN_USER_EMAIL + " = ?", 
                                    new String[]{email});
        
        db.close();
        
        // Return true if at least one row was updated
        return rowsAffected > 0;
    }
    
    /**
     * Add a new waste submission
     * @param submission The WasteSubmission object
     * @return long Row ID of the new submission or -1 if error
     */
    public long addWasteSubmission(WasteSubmission submission) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;
        
        // Ensure the table exists before inserting
        ensureWasteSubmissionsTableExists();
        
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_WASTE_TYPE, submission.getWasteType());
            values.put(COLUMN_WEIGHT, submission.getWeight());
            values.put(COLUMN_PICKUP_DATE, submission.getPickupDate());
            values.put(COLUMN_PICKUP_TIME, submission.getPickupTime());
            values.put(COLUMN_ADDRESS, submission.getAddress());
            values.put(COLUMN_NOTES, submission.getNotes());
            values.put(COLUMN_USER_EMAIL_FK, submission.getUserId());
            values.put(COLUMN_STATUS, submission.getStatus());
            values.put(COLUMN_REFERENCE_ID, submission.getReferenceId());
            
            id = db.insert(TABLE_WASTE_SUBMISSIONS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        
        return id;
    }
    
    /**
     * Get all waste submissions for a specific user
     * @param userEmail The email of the user
     * @return List of WasteSubmission objects
     */
    public List<WasteSubmission> getUserWasteSubmissions(String userEmail) {
        List<WasteSubmission> submissionList = new ArrayList<>();
        
        // Make sure the table exists first
        ensureWasteSubmissionsTableExists();
        
        try {
            String selectQuery = "SELECT * FROM " + TABLE_WASTE_SUBMISSIONS + 
                                " WHERE " + COLUMN_USER_EMAIL_FK + " = ?" +
                                " ORDER BY " + COLUMN_CREATED_AT + " DESC";
            
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, new String[] {userEmail});
            
            if (cursor.moveToFirst()) {
                do {
                    WasteSubmission submission = new WasteSubmission();
                    submission.setWasteType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WASTE_TYPE)));
                    submission.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
                    submission.setPickupDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_DATE)));
                    submission.setPickupTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_TIME)));
                    submission.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
                    submission.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
                    submission.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL_FK)));
                    submission.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                    submission.setReferenceId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFERENCE_ID)));
                    
                    // Get paid timestamp (may be null if not paid)
                    int paidAtColumnIndex = cursor.getColumnIndex(COLUMN_PAID_AT);
                    if (paidAtColumnIndex != -1 && !cursor.isNull(paidAtColumnIndex)) {
                        submission.setPaidAt(cursor.getLong(paidAtColumnIndex));
                    } else {
                        submission.setPaidAt(0); // Not paid yet
                    }
                    
                    submissionList.add(submission);
                } while (cursor.moveToNext());
            }
            
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return submissionList;
    }
    
    /**
     * Ensures that the waste_submissions table exists
     */
    private void ensureWasteSubmissionsTableExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            // Check if the table exists
            Cursor cursor = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?", 
                    new String[]{TABLE_WASTE_SUBMISSIONS});
            
            boolean tableExists = cursor.getCount() > 0;
            cursor.close();
            
            // Create the table if it doesn't exist
            if (!tableExists) {
                db.execSQL(CREATE_WASTE_SUBMISSIONS_TABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get a specific waste submission by reference ID
     * @param referenceId The reference ID of the submission
     * @return WasteSubmission object or null if not found
     */
    public WasteSubmission getWasteSubmissionByReferenceId(String referenceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {
                COLUMN_WASTE_TYPE,
                COLUMN_WEIGHT,
                COLUMN_PICKUP_DATE,
                COLUMN_PICKUP_TIME,
                COLUMN_ADDRESS,
                COLUMN_NOTES,
                COLUMN_USER_EMAIL_FK,
                COLUMN_STATUS,
                COLUMN_REFERENCE_ID,
                COLUMN_PAID_AT,
                COLUMN_CREATED_AT
        };
        
        String selection = COLUMN_REFERENCE_ID + " = ?";
        String[] selectionArgs = {referenceId};
        
        Cursor cursor = db.query(TABLE_WASTE_SUBMISSIONS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        
        WasteSubmission submission = null;
        
        if (cursor.moveToFirst()) {
            submission = new WasteSubmission();
            submission.setWasteType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WASTE_TYPE)));
            submission.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
            submission.setPickupDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_DATE)));
            submission.setPickupTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_TIME)));
            submission.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
            submission.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
            submission.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL_FK)));
            submission.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            submission.setReferenceId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFERENCE_ID)));
            
            // Get paid timestamp (may be null if not paid)
            int paidAtColumnIndex = cursor.getColumnIndex(COLUMN_PAID_AT);
            if (paidAtColumnIndex != -1 && !cursor.isNull(paidAtColumnIndex)) {
                submission.setPaidAt(cursor.getLong(paidAtColumnIndex));
            } else {
                submission.setPaidAt(0); // Not paid yet
            }
        }
        
        cursor.close();
        db.close();
        
        return submission;
    }
    
    /**
     * Update the status of a waste submission
     * @param referenceId The reference ID of the submission
     * @param newStatus The new status
     * @return boolean indicating success or failure
     */
    public boolean updateWasteSubmissionStatus(String referenceId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);
        
        // If status is being set to paid, add a timestamp
        if ("Paid".equals(newStatus)) {
            long currentTime = System.currentTimeMillis();
            values.put(COLUMN_PAID_AT, currentTime);
            android.util.Log.d("DBHelper", "Setting payment timestamp for " + referenceId + 
                              " to " + currentTime + 
                              " (" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(currentTime)) + ")");
        }
        
        int rowsAffected = db.update(TABLE_WASTE_SUBMISSIONS,
                                   values,
                                   COLUMN_REFERENCE_ID + " = ?",
                                   new String[]{referenceId});
        
        android.util.Log.d("DBHelper", "Updated status for " + referenceId + " to " + newStatus + ": " + (rowsAffected > 0 ? "success" : "failed"));
        
        db.close();
        
        return rowsAffected > 0;
    }
    
    /**
     * Get a user's ID from their email
     * @param email The email of the user
     * @return User ID as a String, or null if not found
     */
    public String getUserIdFromEmail(String email) {
        String userId = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }

        cursor.close();
        db.close();

        return userId;
    }

    /**
     * Get a list of all paid waste submissions for a specific user
     * @param userEmail The email of the user
     * @return A list of WasteSubmission objects that have been paid
     */
    public List<WasteSubmission> getPaidWasteSubmissions(String userEmail) {
        List<WasteSubmission> submissions = new ArrayList<>();
        
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Debug logging
        android.util.Log.d("DBHelper", "Looking for paid submissions for user: " + userEmail);
        
        String query = "SELECT * FROM " + TABLE_WASTE_SUBMISSIONS +
                       " WHERE " + COLUMN_USER_EMAIL_FK + " = ? " +
                       " AND " + COLUMN_STATUS + " = ? " +
                       " ORDER BY " + COLUMN_PAID_AT + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{userEmail, "Paid"});
        
        android.util.Log.d("DBHelper", "Found " + cursor.getCount() + " paid submissions");
        
        if (cursor.moveToFirst()) {
            do {
                WasteSubmission submission = new WasteSubmission();
                
                submission.setWasteType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WASTE_TYPE)));
                submission.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
                submission.setPickupDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_DATE)));
                submission.setPickupTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PICKUP_TIME)));
                submission.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
                submission.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
                submission.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL_FK)));
                submission.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                submission.setReferenceId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFERENCE_ID)));
                
                int paidAtColumnIndex = cursor.getColumnIndex(COLUMN_PAID_AT);
                if (paidAtColumnIndex != -1 && !cursor.isNull(paidAtColumnIndex)) {
                    long paidTimestamp = cursor.getLong(paidAtColumnIndex);
                    submission.setPaidAt(paidTimestamp);
                    android.util.Log.d("DBHelper", "Submission " + submission.getReferenceId() + 
                                      " paid at timestamp: " + paidTimestamp + 
                                      " (" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(paidTimestamp)) + ")");
                } else {
                    submission.setPaidAt(0); // Not paid yet
                    android.util.Log.d("DBHelper", "Submission " + submission.getReferenceId() + " has no payment timestamp");
                }
                
                submissions.add(submission);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return submissions;
    }

    /**
     * Updates all submissions with 'Paid' status but missing timestamps
     * Call this method to fix old data that might not have timestamps
     */
    public void updatePaidSubmissionsWithoutTimestamps() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Get current time
        long currentTime = System.currentTimeMillis();
        
        // Query to find paid submissions with null or 0 timestamps
        String query = "SELECT " + COLUMN_REFERENCE_ID + 
                      " FROM " + TABLE_WASTE_SUBMISSIONS + 
                      " WHERE " + COLUMN_STATUS + " = 'Paid' " +
                      " AND (" + COLUMN_PAID_AT + " IS NULL OR " + COLUMN_PAID_AT + " = 0)";
        
        Cursor cursor = db.rawQuery(query, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                String refId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFERENCE_ID));
                
                // Update the timestamp for this submission
                ContentValues values = new ContentValues();
                values.put(COLUMN_PAID_AT, currentTime);
                
                int updated = db.update(TABLE_WASTE_SUBMISSIONS, 
                                      values, 
                                      COLUMN_REFERENCE_ID + " = ?", 
                                      new String[]{refId});
                
                if (updated > 0) {
                    count++;
                    android.util.Log.d("DBHelper", "Updated missing timestamp for submission: " + refId);
                }
            } while (cursor.moveToNext());
        }
        
        android.util.Log.d("DBHelper", "Updated timestamps for " + count + " paid submissions");
        
        cursor.close();
        db.close();
    }

    /**
     * Save a chat message to the database
     */
    public long saveChatMessage(ChatMessage message, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_MESSAGE_TEXT, message.getMessage());
        values.put(COLUMN_MESSAGE_TYPE, message.getMessageType());
        values.put(COLUMN_MESSAGE_TIMESTAMP, message.getTimestamp());
        values.put(COLUMN_MESSAGE_USER_EMAIL, userEmail);
        
        // Insert row
        long id = db.insert(TABLE_CHAT_MESSAGES, null, values);
        
        return id;
    }
    
    /**
     * Get all chat messages for a user
     */
    public List<ChatMessage> getChatHistory(String userEmail) {
        List<ChatMessage> messageList = new ArrayList<>();
        
        // Select all messages for this user, ordered by timestamp
        String selectQuery = "SELECT * FROM " + TABLE_CHAT_MESSAGES + 
                             " WHERE " + COLUMN_MESSAGE_USER_EMAIL + " = ?" +
                             " ORDER BY " + COLUMN_MESSAGE_TIMESTAMP + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{userEmail});
        
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                String messageText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TEXT));
                int messageType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TYPE));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TIMESTAMP));
                
                // Create ChatMessage with retrieved data
                ChatMessage message = new ChatMessage(messageText, messageType);
                // We need to set the timestamp manually to match the stored one
                message.setTimestamp(timestamp);
                
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return messageList;
    }
    
    /**
     * Delete chat history for a user
     */
    public void clearChatHistory(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHAT_MESSAGES, COLUMN_MESSAGE_USER_EMAIL + " = ?", new String[]{userEmail});
    }
}
