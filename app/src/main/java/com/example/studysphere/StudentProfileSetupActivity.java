package com.example.studysphere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studysphere.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class StudentProfileSetupActivity extends AppCompatActivity {

    EditText edtName, edtEnroll, edtClass;
    Button btnSave;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_setup);

        edtName = findViewById(R.id.edtName);
        edtEnroll = findViewById(R.id.edtEnroll);
        edtClass = findViewById(R.id.edtClass);
        btnSave = findViewById(R.id.btnSave);

        firestore = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String name = edtName.getText().toString().trim();
        String enroll = edtEnroll.getText().toString().trim();
        String className = edtClass.getText().toString().trim();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(name.isEmpty() || enroll.isEmpty() || className.isEmpty()){
            Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("enrollment",enroll);
        map.put("class", normalizeClass(className));
        map.put("role","student");

        firestore.collection("users").document(uid)
                .set(map)
                .addOnSuccessListener(aVoid -> {
                    startActivity(new Intent(this, StudentHomeActivity.class));
                    finish();
                });


    }
    private String normalizeClass(String className) {
        if (className == null) return "";
        return className
                .replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase()
                .trim();
    }
}