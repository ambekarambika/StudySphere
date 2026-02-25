package com.example.studysphere.adminview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studysphere.R;

import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {

    private final Context context;
    private List<UploadModel> list;
    private final DeleteListener deleteListener;

    public interface DeleteListener {
        void onDelete(String id);
    }

    public UploadAdapter(Context context, List<UploadModel> list, DeleteListener deleteListener) {
        this.context = context;
        this.list = list;
        this.deleteListener = deleteListener;
    }

    public void setUploads(List<UploadModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_upload, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UploadModel model = list.get(position);

        holder.txtTitle.setText(model.title);
        holder.txtDesc.setText(model.description);

        if ("image".equals(model.fileType)) {
            holder.imgPreview.setVisibility(View.VISIBLE);
            Glide.with(context).load(model.fileUrl).into(holder.imgPreview);
        } else {
            holder.imgPreview.setVisibility(View.GONE);
        }

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.fileUrl));
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(model.id));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc;
        ImageView imgPreview;
        Button btnView, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            imgPreview = itemView.findViewById(R.id.imgPreview);
            btnView = itemView.findViewById(R.id.btnView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
