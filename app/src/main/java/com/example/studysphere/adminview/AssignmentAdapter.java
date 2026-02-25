package com.example.studysphere.adminview;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.MyViewHolder> {

    private List<AssignmentModel> list;
    private final DeleteListener deleteListener;
    private final ViewListener viewListener;

    public interface DeleteListener { void onDelete(String id, String fileUrl); }
    public interface ViewListener { void onView(AssignmentModel m); }

    public AssignmentAdapter(List<AssignmentModel> list,
                             DeleteListener deleteListener,
                             ViewListener viewListener) {
        this.list = list;
        this.deleteListener = deleteListener;
        this.viewListener = viewListener;
    }

    public void setAssignments(List<AssignmentModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AssignmentModel m = list.get(position);

        holder.txtTitle.setText(m.title);
        holder.txtClass.setText("Class: " + m.className);
        holder.txtDeadline.setText("Deadline: " + m.deadline);

        holder.btnView.setOnClickListener(v -> viewListener.onView(m));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(m.id, m.fileUrl));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDeadline, txtClass;
        Button btnDelete, btnView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDeadline = itemView.findViewById(R.id.txtDeadline);
            txtClass = itemView.findViewById(R.id.txtClass);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
