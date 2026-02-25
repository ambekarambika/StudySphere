package com.example.studysphere.studentview;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import com.example.studysphere.adapter.StudentTimetableAdapter;
import com.example.studysphere.model.StudentTimetableModel;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class StudentTimetableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentTimetableAdapter adapter;
    private List<StudentTimetableModel> timetableList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_timetable);

        recyclerView = findViewById(R.id.recyclerTimetable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentTimetableAdapter(this, timetableList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadTimetable();
    }

    private void loadTimetable() {
        db.collection("Timetables")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    timetableList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        StudentTimetableModel model = doc.toObject(StudentTimetableModel.class);
                        if (model != null) timetableList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
