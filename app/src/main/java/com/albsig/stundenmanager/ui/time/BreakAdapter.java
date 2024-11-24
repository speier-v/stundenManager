package com.albsig.stundenmanager.ui.time;

import android.annotation.SuppressLint;
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
import com.albsig.stundenmanager.domain.model.session.BreakModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BreakAdapter extends RecyclerView.Adapter<BreakAdapter.BreakViewHolder> {

    public interface OnBreakClickListener {
        void onBreakDeleted(BreakModel breakModel);
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private List<BreakModel> breaks;
    private OnBreakClickListener onBreakClickListener;

    public BreakAdapter(OnBreakClickListener onBreakClickListener) {
        this.onBreakClickListener = onBreakClickListener;
        breaks = new ArrayList<>();
    }

    @NonNull
    @Override
    public BreakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_break, parent, false);
        return new BreakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BreakViewHolder holder, int position) {
        BreakModel breakEntry = breaks.get(position);

        Timestamp breakStart = breakEntry.getBreakStart();
        Timestamp breakEnd = breakEntry.getBreakEnd();

        holder.breakStartTextView.setText(breakStart != null ? timeFormat.format(breakStart.toDate()) : "Not set");
        holder.breakEndTextView.setText(breakEnd != null ? timeFormat.format(breakEnd.toDate()) : "Not set");

        holder.deleteBreakButton.setOnClickListener(v -> {onBreakClickListener.onBreakDeleted(breakEntry); });
    }

    @Override
    public int getItemCount() {
        return breaks.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBreaks(List<BreakModel> breaks) {
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