package com.example.studysphere.studentview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studysphere.R;
import com.example.studysphere.model.StudentAssignmentModel;

public class StudentAssignmentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignment_detail);

        ImageView img = findViewById(R.id.detailImage);
        TextView title = findViewById(R.id.detailTitle);
        TextView desc = findViewById(R.id.detailDesc);
        TextView deadline = findViewById(R.id.detailDeadline);
        TextView classTxt = findViewById(R.id.detailClass);

        StudentAssignmentModel model =
                (StudentAssignmentModel) getIntent().getSerializableExtra("assignment");

        title.setText(model.title);
        desc.setText(model.description);
        deadline.setText("Deadline: " + model.deadline);
        classTxt.setText("Class: " + model.targetClass);

        if (model.imageBase64 != null) {
            byte[] bytes = Base64.decode(model.imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img.setImageBitmap(bitmap);
        }
    }
}