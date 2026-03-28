package com.example.studysphere.adminview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

    public interface DeleteListener { void onDelete(String id); }
    public interface ViewListener { void onView(AssignmentModel m); }

    public AssignmentAdapter(List<AssignmentModel> list,
                             DeleteListener deleteListener,
                             ViewListener viewListener) {
        this.list = list;
        this.deleteListener = deleteListener;
        this.viewListener = viewListener;
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
        holder.txtClass.setText("Class: " + m.targetClass);
        holder.txtDeadline.setText("Deadline: " + m.deadline);

        try {
            if (m.imageBase64 != null && !m.imageBase64.isEmpty()) {
                byte[] decodedBytes = Base64.decode(m.imageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.imgAssignment.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            holder.imgAssignment.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(m.id));
        holder.itemView.setOnClickListener(v -> viewListener.onView(m));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDeadline, txtClass;
        ImageView imgAssignment;
        Button btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDeadline = itemView.findViewById(R.id.txtDeadline);
            txtClass = itemView.findViewById(R.id.txtClass);
            imgAssignment = itemView.findViewById(R.id.imgAssignment);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}