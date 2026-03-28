package com.example.studysphere.model;

public class StudentAssignmentModel implements java.io.Serializable{
    public String id, title, description, deadline, targetClass, fileUrl;
    public long timestamp;
    public String imageBase64;
    public boolean isImportant = false;


    public StudentAssignmentModel() {} // Firestore

    public StudentAssignmentModel(String id, String title, String description, String deadline,
                                  String className, String fileUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.targetClass = className;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }
}
