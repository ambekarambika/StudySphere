package com.example.studysphere.adminview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

public class AdminAssignmentsActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription, edtDeadline, edtClass;
    private Button btnChooseFile, btnUpload;
    private RecyclerView recyclerAssignments;
    private FirebaseFirestore firestore;
    private List<AssignmentModel> list = new ArrayList<>();
    private AssignmentAdapter adapter;
    private static final int PICK_FILE = 1001;
    private String imageBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assignments);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtDeadline = findViewById(R.id.edtDeadline);
        edtClass = findViewById(R.id.edtClass);
        btnChooseFile = findViewById(R.id.btnChooseImage);
        btnUpload = findViewById(R.id.btnUpload);
        recyclerAssignments = findViewById(R.id.recyclerAssignments);

        firestore = FirebaseFirestore.getInstance();

        recyclerAssignments.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AssignmentAdapter(list,
                id -> deleteAssignment(id),
                model -> Toast.makeText(this, model.title, Toast.LENGTH_SHORT).show());

        recyclerAssignments.setAdapter(adapter);

        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnUpload.setOnClickListener(v -> uploadAssignment());

        loadAssignments();
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] imageBytes = baos.toByteArray();

                imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadAssignment() {

        if (imageBase64 == null || edtTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Select image & enter details", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading...");
        dialog.setCancelable(false);
        dialog.show();

        String id = UUID.randomUUID().toString();

        AssignmentModel model = new AssignmentModel(
                id,
                edtTitle.getText().toString(),
                edtDescription.getText().toString(),
                edtDeadline.getText().toString(),
                normalizeClass(edtClass.getText().toString()),
                imageBase64,
                System.currentTimeMillis()
        );

        firestore.collection("assignments").document(id)
                .set(model)
                .addOnSuccessListener(aVoid -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                    loadAssignments();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadAssignments() {
        firestore.collection("assignments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        AssignmentModel model = doc.toObject(AssignmentModel.class);
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void deleteAssignment(String id) {
        firestore.collection("assignments").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadAssignments();
                });
    }

    private void clearFields() {
        edtTitle.setText("");
        edtDescription.setText("");
        edtDeadline.setText("");
        edtClass.setText("");
        imageBase64 = null;
    }

    // Normalize class name
    private String normalizeClass(String className) {
        if (className == null) return "";
        return className
                .replaceAll("[^a-zA-Z0-9]", "") // remove spaces, hyphens
                .toUpperCase()
                .trim();
    }
}