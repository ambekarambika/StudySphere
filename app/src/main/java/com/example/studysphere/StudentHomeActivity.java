package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.studysphere.studentview.StudentAcademicUpdatesActivity;
import com.example.studysphere.studentview.StudentAlertsActivity;
import com.example.studysphere.studentview.StudentAssignmentsActivity;
import com.example.studysphere.studentview.StudentCampusActivitiesActivity;
import com.example.studysphere.studentview.StudentTimetableActivity;
import com.google.android.material.navigation.NavigationView;

public class StudentHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton alertButton;
    private TextView headerName, headerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Student Dashboard");

        headerName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);


        headerName.setText("Admin User");


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Intent intent = new Intent(StudentHomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
