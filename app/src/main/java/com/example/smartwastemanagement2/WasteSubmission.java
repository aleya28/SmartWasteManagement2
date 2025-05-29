package com.example.smartwastemanagement2;

/**
 * Model class to represent a waste submission
 */
public class WasteSubmission {
    private String wasteType;
    private float weight;
    private String pickupDate;
    private String pickupTime;
    private String address;
    private String notes;
    private String userId;
    private String status;
    private String referenceId;
    private long paidAt; // Timestamp when payment was made

    // Default constructor required for Firebase
    public WasteSubmission() {
    }

    // Constructor with all fields
    public WasteSubmission(String wasteType, float weight, String pickupDate, String pickupTime, 
                           String address, String notes, String userId) {
        this.wasteType = wasteType;
        this.weight = weight;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.address = address;
        this.notes = notes;
        this.userId = userId;
        this.status = "Pending"; // Default status
        this.referenceId = generateReferenceId(); // Generate a reference ID
    }

    // Generate a simple reference ID
    private String generateReferenceId() {
        // Generate a simple reference ID based on current time and random number
        return "WS" + System.currentTimeMillis() % 10000 + "-" + ((int) (Math.random() * 9000) + 1000);
    }

    // Getters and setters
    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public long getPaidAt() {
        return paidAt;
    }
    
    public void setPaidAt(long paidAt) {
        this.paidAt = paidAt;
    }
}
