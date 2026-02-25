package com.example.studysphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import com.example.studysphere.model.StudentTimetableModel;

import java.util.List;

public class StudentTimetableAdapter extends RecyclerView.Adapter<StudentTimetableAdapter.MyViewHolder> {
    private final Context context;
    private final List<StudentTimetableModel> list;

    public StudentTimetableAdapter(Context context, List<StudentTimetableModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_student_timetable, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StudentTimetableModel item = list.get(position);
        holder.txtTitle.setText(item.title);

        holder.btnView.setOnClickListener(v -> {
            if(item.fileUrl != null && !item.fileUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        Button btnView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
