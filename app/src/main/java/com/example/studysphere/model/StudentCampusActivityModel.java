package com.example.studysphere.model;

public class StudentCampusActivityModel {
    public String id;
    public String title;
    public String description;
    public String imageUrl; // optional, for images
    public String fileUrl;  // optional, for PDFs or other files
    public long timestamp;

    // Empty constructor for Firestore
    public StudentCampusActivityModel() {}

    public StudentCampusActivityModel(String id, String title, String description, String imageUrl, String fileUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }
}
