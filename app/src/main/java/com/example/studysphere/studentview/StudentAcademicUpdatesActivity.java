package com.example.studysphere.studentview;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import com.example.studysphere.R;
import com.example.studysphere.adapter.StudentAcademicAdapter;
import com.example.studysphere.model.StudentAcademicModel;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class StudentAcademicUpdatesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText edtSearch;
    private StudentAcademicAdapter adapter;
    private List<StudentAcademicModel> updatesList = new ArrayList<>();
    private List<StudentAcademicModel> filteredList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_academic_updates);

        recyclerView = findViewById(R.id.recyclerUpdates);
        edtSearch = findViewById(R.id.edtSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentAcademicAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadUpdates();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUpdates(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUpdates() {
        db.collection("AcademicUpdates")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    updatesList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        StudentAcademicModel model = doc.toObject(StudentAcademicModel.class);
                        if (model != null) updatesList.add(model);
                    }
                    filteredList.clear();
                    filteredList.addAll(updatesList);
                    adapter.notifyDataSetChanged();
                });
    }

    private void filterUpdates(String query) {
        filteredList.clear();
        for (StudentAcademicModel item : updatesList) {
            if (item.title.toLowerCase().contains(query.toLowerCase()) ||
                    item.description.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
