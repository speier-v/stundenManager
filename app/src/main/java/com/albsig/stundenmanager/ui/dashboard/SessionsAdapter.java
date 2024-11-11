package com.albsig.stundenmanager.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.ui.time.DetailTimeActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionViewHolder> {

    private List<String> sessionDates;
    private List<String> sessionIds;
    private String userId;
    private Context context;

    public SessionsAdapter(Context context, List<String> sessionDates, List<String> sessionIds, String userId) {
        this.context = context;
        this.sessionDates = sessionDates;
        this.sessionIds = sessionIds;
        this.userId = userId;
    }

    public void updateData(List<String> newSessionDates, List<String> newSessionIds) {
        sessionDates.clear();
        sessionIds.clear();
        sessionDates.addAll(newSessionDates);
        sessionIds.addAll(newSessionIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session_date, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        String date = sessionDates.get(position);
        String sessionId = sessionIds.get(position);

        holder.dateTextView.setText(date);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailTimeActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("sessionId", sessionId);
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("sessions")
                    .document(sessionId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(v.getContext(), "Session deleted", Toast.LENGTH_SHORT).show();
                        sessionDates.remove(position);
                        sessionIds.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Failed to delete session", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return sessionDates.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        Button deleteButton;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
