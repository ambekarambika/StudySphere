package com.example.studysphere.studentview;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import com.example.studysphere.adapter.StudentAssignmentAdapter;
import com.example.studysphere.model.StudentAssignmentModel;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class StudentAssignmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAssignmentAdapter adapter;
    private List<StudentAssignmentModel> assignmentsList = new ArrayList<>();
    private FirebaseFirestore db;

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
        db.collection("assignments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    assignmentsList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        StudentAssignmentModel model = doc.toObject(StudentAssignmentModel.class);
                        if (model != null) assignmentsList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
