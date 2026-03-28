package com.example.studysphere.adminview;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;

import java.util.List;

public class AdminStudentAdapter extends RecyclerView.Adapter<AdminStudentAdapter.ViewHolder> {

    private List<AdminStudentModel> list;

    public AdminStudentAdapter(List<AdminStudentModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminStudentModel student = list.get(position);

        holder.name.setText(student.name);
        holder.enrollment.setText(student.enrollment);

        // 🔥 CLICK EVENT
        holder.itemView.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent = new Intent(context, StudentDetailsActivity.class);
            intent.putExtra("name", student.name);
            intent.putExtra("enrollment", student.enrollment);
            intent.putExtra("email", student.email);
            intent.putExtra("class", student.className);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, enrollment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtStudentName);
            enrollment = itemView.findViewById(R.id.txtEnrollment);
        }
    }
}