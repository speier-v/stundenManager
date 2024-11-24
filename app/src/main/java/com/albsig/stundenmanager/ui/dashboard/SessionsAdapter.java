package com.albsig.stundenmanager.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.domain.model.session.SessionModel;

import java.util.ArrayList;
import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionViewHolder> {

    private final List<SessionModel> sessionList;
    private String uid;
    private OnSessionClickListener listener;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public interface OnSessionClickListener {
        void onItemDelete(String uid, String documentId);

        void onItemClick(String uid, String documentId);
    }

    public SessionsAdapter(OnSessionClickListener listener) {
        this.sessionList = new ArrayList<SessionModel>();
        this.listener = listener;
    }

    public void clearListener() { this.listener = null; }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<SessionModel> newSessionDates) {
        sessionList.clear();
        sessionList.addAll(newSessionDates);
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
        SessionModel session = sessionList.get(position);
        String date = Helpers.FSTimestampToDateString(session.getStartTime());
        holder.dateTextView.setText(date);
        initListener(holder, session);
    }

    private void initListener(SessionViewHolder holder, SessionModel session) {
        holder.itemView.setOnClickListener(v -> listener.onItemClick(uid, session.getDocumentId()) );
        holder.deleteButton.setOnClickListener(v -> listener.onItemDelete(uid, session.getDocumentId()) );
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
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
