package com.example.studysphere.model;

public class StudentAlertModel {
    private String title;
    private String description;
    private String type;   // assignment, notice, event etc.
    private long timestamp;


    public StudentAlertModel(String title, String description, long timestamp, String type) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public String getType() { return type; }
}
