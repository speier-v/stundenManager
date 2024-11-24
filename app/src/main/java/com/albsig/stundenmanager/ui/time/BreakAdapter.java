package com.albsig.stundenmanager.ui.time;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.FirestoreUtil;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BreakAdapter extends RecyclerView.Adapter<BreakAdapter.BreakViewHolder> {

    public interface OnBreakDeletedListener {
        void onBreakDeleted();
    }

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private List<Map<String, Timestamp>> breaks = new ArrayList<>();
    private String userId;
    private String sessionId;
    private OnBreakDeletedListener onBreakDeletedListener;

    public BreakAdapter(OnBreakDeletedListener onBreakDeletedListener) {
        this.onBreakDeletedListener = onBreakDeletedListener;
    }

    @NonNull
    @Override
    public BreakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_break, parent, false);
        return new BreakViewHolder(view);
    }

    /*
    @Override
    public void onBindViewHolder(@NonNull BreakViewHolder holder, int position) {
        Map<String, Timestamp> breakEntry = breaks.get(position);

        Timestamp breakStart = breakEntry.get("breakStart");
        Timestamp breakEnd = breakEntry.get("breakEnd");

        holder.breakStartTextView.setText(breakStart != null ? timeFormat.format(breakStart.toDate()) : "Not set");
        holder.breakEndTextView.setText(breakEnd != null ? timeFormat.format(breakEnd.toDate()) : "Not set");

        holder.deleteBreakButton.setOnClickListener(v -> {
            FirestoreUtil.deleteBreak(userId, sessionId, holder.getAdapterPosition());
            notifyItemRemoved(position);
            onBreakDeletedListener.onBreakDeleted();
        });
    }
    */
    @Override
    public void onBindViewHolder(@NonNull BreakViewHolder holder, int position) {
        Map<String, Timestamp> breakEntry = breaks.get(position);

        Timestamp breakStart = breakEntry.get("breakStart");
        Timestamp breakEnd = breakEntry.get("breakEnd");

        holder.breakStartTextView.setText(breakStart != null ? timeFormat.format(breakStart.toDate()) : "Not set");
        holder.breakEndTextView.setText(breakEnd != null ? timeFormat.format(breakEnd.toDate()) : "Not set");

        holder.deleteBreakButton.setOnClickListener(v -> {
            FirestoreUtil.deleteBreak(userId, sessionId, position, new FirestoreUtil.OnDeleteBreakListener() {
                @Override
                public void onSuccess() {
                    if (onBreakDeletedListener != null) {
                        onBreakDeletedListener.onBreakDeleted();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Failed to delete break", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return breaks.size();
    }

    public void setBreaks(List<Map<String, Timestamp>> breaks) {
        this.breaks = breaks;
        notifyDataSetChanged();
    }

    static class BreakViewHolder extends RecyclerView.ViewHolder {
        TextView breakStartTextView;
        TextView breakEndTextView;
        Button deleteBreakButton;

        BreakViewHolder(@NonNull View itemView) {
            super(itemView);
            breakStartTextView = itemView.findViewById(R.id.breakStartTextView);
            breakEndTextView = itemView.findViewById(R.id.breakEndTextView);
            deleteBreakButton = itemView.findViewById(R.id.deleteBreakButton);
        }
    }
}