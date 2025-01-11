package com.albsig.stundenmanager.ui.shiftplanner;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private final List<UserModel> workerList;
    private OnItemSelected listener;
    private int shift;

    public interface OnItemSelected {
        void onWorkerSelected(int shift, UserModel worker);
        void onWorkerDeselected(int shift, UserModel worker);
    }

    public WorkerAdapter(OnItemSelected listener) {
        this.workerList = new ArrayList<>();
        this.listener = listener;
        this.shift = -1;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public void clearListener() {
        this.listener = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<UserModel> workerList) {
        this.workerList.clear();
        this.workerList.addAll(workerList);
        notifyDataSetChanged();
    }

    public List<UserModel> getWorkerList() {
        return workerList;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pick_worker, parent, false);
        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        UserModel userModel = workerList.get(position);
        String detail =  userModel.getName() + ", " + userModel.getSurname();
        holder.workerName.setText(detail);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                listener.onWorkerSelected(shift, userModel);
            } else {
                listener.onWorkerDeselected(shift, userModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    static class WorkerViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView workerName;
        MaterialCheckBox checkBox;

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            workerName = itemView.findViewById(R.id.titleWorkerName);
            checkBox = itemView.findViewById(R.id.checkboxPicked);
        }
    }
}
