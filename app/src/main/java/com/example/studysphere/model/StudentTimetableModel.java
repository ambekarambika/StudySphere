package com.example.studysphere.model;

public class StudentTimetableModel {
    public String id, title, fileUrl;
    public long timestamp;

    public StudentTimetableModel() {}

    public StudentTimetableModel(String id, String title, String fileUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }
}
