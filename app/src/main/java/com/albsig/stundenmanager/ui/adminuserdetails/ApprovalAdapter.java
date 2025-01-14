package com.albsig.stundenmanager.ui.adminuserdetails;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ApprovalViewHolder> {

    private final List<VIModel> viList;
    private OnUserApprovalClickListener listener;

    public interface OnUserApprovalClickListener {
        void onUserApprovalUpdate(String approvalType, String uid, String docId);
    }

    public ApprovalAdapter(OnUserApprovalClickListener listener) {
        this.viList = new ArrayList<VIModel>();
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<VIModel> viModelList) {
        viList.clear();
        viList.addAll(viModelList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user_details_approval, parent, false);
        return new ApprovalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalViewHolder holder, int position) {
        VIModel viElement = viList.get(position);
        holder.viType.setText(viElement.getType());
        String sDTxt = "From "+Helpers.FSTimestampToDateString(viElement.getStartDate());
        String eDTxt = "To "+Helpers.FSTimestampToDateString(viElement.getEndDate());
        holder.tvStartDate.setText(sDTxt);
        holder.tvEndDate.setText(eDTxt);
        holder.btnApproval.setOnClickListener(view -> {
            listener.onUserApprovalUpdate(Constants.APPROVAL_STATUS_APPROVED, viElement.getUid(), viElement.getDocId());
        });
        holder.btnDeny.setOnClickListener(view -> {
            listener.onUserApprovalUpdate(Constants.APPROVAL_STATUS_DENIED, viElement.getUid(), viElement.getDocId());
        });
    }

    @Override
    public int getItemCount() {
        return viList.size();
    }

    public static class ApprovalViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView viType;
        MaterialTextView tvStartDate;
        MaterialTextView tvEndDate;
        MaterialButton btnApproval;
        MaterialButton btnDeny;

        public ApprovalViewHolder(@NonNull View itemView) {
            super(itemView);
            viType = itemView.findViewById(R.id.viType);
            tvStartDate = itemView.findViewById(R.id.viStartDate);
            tvEndDate = itemView.findViewById(R.id.viEndDate);
            btnApproval = itemView.findViewById(R.id.approveButton);
            btnDeny = itemView.findViewById(R.id.deleteButton);
        }
    }
}

