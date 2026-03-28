package com.example.studysphere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.messaging.FirebaseMessaging;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studysphere.studentview.*;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class StudentHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView txtBadge;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton alertButton;
    private TextView headerName, headerEmail, headerClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student Dashboard");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        txtBadge = findViewById(R.id.txtBadge);
        loadNotificationCount();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("students");
        // ✅ Fix: Initialize header views properly
        headerName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        headerEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        headerClass = navigationView.getHeaderView(0).findViewById(R.id.nav_header_class);

        loadStudentProfile(); // 🔥 Load student data

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        alertButton = findViewById(R.id.btnAlerts);
        if (alertButton != null) {
            alertButton.setOnClickListener(v ->
                    startActivity(new Intent(this, StudentAlertsActivity.class))
            );
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, StudentHomeActivity.class));

            } else if (id == R.id.nav_academic_updates) {
                startActivity(new Intent(this, StudentAcademicUpdatesActivity.class));

            } else if (id == R.id.nav_campus_activities) {
                startActivity(new Intent(this, StudentCampusActivitiesActivity.class));

            } else if (id == R.id.nav_timetable) {
                startActivity(new Intent(this, StudentTimetableActivity.class));

            } else if (id == R.id.nav_assignments) {
                startActivity(new Intent(this, StudentAssignmentsActivity.class));

            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(this, StudentAlertsActivity.class));

            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        findViewById(R.id.cardAcademicUpdates).setOnClickListener(v ->
                startActivity(new Intent(this, StudentAcademicUpdatesActivity.class))
        );

        findViewById(R.id.cardCampusActivities).setOnClickListener(v ->
                startActivity(new Intent(this, StudentCampusActivitiesActivity.class))
        );

        findViewById(R.id.cardTimetable).setOnClickListener(v ->
                startActivity(new Intent(this, StudentTimetableActivity.class))
        );

        findViewById(R.id.cardAssignments).setOnClickListener(v ->
                startActivity(new Intent(this, StudentAssignmentsActivity.class))
        );
        String url = "https://sparktrack-mini-8pjs.vercel.app/";

        findViewById(R.id.cardpbl).setOnClickListener(v -> openCustomTab(url));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(StudentHomeActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    private void openCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        builder.setShowTitle(true); // optional
        // builder.setToolbarColor(getResources().getColor(R.color.purple_500)); // optional

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    // 🔥 Fetch student profile from Firestore
    private void loadStudentProfile() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    if (doc.exists()) {
                        headerName.setText(doc.getString("name"));
                        headerEmail.setText("Enrollment: " + doc.getString("enrollment"));
                        headerClass.setText("Class: " + doc.getString("class"));
                    } else {
                        startActivity(new Intent(this, StudentProfileSetupActivity.class));
                        finish();
                    }
                });
    }
    private void loadNotificationCount() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<String> collections = Arrays.asList(
                "assignments", "AcademicUpdates", "CampusActivities", "Timetables"
        );

        final int[] unreadCount = {0};

        for (String col : collections) {
            db.collection(col)
                    .addSnapshotListener((value, error) -> {
                        if (value == null) return;

                        unreadCount[0] = 0;

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            List<String> readBy = (List<String>) doc.get("readBy");
                            if (readBy == null || !readBy.contains(uid)) {
                                unreadCount[0]++;
                            }
                        }

                        if (unreadCount[0] > 0) {
                            txtBadge.setVisibility(View.VISIBLE);
                            txtBadge.setText(String.valueOf(unreadCount[0]));
                        } else {
                            txtBadge.setVisibility(View.GONE);
                        }
                    });
        }
    }
}