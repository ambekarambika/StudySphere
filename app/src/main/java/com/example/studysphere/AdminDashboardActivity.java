package com.example.studysphere;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.adminview.*;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView headerName, headerEmail;

    private TextView txtMyStudents, txtTotalStudents, txtTotalAssignments, txtTotalAlerts;

    private RecyclerView recyclerStudents;
    private AdminStudentAdapter studentAdapter;
    private List<AdminStudentModel> studentList = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // TEXT VIEWS
        txtMyStudents = findViewById(R.id.txtMyStudents);
        txtTotalStudents = findViewById(R.id.txtTotalStudents);
        txtTotalAssignments = findViewById(R.id.txtTotalAssignments);
        txtTotalAlerts = findViewById(R.id.txtTotalAlerts);

        // RECYCLER VIEW
        recyclerStudents = findViewById(R.id.recyclerStudents);
        recyclerStudents.setLayoutManager(new LinearLayoutManager(this));

        studentAdapter = new AdminStudentAdapter(studentList);
        recyclerStudents.setAdapter(studentAdapter);

        db = FirebaseFirestore.getInstance();

        // HEADER
        headerName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        headerEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            String email = user.getEmail();

            // FORMAT NAME
            String namePart = email.substring(0, email.indexOf("@"))
                    .replace(".", " ")
                    .replace("_", " ");

            String[] words = namePart.split(" ");
            StringBuilder formattedName = new StringBuilder();

            for (String w : words) {
                if (!w.isEmpty()) {
                    formattedName.append(Character.toUpperCase(w.charAt(0)))
                            .append(w.substring(1))
                            .append(" ");
                }
            }

            headerName.setText(formattedName.toString().trim());
            headerEmail.setText(email);

            // LOAD DATA
            loadMyStudents(email);
            loadAnalytics();
        }

        // DRAWER
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::handleMenuClick);
        setupGridButtons();
    }

    // 🔥 LOAD STUDENTS (COUNT + LIST)
    private void loadMyStudents(String teacherEmail) {

        db.collection("class_teachers")
                .whereEqualTo("teacherEmail", teacherEmail)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.isEmpty()) {
                        Toast.makeText(this, "No class assigned", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String classId = snapshot.getDocuments().get(0).getId();

                    db.collection("users")
                            .whereEqualTo("class", classId)
                            .whereEqualTo("role", "student")
                            .get()
                            .addOnSuccessListener(studentSnap -> {

                                // COUNT
                                animateText(txtMyStudents, studentSnap.size(), "My Students");

                                // LIST
                                studentList.clear();

                                for (DocumentSnapshot doc : studentSnap.getDocuments()) {

                                    String name = doc.getString("name");
                                    String enrollment = doc.getString("enrollment");
                                    String email = doc.getString("email");
                                    String studentClass = doc.getString("class");

                                    studentList.add(new AdminStudentModel(
                                            name != null ? name : "No Name",
                                            enrollment != null ? enrollment : "No Enrollment",
                                            email != null ? email : "No Email",
                                            studentClass != null ? studentClass : "N/A"
                                    ));
                                }

                                studentAdapter.notifyDataSetChanged();
                            });
                });
    }

    // 🔥 ANALYTICS
    private void loadAnalytics() {

        db.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .addOnSuccessListener(snapshot ->
                        animateText(txtTotalStudents, snapshot.size(), "Total Students"));

        db.collection("assignments")
                .get()
                .addOnSuccessListener(snapshot ->
                        animateText(txtTotalAssignments, snapshot.size(), "Assignments"));

        db.collection("AcademicUpdates")
                .get()
                .addOnSuccessListener(snapshot ->
                        animateText(txtTotalAlerts, snapshot.size(), "Alerts"));
    }

    // 🔥 ANIMATION
    private void animateText(TextView view, int value, String label) {
        ValueAnimator animator = ValueAnimator.ofInt(0, value);
        animator.setDuration(1000);

        animator.addUpdateListener(animation -> {
            int val = (int) animation.getAnimatedValue();
            view.setText(label + ": " + val);
        });

        animator.start();
    }
    private void openCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        builder.setShowTitle(true); // optional
        // builder.setToolbarColor(getResources().getColor(R.color.purple_500)); // optional

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private void setupGridButtons() {
        String url = "https://sparktrack-mini-8pjs.vercel.app/";
        findViewById(R.id.cardAssignments).setOnClickListener(v -> startActivity(new Intent(this, AdminAssignmentsActivity.class)));
        findViewById(R.id.cardAcademicUpdates).setOnClickListener(v -> startActivity(new Intent(this, AdminAcademicUpdatesActivity.class)));
        findViewById(R.id.cardTimetable).setOnClickListener(v -> startActivity(new Intent(this, AdminTimetableActivity.class)));
        findViewById(R.id.cardCampusActivities).setOnClickListener(v -> startActivity(new Intent(this, AdminCampusActivitiesActivity.class)));
        findViewById(R.id.cardpbl).setOnClickListener(v -> openCustomTab(url));

    }

    private boolean handleMenuClick(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_assignments) {
            startActivity(new Intent(this, AdminAssignmentsActivity.class));
        } else if (id == R.id.nav_notices) {
            startActivity(new Intent(this, AdminAcademicUpdatesActivity.class));
        } else if (id == R.id.nav_timetable) {
            startActivity(new Intent(this, AdminTimetableActivity.class));
        } else if (id == R.id.nav_events) {
            startActivity(new Intent(this, AdminCampusActivitiesActivity.class));
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}