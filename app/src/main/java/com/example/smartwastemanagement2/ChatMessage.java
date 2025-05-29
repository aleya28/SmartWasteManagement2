package com.example.smartwastemanagement2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model class representing a chat message
 */
public class ChatMessage {
    public static final int TYPE_USER = 1;
    public static final int TYPE_BOT = 2;
    
    private String message;
    private int messageType;
    private long timestamp;
    
    /**
     * Create a new chat message
     * @param message The message text
     * @param messageType The message type (TYPE_USER or TYPE_BOT)
     */
    /**
     * Create a new chat message with current timestamp
     * @param message The message text
     * @param messageType The message type (TYPE_USER or TYPE_BOT)
     */
    public ChatMessage(String message, int messageType) {
        this.message = message;
        this.messageType = messageType;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Create a new chat message with specific timestamp
     * @param message The message text
     * @param messageType The message type (TYPE_USER or TYPE_BOT)
     * @param timestamp The timestamp to use
     */
    public ChatMessage(String message, int messageType, long timestamp) {
        this.message = message;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getMessageType() {
        return messageType;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Set the timestamp manually (needed for loading from database)
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Check if this is a user message
     */
    public boolean isUser() {
        return messageType == TYPE_USER;
    }
    
    /**
     * Check if this is a bot message
     */
    public boolean isBot() {
        return messageType == TYPE_BOT;
    }
    
    /**
     * Get a formatted time string for this message
     */
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
