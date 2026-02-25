package com.example.studysphere.adminview;

public class AssignmentModel {
    public String id;
    public String title;
    public String description;
    public String deadline;
    public String className;
    public String fileUrl; // now stores file URL instead of Base64
    public long timestamp;

    public AssignmentModel() {}

    public AssignmentModel(String id, String title, String description,
                           String deadline, String className,
                           String fileUrl, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.className = className;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
    }
}
