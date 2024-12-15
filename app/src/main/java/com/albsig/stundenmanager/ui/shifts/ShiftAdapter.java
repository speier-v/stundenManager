package com.albsig.stundenmanager.ui.shifts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.domain.model.session.Shift;

import java.util.List;

public class ShiftAdapter extends RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder> {

    private List<Shift> shiftList;

    public ShiftAdapter(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shift, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        Shift shift = shiftList.get(position);

        holder.shiftTypeTextView.setText(shift.getShiftType());
        holder.startDateTextView.setText(shift.getStartDate());
        holder.endDateTextView.setText(shift.getEndDate());
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }

    public static class ShiftViewHolder extends RecyclerView.ViewHolder {
        TextView shiftTypeTextView;
        TextView startDateTextView;
        TextView endDateTextView;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            shiftTypeTextView = itemView.findViewById(R.id.text_shift_type);
            startDateTextView = itemView.findViewById(R.id.text_start_date);
            endDateTextView = itemView.findViewById(R.id.text_end_date);
        }
    }
}

