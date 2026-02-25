package com.example.studysphere.studentview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.adapter.StudentAlertsAdapter;
import com.example.studysphere.model.StudentAlertModel;
import com.google.firebase.firestore.*;
import com.example.studysphere.R;

import java.util.*;

public class StudentAlertsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAlertsAdapter adapter;
    private List<StudentAlertModel> alertList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_alerts);

        recyclerView = findViewById(R.id.alertsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentAlertsAdapter(alertList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadAlerts();
    }

    private void loadAlerts() {
        List<String> collections = Arrays.asList("assignments", "AcademicUpdates", "CampusActivities", "Timetables");

        for (String col : collections) {
            db.collection(col)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null || snapshots == null) return;


                        alertList.removeIf(alert -> alert.getType().equals(col));

                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            StudentAlertModel alert = new StudentAlertModel(
                                    doc.getString("title"),
                                    doc.getString("description"),
                                    doc.contains("timestamp") ? doc.getLong("timestamp") : System.currentTimeMillis(),
                                    col
                            );
                            alertList.add(alert);
                        }


                        Collections.sort(alertList, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

                        adapter.notifyDataSetChanged();
                    });
        }
    }

}
