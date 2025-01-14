package com.albsig.stundenmanager.ui.adminworkeradministration;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.ui.dashboard.SessionsAdapter;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class UserAdministrationAdapter extends RecyclerView.Adapter<UserAdministrationAdapter.ShiftViewHolder> {

    private List<UserModel> userModelList;

    public UserAdministrationAdapter() {
        this.userModelList = new ArrayList<>();
    }

    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserModel userModel);
    }

    public UserAdministrationAdapter(OnUserClickListener listener) {
        this.listener = listener;
        this.userModelList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<UserModel> userModelList) {
        this.userModelList.clear();
        this.userModelList.addAll(userModelList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_administration, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        String fullName = userModel.getName() + " " + userModel.getSurname();
        holder.workerName.setText(fullName);
        holder.itemView.setOnClickListener(view -> { listener.onUserClick(userModel); });
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public static class ShiftViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView workerName;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            workerName = itemView.findViewById(R.id.workerName);
        }
    }
}

