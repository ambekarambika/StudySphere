package com.example.studysphere.model;

public class StudentAcademicModel {
    public String id, title, description, fileUrl;
    public long timestamp;

    public StudentAcademicModel() {}

    public StudentAcademicModel(String id, String title, String description, String fileUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }
}
