package com.example.studysphere.adminview;

public class AssignmentModel {
    public String id;
    public String title;
    public String description;
    public String deadline;
    public String targetClass;
    public String imageBase64; // store image here
    public long timestamp;

    public AssignmentModel() {}

    public AssignmentModel(String id, String title, String description,
                           String deadline, String targetClass,
                           String imageBase64, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.targetClass = targetClass;
        this.imageBase64 = imageBase64;
        this.timestamp = timestamp;
    }
}