package com.example.studysphere.adminview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminCampusActivitiesActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 300;

    private EditText edtTitle, edtDescription, edtDate;
    private Button btnAddSeminar, btnAddEvent;
    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private UploadAdapter adapter;
    private List<UploadModel> list = new ArrayList<>();
    private Uri fileUri;
    private ProgressDialog progressDialog;
    private String currentType = "seminar"; // "seminar" or "event"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_campus_activities);

        edtTitle = findViewById(R.id.edtActivityTitle);
        edtDescription = findViewById(R.id.edtActivityDescription);
        edtDate = findViewById(R.id.edtActivityDate);
        btnAddSeminar = findViewById(R.id.btnAddSeminar);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        recyclerView = findViewById(R.id.recyclerCampusActivities);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UploadAdapter(this, list, id -> deleteItem(id));
        recyclerView.setAdapter(adapter);

        loadActivities();

        btnAddSeminar.setOnClickListener(v -> {
            currentType = "seminar";
            openFilePicker();
        });

        btnAddEvent.setOnClickListener(v -> {
            currentType = "event";
            openFilePicker();
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select File"), FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            uploadFileToFirebase();
        }
    }

    private void uploadFileToFirebase() {
        if (fileUri == null) return;

        progressDialog.show();
        String fileName = UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child("campus_activities/" + fileName);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> saveToFirestore(uri.toString())))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToFirestore(String url) {
        String id = UUID.randomUUID().toString();
        String fileType = getFileType(fileUri.toString());

        UploadModel model = new UploadModel(
                id,
                edtTitle.getText().toString().trim().isEmpty() ? "New " + capitalize(currentType) : edtTitle.getText().toString().trim(),
                edtDescription.getText().toString().trim().isEmpty() ? "No description" : edtDescription.getText().toString().trim() + "\nDate: " + edtDate.getText().toString().trim(),
                url,
                fileType,
                System.currentTimeMillis()
        );

        firestore.collection("CampusActivities")
                .document(id)
                .set(model)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, capitalize(currentType) + " uploaded successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                    loadActivities();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getFileType(String uri) {
        if (uri.contains(".pdf")) return "pdf";
        else if (uri.contains(".doc") || uri.contains(".docx")) return "doc";
        else if (uri.contains("image")) return "image";
        else return "file";
    }

    private void loadActivities() {
        firestore.collection("CampusActivities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        UploadModel m = doc.toObject(UploadModel.class);
                        list.add(m);
                    }
                    adapter.setUploads(list);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load activities", Toast.LENGTH_SHORT).show());
    }

    private void deleteItem(String id) {
        firestore.collection("CampusActivities").document(id).delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadActivities();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error deleting: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        edtTitle.setText("");
        edtDescription.setText("");
        edtDate.setText("");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
