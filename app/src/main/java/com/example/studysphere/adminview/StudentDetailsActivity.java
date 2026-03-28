package com.example.studysphere.adminview;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studysphere.R;

public class StudentDetailsActivity extends AppCompatActivity {

    TextView name, enrollment, email, className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        name = findViewById(R.id.txtName);
        enrollment = findViewById(R.id.txtEnrollment);
        email = findViewById(R.id.txtEmail);
        className = findViewById(R.id.txtClass);

        // Get data
        name.setText(getIntent().getStringExtra("name"));
        enrollment.setText(getIntent().getStringExtra("enrollment"));
        email.setText(getIntent().getStringExtra("email"));
        className.setText(getIntent().getStringExtra("class"));
    }
}