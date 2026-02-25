package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.studysphere.adminview.AdminAcademicUpdatesActivity;
import com.example.studysphere.adminview.AdminAssignmentsActivity;
import com.example.studysphere.adminview.AdminCampusActivitiesActivity;
import com.example.studysphere.adminview.AdminTimetableActivity;
import com.google.android.material.navigation.NavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView headerName, headerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (drawer.isDrawerOpen(GravityCompat.START)) {

                    drawer.closeDrawer(GravityCompat.START);
                } else {

                    setEnabled(false);
                    AdminDashboardActivity.super.onBackPressed();
                }
            }
        });



        headerName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);



        headerName.setText("Admin User");



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this::handleMenuClick);


        setupGridButtons();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Custom action for back button
                Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close current activity
            }
        });
    }

    private void setupGridButtons() {
        findViewById(R.id.cardAssignments).setOnClickListener(v -> startActivity(new Intent(this, AdminAssignmentsActivity.class)));
        findViewById(R.id.cardAcademicUpdates).setOnClickListener(v -> startActivity(new Intent(this, AdminAcademicUpdatesActivity.class)));
        findViewById(R.id.cardTimetable).setOnClickListener(v -> startActivity(new Intent(this, AdminTimetableActivity.class)));
        findViewById(R.id.cardCampusActivities).setOnClickListener(v -> startActivity(new Intent(this, AdminCampusActivitiesActivity.class)));

    }

    private boolean handleMenuClick(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_assignments) {
            startActivity(new Intent(this, AdminAssignmentsActivity.class));
        } else if (id == R.id.nav_notices) {
            startActivity(new Intent(this, AdminAcademicUpdatesActivity.class));
        } else if (id == R.id.nav_circulars) {
            startActivity(new Intent(this, AdminAcademicUpdatesActivity.class));
        } else if (id == R.id.nav_timetable) {
            startActivity(new Intent(this, AdminTimetableActivity.class));
        } else if (id == R.id.nav_seminars) {
            startActivity(new Intent(this, AdminCampusActivitiesActivity.class));
        } else if (id == R.id.nav_events) {
            startActivity(new Intent(this, AdminCampusActivitiesActivity.class));

        } else if (id == R.id.nav_logout) {
            showMessage("Logging out...");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



}
