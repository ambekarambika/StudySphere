package com.example.studysphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;
import com.example.studysphere.model.StudentCampusActivityModel;

import java.util.List;

public class StudentCampusActivitiesAdapter extends RecyclerView.Adapter<StudentCampusActivitiesAdapter.MyViewHolder> {

    private final Context context;
    private final List<StudentCampusActivityModel> list;

    public StudentCampusActivitiesAdapter(Context context, List<StudentCampusActivityModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_campus, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StudentCampusActivityModel item = list.get(position);

        holder.txtTitle.setText(item.title);
        holder.txtDescription.setText(item.description);

        // Handle image visibility
        if(item.imageUrl != null && !item.imageUrl.isEmpty()){
            holder.imgPreview.setVisibility(View.VISIBLE);
            // Load image via Picasso if available
            try {
                com.squareup.picasso.Picasso.get()
                        .load(item.imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(holder.imgPreview);
            } catch (Exception e){
                holder.imgPreview.setVisibility(View.GONE);
            }
        } else {
            holder.imgPreview.setVisibility(View.GONE);
        }

        // View button opens file or image
        holder.btnView.setOnClickListener(v -> {
            if(item.fileUrl != null && !item.fileUrl.isEmpty()){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.fileUrl));
                context.startActivity(intent);
            } else if(item.imageUrl != null && !item.imageUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.imageUrl));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No file or image available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription;
        ImageView imgPreview;
        Button btnView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imgPreview = itemView.findViewById(R.id.imgPreview);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
