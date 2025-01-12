package com.albsig.stundenmanager.ui.shiftplanner;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.WorkerModel;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private final List<WorkerModel> workerList;
    private OnItemSelected listener;
    private int shift;

    public interface OnItemSelected {
        void onWorkerSelected(WorkerModel worker);
        void onWorkerDeselected(WorkerModel worker);
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
    public void updateList(List<WorkerModel> workerList) {
        this.workerList.clear();
        this.workerList.addAll(workerList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pick_worker, parent, false);
        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        WorkerModel worker = workerList.get(position);
        String detail =  worker.getName() + ", " + worker.getSurname();
        holder.workerName.setText(detail);
        holder.shiftPicked.setText("-");

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked && shift == worker.getShift()) {
                listener.onWorkerDeselected(worker);
                worker.setShift(Constants.NO_SHIFT_SELECTED);
                holder.shiftPicked.setText("-");
                return;
            }

            if(!isChecked && shift != worker.getShift() && shift != Constants.NO_SHIFT_SELECTED) {
                return;
            }

            if (shift == Constants.MORNING_SHIFT) {
                holder.shiftPicked.setText("M");
                worker.setShift(Constants.MORNING_SHIFT);
            } else if (shift == Constants.LATE_SHIFT) {
                holder.shiftPicked.setText("L");
                worker.setShift(Constants.LATE_SHIFT);
            } else if (shift == Constants.NIGHT_SHIFT) {
                holder.shiftPicked.setText("N");
                worker.setShift(Constants.NIGHT_SHIFT);
            }

            listener.onWorkerSelected(worker);
        });
    }

    @Override
    public int getItemCount() {
        return workerList.size();
    }

    static class WorkerViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView workerName;
        MaterialCheckBox checkBox;
        MaterialTextView shiftPicked;

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            workerName = itemView.findViewById(R.id.titleWorkerName);
            checkBox = itemView.findViewById(R.id.checkboxPicked);
            shiftPicked = itemView.findViewById(R.id.shiftPicked);

        }
    }
}
