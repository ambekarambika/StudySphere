package com.example.studysphere.adminview;
public class AdminStudentModel {
    public String name;
    public String enrollment;
    public String email;
    public String className;

    public AdminStudentModel() {}

    public AdminStudentModel(String name, String enrollment, String email, String className) {
        this.name = name;
        this.enrollment = enrollment;
        this.className = className;
        this.email = email;
    }
}