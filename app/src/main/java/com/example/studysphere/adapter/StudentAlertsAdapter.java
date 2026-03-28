package com.example.studysphere.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;
import com.example.studysphere.model.StudentAlertModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StudentAlertsAdapter extends RecyclerView.Adapter<StudentAlertsAdapter.ViewHolder> {

    private List<StudentAlertModel> alertList;

    public StudentAlertsAdapter(List<StudentAlertModel> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentAlertModel alert = alertList.get(position);
        holder.title.setText(alert.getTitle());
        holder.type.setText(alert.getType());
        holder.date.setText(
                new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                        .format(alert.getTimestamp())
        );
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, type, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.alertTitle);
            type = itemView.findViewById(R.id.alertType);
            date = itemView.findViewById(R.id.alertDate);
        }
    }
}