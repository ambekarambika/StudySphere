package com.example.studysphere.adminview;

public class UploadModel {
    public String id;
    public String title ;
    public String description;
    public String fileUrl;
    public String fileType; // e.g., "image", "pdf", "doc"
    public long timestamp;
    public String className;


    public UploadModel() {
        // Required empty constructor for Firestore
    }

    public UploadModel(String id, String title, String description, String fileUrl, String fileType, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.timestamp = timestamp;

    }
}
