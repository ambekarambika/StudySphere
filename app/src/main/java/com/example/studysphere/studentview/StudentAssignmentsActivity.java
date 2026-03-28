package com.example.studysphere.studentview;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;
import com.example.studysphere.adapter.StudentAssignmentAdapter;
import com.example.studysphere.model.StudentAssignmentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class StudentAssignmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAssignmentAdapter adapter;
    private List<StudentAssignmentModel> assignmentsList = new ArrayList<>();
    private FirebaseFirestore db;
    private String studentClass = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignments);

        recyclerView = findViewById(R.id.recyclerStudentAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentAssignmentAdapter(this, assignmentsList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAssignments();
    }

    private void loadAssignments() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get student's class
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {

                        studentClass = normalizeClass(doc.getString("class"));

                        // Load only assignments for that class
                        db.collection("assignments")
                                .whereEqualTo("targetClass", studentClass)
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .addSnapshotListener((value, error) -> {
                                    if (error != null || value == null) return;

                                    assignmentsList.clear();
                                    for (DocumentSnapshot d : value.getDocuments()) {
                                        StudentAssignmentModel model = d.toObject(StudentAssignmentModel.class);
                                        if (model != null) assignmentsList.add(model);
                                    }
                                    adapter.notifyDataSetChanged();
                                });
                    }
                });
    }

    private String normalizeClass(String className) {
        if (className == null) return "";
        return className
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase()
                .trim();
    }
}