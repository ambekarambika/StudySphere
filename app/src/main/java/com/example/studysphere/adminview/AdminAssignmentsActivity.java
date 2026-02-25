package com.example.studysphere.adminview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;
import java.util.*;

public class AdminAssignmentsActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription, edtDeadline, edtClass;
    private Button btnChooseFile, btnUpload;
    private RecyclerView recyclerAssignments;
    private Uri fileUri = null;
    private FirebaseFirestore firestore;
    private StorageReference storageRef;
    private List<AssignmentModel> list = new ArrayList<>();
    private AssignmentAdapter adapter;
    private static final int PICK_FILE = 1001;

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
        storageRef = FirebaseStorage.getInstance().getReference("assignments");

        recyclerAssignments.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AssignmentAdapter(list,
                (id, fileUrl) -> deleteAssignment(id, fileUrl),
                model -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(model.fileUrl));
                    startActivity(intent);
                });
        recyclerAssignments.setAdapter(adapter);

        btnChooseFile.setOnClickListener(v -> chooseFile());
        btnUpload.setOnClickListener(v -> uploadAssignment());

        loadAssignments();
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadAssignment() {
        if (fileUri == null || edtTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select a file and enter details", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading...");
        dialog.setCancelable(false);
        dialog.show();

        String id = UUID.randomUUID().toString();
        StorageReference fileRef = storageRef.child(id + "_" + Objects.requireNonNull(fileUri.getLastPathSegment()));

        fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    AssignmentModel model = new AssignmentModel(
                            id,
                            edtTitle.getText().toString(),
                            edtDescription.getText().toString(),
                            edtDeadline.getText().toString(),
                            edtClass.getText().toString(),
                            uri.toString(),
                            System.currentTimeMillis()
                    );

                    firestore.collection("assignments").document(id)
                            .set(model)
                            .addOnSuccessListener(aVoid -> {
                                dialog.dismiss();
                                Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                loadAssignments();
                            })
                            .addOnFailureListener(e -> {
                                dialog.dismiss();
                                Toast.makeText(this, "Failed to upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
        ).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadAssignments() {
        firestore.collection("assignments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    list.addAll(queryDocumentSnapshots.toObjects(AssignmentModel.class));
                    adapter.notifyDataSetChanged();
                });
    }

    private void deleteAssignment(String id, String fileUrl) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Deleting...");
        dialog.setCancelable(false);
        dialog.show();

        StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);
        fileRef.delete()
                .addOnSuccessListener(unused -> firestore.collection("assignments").document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            loadAssignments();
                        })
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(this, "Failed to delete Firestore record", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to delete file", Toast.LENGTH_SHORT).show();
                });
    }
}
