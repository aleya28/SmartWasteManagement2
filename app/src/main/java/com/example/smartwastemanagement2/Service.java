package com.example.smartwastemanagement2;

/**
 * Model class representing a service in the Smart Waste Management app.
 */
public class Service {
    private int id;
    private String name;
    private String description;
    private int iconResourceId;
    private String activityClass;

    public Service(int id, String name, String description, int iconResourceId, String activityClass) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResourceId = iconResourceId;
        this.activityClass = activityClass;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getActivityClass() {
        return activityClass;
    }
}
