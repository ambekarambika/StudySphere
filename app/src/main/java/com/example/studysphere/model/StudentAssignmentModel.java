package com.example.studysphere.model;

public class StudentAssignmentModel {
    public String id, title, description, deadline, className, fileUrl;
    public long timestamp;

    public StudentAssignmentModel() {} // Firestore

    public StudentAssignmentModel(String id, String title, String description, String deadline,
                                  String className, String fileUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.className = className;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }
}
