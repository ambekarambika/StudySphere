package com.example.studysphere.studentview;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import com.example.studysphere.adapter.StudentCampusActivitiesAdapter;
import com.example.studysphere.model.StudentCampusActivityModel;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class StudentCampusActivitiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentCampusActivitiesAdapter adapter;
    private List<StudentCampusActivityModel> activitiesList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_campus_activities);

        recyclerView = findViewById(R.id.recyclerCampus);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentCampusActivitiesAdapter(this, activitiesList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadActivities();
    }

    private void loadActivities() {
        db.collection("CampusActivities")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    activitiesList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        StudentCampusActivityModel model = doc.toObject(StudentCampusActivityModel.class);
                        if (model != null) activitiesList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
