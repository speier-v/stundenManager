package com.albsig.stundenmanager.domain.model.session;

import com.albsig.stundenmanager.common.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShiftModel implements Serializable {
    private String id;
    private String shiftType; // Morning, Late, or Night
    private String startDate;
    private String endDate;
    private Date start;
    private Date end;

    public ShiftModel() {}

    public ShiftModel(String id, String shiftType, String startDate, String endDate) {
        this.id = id;
        this.shiftType = shiftType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ShiftModel(String shiftType, DocumentSnapshot document) {
        this.id = document.getId();
        this.shiftType = shiftType;

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
        this.start = document.getTimestamp("startDate").toDate();
        this.end = document.getTimestamp("endDate").toDate();

        this.startDate = "Shiftplan gültig ab: "+ dateFormat.format(start);
        this.endDate = "Shiftplan endet um: "+ dateFormat.format(end);
    }

    public String getId() {
        return id;
    }

    public String getShiftType() {
        return shiftType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getDurationInMinutes() {
        if (start == null || end == null) {
            return 0;
        }

        long durationMillis = end.getTime() - start.getTime();
        return (int) (durationMillis / (1000 * 60));
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}
