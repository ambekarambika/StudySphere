package com.example.studysphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;
import com.example.studysphere.model.StudentAssignmentModel;
import com.example.studysphere.studentview.StudentAssignmentDetailActivity;

import java.util.List;

public class StudentAssignmentAdapter extends RecyclerView.Adapter<StudentAssignmentAdapter.MyViewHolder> {

    private final Context context;
    private final List<StudentAssignmentModel> list;

    public StudentAssignmentAdapter(Context context, List<StudentAssignmentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_assignment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StudentAssignmentModel item = list.get(position);

        holder.txtTitle.setText(item.title);
        holder.txtClass.setText("Class: " + item.targetClass);
        holder.txtDeadline.setText("Deadline: " + item.deadline);

        // ⭐ STAR LOGIC
        holder.btnStar.setImageResource(
                item.isImportant ?
                        android.R.drawable.btn_star_big_on :
                        android.R.drawable.btn_star_big_off
        );

        holder.btnStar.setOnClickListener(v -> {
            item.isImportant = !item.isImportant;
            notifyItemChanged(position);
        });

        // OPEN DETAILS
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudentAssignmentDetailActivity.class);
            intent.putExtra("assignment", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDeadline, txtClass;
        Button btnView;
        ImageView btnStar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDeadline = itemView.findViewById(R.id.txtDeadline);
            txtClass = itemView.findViewById(R.id.txtClass);
            btnView = itemView.findViewById(R.id.btnView);
            btnStar = itemView.findViewById(R.id.btnStar); // ✅ CORRECT PLACE
        }
    }
}