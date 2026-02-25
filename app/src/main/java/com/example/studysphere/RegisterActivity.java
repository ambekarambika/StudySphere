package com.example.studysphere;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure no logged-in user interferes
        FirebaseAuth.getInstance().signOut();

        setContentView(R.layout.activity_register);

        // Initialize views
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        confirmPasswordInput = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.buttonRegister);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email format");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assign role based on email
        String role;
        if (email.toLowerCase().endsWith("@student.mit.edu")) {
            role = "Student";
        } else if (email.toLowerCase().endsWith("@mit.edu")) {
            role = "Admin";
        } else {
            Toast.makeText(this, "Registration allowed only for MIT emails", Toast.LENGTH_LONG).show();
            emailInput.setError("Invalid MIT email");
            return;
        }

        progressDialog.show();

        // Firebase registration
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Store user in Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("role", role);

                        db.collection("users").document(user.getUid())
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this,
                                            "Registration successful! Role: " + role,
                                            Toast.LENGTH_SHORT).show();


                                    // Open correct dashboard based on role
                                    if (role.equals("Admin")) {
                                        // Admin email → go to Admin dashboard
                                        startActivity(new Intent(RegisterActivity.this, AdminDashboardActivity.class));
                                    } else if (role.equals("Student")) {
                                        // Student email → go to Student dashboard
                                        startActivity(new Intent(RegisterActivity.this, StudentHomeActivity.class));
                                    } else {
                                        // Safety fallback (should not happen)
                                        Toast.makeText(this, "Unknown role, cannot redirect", Toast.LENGTH_SHORT).show();
                                    }


                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this,
                                        "Firestore Error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());

                        // Send verification email
                        user.sendEmailVerification();

                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Auth Error: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
