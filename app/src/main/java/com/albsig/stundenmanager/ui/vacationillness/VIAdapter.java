package com.albsig.stundenmanager.ui.vacationillness;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.albsig.stundenmanager.domain.model.session.ShiftModel;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class VIAdapter extends RecyclerView.Adapter<VIAdapter.ShiftViewHolder> {

    private final List<VIModel> viList;

    public VIAdapter() {
        this.viList = new ArrayList<VIModel>();
    }

    public VIAdapter(List<VIModel> shiftList) {
        this.viList = shiftList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<VIModel> viModelList) {
        viList.clear();
        viList.addAll(viModelList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vacation_illness, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        VIModel viElement = viList.get(position);
        holder.viType.setText(viElement.getType());
        String sDTxt = "From "+Helpers.FSTimestampToDateString(viElement.getStartDate());
        String eDTxt = "To "+Helpers.FSTimestampToDateString(viElement.getEndDate());
        holder.tvStartDate.setText(sDTxt);
        holder.tvEndDate.setText(eDTxt);
        holder.tvApproval.setText(viElement.getApproval());
    }

    @Override
    public int getItemCount() {
        return viList.size();
    }

    public static class ShiftViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView viType;
        MaterialTextView tvStartDate;
        MaterialTextView tvEndDate;
        MaterialTextView tvApproval;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            viType = itemView.findViewById(R.id.viType);
            tvStartDate = itemView.findViewById(R.id.viStartDate);
            tvEndDate = itemView.findViewById(R.id.viEndDate);
            tvApproval = itemView.findViewById(R.id.tvApproval);
        }
    }
}

