package com.example.studysphere.adminview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminAcademicUpdatesActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 100;

    private Button btnAddCircular, btnAddNotice, btndt;
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private UploadAdapter adapter;
    private List<UploadModel> list = new ArrayList<>();
    private Uri fileUri;
    private ProgressDialog progressDialog;
    private String currentType = "general"; // "circular" or "notice"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_academic_updates);

        btnAddCircular = findViewById(R.id.btnAddCircular);
        btnAddNotice = findViewById(R.id.btnAddNotice);
        recyclerView = findViewById(R.id.recyclerAcademicUpdates);



        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UploadAdapter(this, list, id -> deleteItem(id));
        recyclerView.setAdapter(adapter);

        loadUpdates();

        btnAddCircular.setOnClickListener(v -> {
            currentType = "circular";
            openFilePicker();
        });

        btnAddNotice.setOnClickListener(v -> {
            currentType = "notice";
            openFilePicker();
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // any file type
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
        StorageReference ref = storage.getReference().child("academic_updates/" + fileName);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(uri.toString());
                }))
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
                currentType.equals("circular") ? "New Circular" : "New Notice",
                "Uploaded by Admin",
                url,
                fileType,
                System.currentTimeMillis()
        );

        firestore.collection("AcademicUpdates")
                .document(id)
                .set(model)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                    loadUpdates();
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

    private void loadUpdates() {
        firestore.collection("AcademicUpdates")
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
                        Toast.makeText(this, "Failed to load updates", Toast.LENGTH_SHORT).show());
    }

    private void deleteItem(String id) {
        firestore.collection("AcademicUpdates").document(id).delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadUpdates();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error deleting: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
